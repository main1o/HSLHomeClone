package com.example.hslmiuix.ui.device

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import com.example.hslmiuix.data.DeviceCategory
import com.example.hslmiuix.data.DeviceState
import com.example.hslmiuix.data.Store
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CircularProgressIndicator
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.SnackbarHost
import top.yukonga.miuix.kmp.basic.SnackbarHostState
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.basic.ArrowRight
import top.yukonga.miuix.kmp.icon.extended.Unlock
import top.yukonga.miuix.kmp.icon.extended.VolumeOff
import top.yukonga.miuix.kmp.icon.extended.VolumeUp
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * 设备详情 + 使用计费页：
 * 待机 → 启动确认 → 启动中(准备) → 运行中(计时计费) → 结束确认 → 结算中 → 扣费
 * （模拟原 App /dev/start → 状态轮询 → /dev/end 全流程）
 */
@Composable
fun DeviceScreen(
    category: DeviceCategory,
    onBack: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showStartDialog by remember { mutableStateOf(false) }
    var showEndDialog by remember { mutableStateOf(false) }
    var settleCost by remember { mutableStateOf<Double?>(null) }
    var temperature by remember { mutableFloatStateOf(0.6f) }

    val state = Store.deviceState
    val running = state == DeviceState.RUNNING
    val busy = state == DeviceState.STARTING || state == DeviceState.SETTLING

    // 运行中每秒 tick（计费计时）
    LaunchedEffect(state) {
        while (Store.deviceState == DeviceState.RUNNING) {
            delay(1000L)
            Store.tick()
        }
    }

    val minutes = Store.elapsedSeconds / 60
    val seconds = Store.elapsedSeconds % 60
    val timeText = "%02d:%02d".format(minutes, seconds)

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = category.name,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = MiuixIcons.ArrowRight,
                            contentDescription = "返回",
                            modifier = Modifier.rotate(180f),
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            ) {
                // ===== 状态卡 =====
                item(key = "status") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(88.dp)
                                    .background(
                                        if (running) MiuixTheme.colorScheme.primaryContainer
                                        else MiuixTheme.colorScheme.secondaryContainer,
                                        RoundedCornerShape(28.dp),
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (busy) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(36.dp),
                                        size = 36.dp,
                                        strokeWidth = 4.dp,
                                    )
                                } else {
                                    Icon(
                                        imageVector = category.icon,
                                        contentDescription = category.name,
                                        modifier = Modifier.size(40.dp),
                                        tint = if (running) {
                                            MiuixTheme.colorScheme.onPrimaryContainer
                                        } else {
                                            MiuixTheme.colorScheme.onSecondaryContainer
                                        },
                                    )
                                }
                            }
                            Spacer(Modifier.height(14.dp))
                            Text(
                                text = when {
                                    running -> "正在使用 · $timeText"
                                    state == DeviceState.STARTING -> "设备启动中，请稍候…"
                                    state == DeviceState.SETTLING -> "结算中…"
                                    else -> "选择时间，即开即用"
                                },
                                style = MiuixTheme.textStyles.headline1,
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "单价 %.2f 元/分钟 · 当前已用 %.2f 元".format(
                                    category.pricePerMinute, Store.runningCost,
                                ),
                                style = MiuixTheme.textStyles.body2,
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            )
                            if (running) {
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    text = timeText,
                                    style = MiuixTheme.textStyles.headline1.copy(
                                        fontSize = 34.sp,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    color = MiuixTheme.colorScheme.primary,
                                )
                            }
                        }
                    }
                }

                // ===== 参数设置 =====
                if (category.type.code == 5 || category.type.code == 6 || category.type.code == 8) {
                    item(key = "temp") {
                        SmallTitle(text = "水温调节（演示）")
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = MiuixIcons.VolumeOff,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                    )
                                    Slider(
                                        value = temperature,
                                        onValueChange = { temperature = it },
                                        valueRange = 0f..1f,
                                        steps = 5,
                                        enabled = !busy,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 10.dp),
                                    )
                                    Icon(
                                        imageVector = MiuixIcons.VolumeUp,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                    )
                                }
                                Text(
                                    text = "目标水温 ${(30 + temperature * 30).toInt()} ℃",
                                    style = MiuixTheme.textStyles.body2,
                                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                )
                            }
                        }
                    }
                }

                // ===== 计费说明 =====
                item(key = "rules") {
                    SmallTitle(text = "计费说明")
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            RuleRow("计费方式", "按使用时长计费，不足 1 分钟按 1 分钟计")
                            RuleRow("代金券", "1000 积分 = 1 元，开启自动抵扣后优先使用")
                            RuleRow("当前资产", "积分 ${Store.points} · 余额 %.2f 元".format(Store.balance))
                        }
                    }
                }

                // ===== 操作按钮 =====
                item(key = "action") {
                    Spacer(Modifier.height(10.dp))
                    if (state == DeviceState.OFF) {
                        Button(
                            onClick = { showStartDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColorsPrimary(),
                        ) {
                            Icon(imageVector = MiuixIcons.Unlock, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("启动设备")
                        }
                    } else if (busy) {
                        Button(
                            onClick = {},
                            enabled = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColorsPrimary(),
                        ) {
                            Text(if (state == DeviceState.STARTING) "启动中…" else "结算中…")
                        }
                    } else {
                        Button(
                            onClick = { showEndDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColorsPrimary(),
                        ) {
                            Text("结束使用并结算")
                        }
                        Spacer(Modifier.height(8.dp))
                        TextButton(
                            text = "继续用一会儿",
                            onClick = {},
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }

            // ===== 弹窗层（必须位于 Scaffold 内部） =====
            if (showStartDialog) {
                OverlayDialog(
                    show = true,
                    title = "启动「${category.name}」",
                    summary = "单价 %.2f 元/分钟，结束后自动从代金券/余额扣费。请确认设备附近无人使用。".format(
                        category.pricePerMinute,
                    ),
                    onDismissRequest = { showStartDialog = false },
                ) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                        TextButton(
                            text = "取消",
                            onClick = { showStartDialog = false },
                            modifier = Modifier.weight(1f),
                        )
                        Spacer(Modifier.width(20.dp))
                        TextButton(
                            text = "确认启动",
                            onClick = {
                                showStartDialog = false
                                scope.launch { Store.startDevice(category) }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary(),
                        )
                    }
                }
            }

            if (showEndDialog) {
                OverlayDialog(
                    show = true,
                    title = "结束使用",
                    summary = "已使用 $timeText，预计费用 %.2f 元。${category.name} 将进入结算。".format(
                        Store.runningCost,
                    ),
                    onDismissRequest = { showEndDialog = false },
                ) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                        TextButton(
                            text = "再想想",
                            onClick = { showEndDialog = false },
                            modifier = Modifier.weight(1f),
                        )
                        Spacer(Modifier.width(20.dp))
                        TextButton(
                            text = "结束并结算",
                            onClick = {
                                showEndDialog = false
                                scope.launch {
                                    val cost = Store.endDevice()
                                    when {
                                        cost < 0 -> snackbarHostState.showSnackbar(
                                            "余额不足，请前往首页做任务赚积分或充值后重试",
                                        )
                                        else -> settleCost = cost
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary(),
                        )
                    }
                }
            }

            val cost = settleCost
            if (cost != null) {
                OverlayDialog(
                    show = true,
                    title = "结算完成",
                    summary = "本次使用「${category.name}」$timeText，扣费 %.2f 元。".format(cost),
                    onDismissRequest = { settleCost = null },
                ) {
                    TextButton(
                        text = "完成",
                        onClick = {
                            settleCost = null
                            onBack()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColorsPrimary(),
                    )
                }
            }
        }
    }
}

@Composable
private fun RuleRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MiuixTheme.textStyles.body2,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        )
        Text(
            text = value,
            style = MiuixTheme.textStyles.body2,
            modifier = Modifier.padding(start = 16.dp),
        )
    }
}
