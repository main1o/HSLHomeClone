package com.example.hslmiuix.ui.mine

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.foundation.background
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hslmiuix.data.Store
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
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.extended.Contacts
import top.yukonga.miuix.kmp.icon.extended.Favorites
import top.yukonga.miuix.kmp.icon.extended.GridView
import top.yukonga.miuix.kmp.icon.extended.Help
import top.yukonga.miuix.kmp.icon.extended.Info
import top.yukonga.miuix.kmp.icon.extended.Lock
import top.yukonga.miuix.kmp.icon.extended.Refresh
import top.yukonga.miuix.kmp.icon.extended.Tasks
import top.yukonga.miuix.kmp.icon.extended.Theme
import top.yukonga.miuix.kmp.icon.extended.Timer
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.preference.OverlayDropdownPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * 我的 / 设置页：账户信息 + 偏好项（Miuix preference 组件展示）
 */
@Composable
fun MineScreen(
    onBack: () -> Unit,
    onOpenTasks: () -> Unit,
    onOpenHistory: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showLogoutConfirm by remember { mutableStateOf(false) }
    var showAbout by remember { mutableStateOf(false) }
    var showRecharge by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = "我的",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = MiuixIcons.Back,
                            contentDescription = "返回",
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
                // ===== 账户卡 =====
                item(key = "account") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    ) {
                        Row(
                            modifier = Modifier.padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(
                                        MiuixTheme.colorScheme.primaryContainer,
                                        RoundedCornerShape(28.dp),
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = MiuixIcons.Contacts,
                                    contentDescription = null,
                                    tint = MiuixTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(28.dp),
                                )
                            }
                            Spacer(Modifier.width(14.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = Store.nickname,
                                    style = MiuixTheme.textStyles.headline1,
                                )
                                Text(
                                    text = Store.phone,
                                    style = MiuixTheme.textStyles.body2,
                                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp)
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            MetricCell("积分", Store.points.toString())
                            MetricCell("代金券", "%.2f 元".format(Store.couponYuan))
                            MetricCell("余额", "%.2f 元".format(Store.balance))
                        }
                        TextButton(
                            text = "余额充值（满 30 送 3 · 满 50 送 8）",
                            onClick = { showRecharge = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp),
                            colors = ButtonDefaults.textButtonColorsPrimary(),
                        )
                    }
                }

                // ===== 消费与抵扣 =====
                item(key = "pref_pay") {
                    SmallTitle(text = "消费与抵扣")
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    ) {
                        SwitchPreference(
                            title = "代金券自动抵扣",
                            summary = "结算时优先使用积分兑换的代金券",
                            checked = Store.useCoupon,
                            onCheckedChange = { Store.useCoupon = it },
                            startAction = {
                                Icon(
                                    modifier = Modifier.padding(start = 16.dp, end = 12.dp),
                                    imageVector = MiuixIcons.Favorites,
                                    contentDescription = null,
                                    tint = MiuixTheme.colorScheme.primary,
                                )
                            },
                        )
                        ArrowPreference(
                            title = "手动兑换代金券",
                            summary = "每 1000 积分 → 1 元券（演示直接换算）",
                            startAction = {
                                Icon(
                                    modifier = Modifier.padding(start = 16.dp, end = 12.dp),
                                    imageVector = MiuixIcons.Refresh,
                                    contentDescription = null,
                                    tint = MiuixTheme.colorScheme.primary,
                                )
                            },
                            onClick = {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "已换算：当前可用代金券 %.2f 元".format(Store.couponYuan),
                                    )
                                }
                            },
                        )
                    }
                }

                // ===== 外观 =====
                item(key = "pref_theme") {
                    SmallTitle(text = "外观")
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    ) {
                        OverlayDropdownPreference(
                            title = "外观模式",
                            startAction = {
                                Icon(
                                    modifier = Modifier.padding(start = 16.dp, end = 12.dp),
                                    imageVector = MiuixIcons.Theme,
                                    contentDescription = null,
                                    tint = MiuixTheme.colorScheme.primary,
                                )
                            },
                            items = listOf("跟随系统", "浅色", "深色"),
                            selectedIndex = Store.appearance,
                            onSelectedIndexChange = { Store.updateAppearance(it) },
                        )
                    }
                }

                // ===== 关于 =====
                item(key = "pref_about") {
                    SmallTitle(text = "关于")
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    ) {
                        ArrowPreference(
                            title = "使用记录",
                            summary = "共 ${Store.history.size} 条 · 累计 %.2f 元".format(
                                Store.history.sumOf { it.costYuan },
                            ),
                            startAction = {
                                Icon(
                                    modifier = Modifier.padding(start = 16.dp, end = 12.dp),
                                    imageVector = MiuixIcons.Timer,
                                    contentDescription = null,
                                    tint = MiuixTheme.colorScheme.primary,
                                )
                            },
                            onClick = onOpenHistory,
                        )
                        ArrowPreference(
                            title = "任务中心",
                            summary = "看广告 · 签到 · 三方任务赚积分",
                            startAction = {
                                Icon(
                                    modifier = Modifier.padding(start = 16.dp, end = 12.dp),
                                    imageVector = MiuixIcons.Tasks,
                                    contentDescription = null,
                                    tint = MiuixTheme.colorScheme.primary,
                                )
                            },
                            onClick = onOpenTasks,
                        )
                        ArrowPreference(
                            title = "积分商城",
                            summary = "兑换周边 / 免单券（演示占位）",
                            startAction = {
                                Icon(
                                    modifier = Modifier.padding(start = 16.dp, end = 12.dp),
                                    imageVector = MiuixIcons.GridView,
                                    contentDescription = null,
                                    tint = MiuixTheme.colorScheme.primary,
                                )
                            },
                            onClick = {
                                scope.launch { snackbarHostState.showSnackbar("演示版：商城暂未开放") }
                            },
                        )
                        ArrowPreference(
                            title = "帮助与反馈",
                            startAction = {
                                Icon(
                                    modifier = Modifier.padding(start = 16.dp, end = 12.dp),
                                    imageVector = MiuixIcons.Help,
                                    contentDescription = null,
                                    tint = MiuixTheme.colorScheme.primary,
                                )
                            },
                            onClick = {
                                scope.launch {
                                    snackbarHostState.showSnackbar("演示版：反馈通道未接入")
                                }
                            },
                        )
                        ArrowPreference(
                            title = "关于本应用",
                            summary = "HSL Miuix · v1.0.0 (demo)",
                            startAction = {
                                Icon(
                                    modifier = Modifier.padding(start = 16.dp, end = 12.dp),
                                    imageVector = MiuixIcons.Info,
                                    contentDescription = null,
                                    tint = MiuixTheme.colorScheme.primary,
                                )
                            },
                            onClick = { showAbout = true },
                        )
                    }
                }

                // ===== 退出登录 =====
                item(key = "pref_logout") {
                    Spacer(Modifier.height(16.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    ) {
                        ArrowPreference(
                            title = "退出登录",
                            summary = "清除会话返回登录页",
                            startAction = {
                                Icon(
                                    modifier = Modifier.padding(start = 16.dp, end = 12.dp),
                                    imageVector = MiuixIcons.Lock,
                                    contentDescription = null,
                                    tint = MiuixTheme.colorScheme.error,
                                )
                            },
                            onClick = { showLogoutConfirm = true },
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }

            // ===== 弹窗（必须位于 Scaffold 内） =====
            if (showRecharge) {
                OverlayDialog(
                    show = true,
                    title = "余额充值",
                    summary = "当前余额 %.2f 元，选择档位（演示直接入账，不拉起微信支付）".format(Store.balance),
                    onDismissRequest = { showRecharge = false },
                ) {
                    Column {
                        Store.rechargeTiers.forEach { amount ->
                            BasicComponent(
                                title = "充 %.0f 元".format(amount),
                                summary = if (Store.rechargeBonus(amount) > 0) {
                                    "送 %.0f 元，实际到账 %.0f 元".format(
                                        Store.rechargeBonus(amount),
                                        amount + Store.rechargeBonus(amount),
                                    )
                                } else {
                                    "无赠送"
                                },
                                endActions = {
                                    Text(
                                        text = "充值",
                                        style = MiuixTheme.textStyles.button,
                                        color = MiuixTheme.colorScheme.primary,
                                    )
                                },
                                onClick = {
                                    showRecharge = false
                                    Store.recharge(amount)
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            "充值成功，余额 %.2f 元".format(Store.balance),
                                        )
                                    }
                                },
                            )
                        }
                    }
                }
            }

            if (showLogoutConfirm) {
                OverlayDialog(
                    show = true,
                    title = "退出登录",
                    summary = "本地演示数据不会被清除，下次进入需要重新登录。",
                    onDismissRequest = { showLogoutConfirm = false },
                ) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                        TextButton(
                            text = "取消",
                            onClick = { showLogoutConfirm = false },
                            modifier = Modifier.weight(1f),
                        )
                        Spacer(Modifier.width(20.dp))
                        TextButton(
                            text = "退出",
                            onClick = {
                                showLogoutConfirm = false
                                Store.logout()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary(),
                        )
                    }
                }
            }

            if (showAbout) {
                OverlayDialog(
                    show = true,
                    title = "关于 HSL Miuix",
                    summary = "使用 Miuix (top.yukonga.miuix.kmp v0.9.4) 复刻校园「慧生活」常见流程：\n" +
                        "手机号登录 / 首页宫格 / 设备计费 / 积分任务 / 设置偏好。\n" +
                        "全部数据均为本地 Mock，不连接任何后端。",
                    onDismissRequest = { showAbout = false },
                ) {
                    TextButton(
                        text = "知道了",
                        onClick = { showAbout = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColorsPrimary(),
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricCell(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MiuixTheme.textStyles.main,
            color = MiuixTheme.colorScheme.primary,
        )
        Text(
            text = label,
            style = MiuixTheme.textStyles.body2,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        )
    }
}
