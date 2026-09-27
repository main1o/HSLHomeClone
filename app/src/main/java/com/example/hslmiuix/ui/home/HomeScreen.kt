package com.example.hslmiuix.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hslmiuix.ui.shared.RewardAdDialog
import com.example.hslmiuix.data.DeviceCategory
import com.example.hslmiuix.data.Mission
import com.example.hslmiuix.data.MissionType
import com.example.hslmiuix.data.Store
import com.example.hslmiuix.data.mockBanners
import com.example.hslmiuix.data.mockDevices
import com.example.hslmiuix.data.mockMissions
import com.example.hslmiuix.data.mockStations
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.SnackbarHost
import top.yukonga.miuix.kmp.basic.SnackbarHostState
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Contacts
import top.yukonga.miuix.kmp.icon.extended.Home
import top.yukonga.miuix.kmp.icon.extended.Location
import top.yukonga.miuix.kmp.icon.extended.Notes
import top.yukonga.miuix.kmp.icon.extended.Scan
import top.yukonga.miuix.kmp.icon.extended.Settings
import top.yukonga.miuix.kmp.icon.extended.Show
import top.yukonga.miuix.kmp.icon.extended.Tasks
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun HomeScreen(
    onOpenDevice: (DeviceCategory) -> Unit,
    onOpenMine: () -> Unit,
    onOpenTasks: () -> Unit,
) {
    val scrollBehavior = MiuixScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var adMission by remember { mutableStateOf<Mission?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = "慧家生活",
                largeTitle = "${Store.nickname}，欢迎回来",
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButtonLite(onClick = onOpenMine)
                },
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = MiuixIcons.Home,
                    label = "首页",
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onOpenTasks,
                    icon = MiuixIcons.Tasks,
                    label = "任务",
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onOpenMine,
                    icon = MiuixIcons.Contacts,
                    label = "我的",
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding() + 12.dp,
            ),
        ) {
            // ===== 账户卡：积分 / 代金券 / 余额 =====
            item(key = "account") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = "当前 ${Store.points} 积分",
                                style = MiuixTheme.textStyles.headline1,
                            )
                            Text(
                                text = "可抵 %.2f 元代金券 · 余额 %.2f 元".format(Store.couponYuan, Store.balance),
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
                    SwitchPreference(
                        title = "消费时代金券自动抵扣",
                        summary = "1000 积分 = 1 元",
                        checked = Store.useCoupon,
                        onCheckedChange = { Store.useCoupon = it },
                    )
                }
            }

            // ===== 轮播 Banner =====
            item(key = "banner") {
                BannerPager()
            }

            // ===== 扫码 / 碰一碰 =====
            item(key = "entry") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    EntryButton(
                        text = "扫码用水",
                        icon = MiuixIcons.Scan,
                        modifier = Modifier.weight(1f),
                    ) {
                        scope.launch { snackbarHostState.showSnackbar("扫码为演示入口：请在下方选择设备") }
                    }
                    EntryButton(
                        text = "碰一碰(NFC)",
                        icon = MiuixIcons.Show,
                        modifier = Modifier.weight(1f),
                    ) {
                        scope.launch { snackbarHostState.showSnackbar("NFC 为演示入口：请靠近设备标签") }
                    }
                }
            }

            // ===== 我的设备（宫格） =====
            item(key = "devices") {
                SmallTitle(text = "我的设备")
                DeviceGrid(onOpenDevice)
                Spacer(Modifier.height(4.dp))
            }

            // ===== 免费用水 · 积分任务 =====
            item(key = "missions") {
                SmallTitle(text = "免费用水 · 做任务得积分")
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
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

            // ===== 推广位 =====
            item(key = "adslot") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .height(90.dp)
                        .background(
                            MiuixTheme.colorScheme.secondaryContainer,
                            RoundedCornerShape(20.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "推广位（演示）",
                            style = MiuixTheme.textStyles.main,
                            color = MiuixTheme.colorScheme.onSecondaryContainer,
                        )
                        Text(
                            text = "原 App 此处为 GroMore/Taku 聚合广告",
                            style = MiuixTheme.textStyles.body2,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        )
                    }
                }
            }

            // ===== 附近站点 =====
            item(key = "stations") {
                SmallTitle(text = "附近站点")
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    mockStations.forEach { station ->
                        BasicComponent(
                            title = station.name,
                            summary = "${station.distanceMeters} m · 空闲 ${station.available}",
                            startAction = {
                                Icon(
                                    modifier = Modifier.padding(end = 16.dp),
                                    imageVector = MiuixIcons.Location,
                                    contentDescription = null,
                                    tint = MiuixTheme.colorScheme.onBackground,
                                )
                            },
                            onClick = {
                                scope.launch {
                                    snackbarHostState.showSnackbar("导航到「${station.name}」（演示）")
                                }
                            },
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }

        // 模拟激励视频任务（Miuix OverlayDialog 必须位于 Scaffold 内部）
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
        }
    }
}

/** TopAppBar 右上角设置入口 */
@Composable
private fun IconButtonLite(onClick: () -> Unit) {
    top.yukonga.miuix.kmp.basic.IconButton(onClick = onClick) {
        Icon(
            imageVector = MiuixIcons.Settings,
            contentDescription = "我的",
            tint = MiuixTheme.colorScheme.onSurface,
        )
    }
}

/** 扫码/NFC 大入口按钮 */
@Composable
private fun EntryButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    top.yukonga.miuix.kmp.basic.Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColorsPrimary(),
    ) {
        Icon(imageVector = icon, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(text)
    }
}

@Composable
private fun BannerPager() {
    val pagerState = rememberPagerState(pageCount = { mockBanners.size })
    LaunchedEffect(pagerState) {
        while (true) {
            delay(4000L)
            val next = (pagerState.currentPage + 1) % mockBanners.size
            pagerState.animateScrollToPage(next)
        }
    }
    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .height(140.dp),
    ) { page ->
        val banner = mockBanners[page]
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(banner.color, RoundedCornerShape(24.dp)),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = banner.title,
                    style = MiuixTheme.textStyles.headline1,
                    color = androidx.compose.ui.graphics.Color.White,
                )
                Text(
                    text = banner.subtitle,
                    style = MiuixTheme.textStyles.body2,
                    color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.85f),
                )
            }
            Text(
                text = "${page + 1}/${mockBanners.size}",
                style = MiuixTheme.textStyles.body2,
                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp),
            )
        }
    }
}

@Composable
private fun DeviceGrid(onOpenDevice: (DeviceCategory) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
    ) {
        mockDevices.chunked(4).forEach { rowDevices ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                rowDevices.forEach { device ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onOpenDevice(device) },
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .background(
                                    MiuixTheme.colorScheme.primaryContainer,
                                    RoundedCornerShape(16.dp),
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = device.icon,
                                contentDescription = device.name,
                                tint = MiuixTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = device.name,
                            style = MiuixTheme.textStyles.body2,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                // 补齐空位
                repeat(4 - rowDevices.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}
