package com.example.hslmiuix.ui.tasks

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.example.hslmiuix.data.Mission
import com.example.hslmiuix.data.MissionType
import com.example.hslmiuix.data.Store
import com.example.hslmiuix.data.mockMissions
import com.example.hslmiuix.ui.shared.RewardAdDialog
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.SnackbarHost
import top.yukonga.miuix.kmp.basic.SnackbarHostState
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.basic.ArrowRight
import top.yukonga.miuix.kmp.icon.extended.FavoritesFill
import top.yukonga.miuix.kmp.icon.extended.Link
import top.yukonga.miuix.kmp.icon.extended.Notes
import top.yukonga.miuix.kmp.icon.extended.Play
import top.yukonga.miuix.kmp.icon.extended.Show
import top.yukonga.miuix.kmp.icon.extended.GridView
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme

/** 积分商城兑换项（演示数据） */
private data class MallItem(
    val id: Int,
    val name: String,
    val desc: String,
    val costPoints: Int,
)

private val mockMallItems = listOf(
    MallItem(101, "直饮水 5 元券", "满 5 元立减，7 天有效", 5000),
    MallItem(102, "洗衣免单券", "单次洗衣机免费使用", 8000),
    MallItem(103, "淋浴 10 分钟体验", "赠送 10 分钟淋浴时长", 3000),
    MallItem(104, "周边帆布袋", "校园限定款，线下自提", 20000),
)

/**
 * 积分任务 & 商城页：任务列表 + 兑换列表，完整闭环「看广告/签到 → 得积分 → 兑换/抵扣」。
 */
@Composable
fun TasksScreen(onBack: () -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var adMission by remember { mutableStateOf<Mission?>(null) }
    var redeemItem by remember { mutableStateOf<MallItem?>(null) }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = "任务与商城",
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
                // ===== 积分总览 =====
                item(key = "header") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    ) {
                        Row(
                            modifier = Modifier.padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = MiuixIcons.FavoritesFill,
                                contentDescription = null,
                                modifier = Modifier.size(30.dp),
                                tint = MiuixTheme.colorScheme.primary,
                            )
                            Spacer(Modifier.width(14.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = "${Store.points} 积分",
                                    style = MiuixTheme.textStyles.headline1,
                                )
                                Text(
                                    text = "可抵 %.2f 元 · 1000 积分 = 1 元".format(Store.couponYuan),
                                    style = MiuixTheme.textStyles.body2,
                                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                )
                            }
                            TextButton(
                                text = if (Store.signedInToday) "已签到" else "签到 +20",
                                onClick = {
                                    val ok = Store.signIn()
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            if (ok) "签到成功，积分 +20" else "今天已经签过啦",
                                        )
                                    }
                                },
                                enabled = !Store.signedInToday,
                                colors = ButtonDefaults.textButtonColorsPrimary(),
                            )
                        }
                    }
                }

                // ===== 任务列表 =====
                item(key = "missions") {
                    SmallTitle(text = "今日任务")
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    ) {
                        mockMissions.forEach { mission ->
                            BasicComponent(
                                title = mission.title,
                                summary = "${mission.desc}（+${mission.points} 分）",
                                startAction = {
                                    Icon(
                                        modifier = Modifier.padding(end = 16.dp),
                                        imageVector = when (mission.type) {
                                            MissionType.WATCH_AD -> MiuixIcons.Play
                                            MissionType.SIGN_IN -> MiuixIcons.Notes
                                            MissionType.FULL_AD -> MiuixIcons.Show
                                            MissionType.THIRD_LINK -> MiuixIcons.Link
                                        },
                                        contentDescription = null,
                                        tint = MiuixTheme.colorScheme.primary,
                                    )
                                },
                                endActions = {
                                    Text(
                                        text = if (mission.type == MissionType.SIGN_IN && Store.signedInToday) {
                                            "已完成"
                                        } else {
                                            "去完成"
                                        },
                                        style = MiuixTheme.textStyles.button,
                                        color = MiuixTheme.colorScheme.primary,
                                    )
                                },
                                onClick = {
                                    when (mission.type) {
                                        MissionType.SIGN_IN -> {
                                            val ok = Store.signIn()
                                            scope.launch {
                                                snackbarHostState.showSnackbar(
                                                    if (ok) "签到成功，积分 +20" else "今天已经签过啦",
                                                )
                                            }
                                        }
                                        else -> adMission = mission
                                    }
                                },
                            )
                        }
                    }
                }

                // ===== 商城列表 =====
                item(key = "mall") {
                    SmallTitle(text = "积分商城")
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    ) {
                        mockMallItems.forEach { item ->
                            BasicComponent(
                                title = item.name,
                                summary = item.desc,
                                startAction = {
                                    Icon(
                                        modifier = Modifier.padding(end = 16.dp),
                                        imageVector = MiuixIcons.GridView,
                                        contentDescription = null,
                                        tint = MiuixTheme.colorScheme.primary,
                                    )
                                },
                                endActions = {
                                    Text(
                                        text = "${item.costPoints} 分",
                                        style = MiuixTheme.textStyles.button,
                                        color = if (Store.points >= item.costPoints) {
                                            MiuixTheme.colorScheme.primary
                                        } else {
                                            MiuixTheme.colorScheme.disabledOnSecondaryVariant
                                        },
                                    )
                                },
                                onClick = {
                                    if (Store.points < item.costPoints) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                "积分不足，还差 ${item.costPoints - Store.points} 分",
                                            )
                                        }
                                    } else {
                                        redeemItem = item
                                    }
                                },
                            )
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                }
            }

            // ===== 弹窗层（必须在 Scaffold 内） =====
            val mission = adMission
            if (mission != null) {
                RewardAdDialog(
                    mission = mission,
                    onDone = {
                        Store.addPoints(mission)
                        adMission = null
                        scope.launch {
                            snackbarHostState.showSnackbar("奖励到账：积分 +${mission.points}")
                        }
                    },
                    onCancel = { adMission = null },
                )
            }

            val item = redeemItem
            if (item != null) {
                OverlayDialog(
                    show = true,
                    title = "确认兑换",
                    summary = "使用 ${item.costPoints} 积分兑换「${item.name}」？兑换后积分不可退回。",
                    onDismissRequest = { redeemItem = null },
                ) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                        TextButton(
                            text = "再想想",
                            onClick = { redeemItem = null },
                            modifier = Modifier.weight(1f),
                        )
                        Spacer(Modifier.width(20.dp))
                        TextButton(
                            text = "兑换",
                            onClick = {
                                redeemItem = null
                                Store.redeem(item.costPoints)
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "兑换成功：「${item.name}」已发放至券包（演示）",
                                    )
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary(),
                        )
                    }
                }
            }
        }
    }
}
