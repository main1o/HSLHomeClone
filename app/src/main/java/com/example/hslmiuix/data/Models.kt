package com.example.hslmiuix.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Alarm
import top.yukonga.miuix.kmp.icon.extended.CloudFill
import top.yukonga.miuix.kmp.icon.extended.Favorites
import top.yukonga.miuix.kmp.icon.extended.Refresh
import top.yukonga.miuix.kmp.icon.extended.SearchDevice
import top.yukonga.miuix.kmp.icon.extended.Settings
import top.yukonga.miuix.kmp.icon.extended.Timer
import top.yukonga.miuix.kmp.icon.extended.VolumeUp

/** 设备品类（对应原 App 的设备类型编码，如淋浴=6、直饮水=5） */
enum class DeviceType(val code: Int) {
    PURIFIER(5),   // 直饮水
    SHOWER(6),     // 淋浴
    WATER(8),      // 饮水机
    WASHER(3),     // 洗衣
    DRYER(4),      // 烘干
    HAIR_DRYER(9), // 吹风机
    CHARGER(12),   // 充电桩
    VENDING(15),   // 售货机
}

/** 设备使用状态（模拟原 App DeviceStatus：OFF0/ON1/WORK10/FLUSH20/PREPARE30） */
enum class DeviceState(val label: String) {
    OFF("待机"),
    STARTING("启动中"),
    RUNNING("运行中"),
    SETTLING("结算中"),
}

data class DeviceCategory(
    val type: DeviceType,
    val name: String,
    val icon: ImageVector,
    /** 单价，元/分钟（淋浴、洗衣按时间计费演示） */
    val pricePerMinute: Double,
)

data class Station(
    val name: String,
    val distanceMeters: Int,
    val available: Int,
)

/** 使用记录（演示） */
data class UsageRecord(
    val deviceName: String,
    val seconds: Int,
    val costYuan: Double,
)

data class Banner(
    val title: String,
    val subtitle: String,
    val color: Color,
)

enum class MissionType { WATCH_AD, SIGN_IN, FULL_AD, THIRD_LINK }

/** 积分任务（refId 沿用原 App 常量语义：激励视频 1705776998、插屏 popsreen） */
data class Mission(
    val id: Int,
    val title: String,
    val desc: String,
    val points: Int,
    val refId: String,
    val type: MissionType,
)

val mockDevices = listOf(
    DeviceCategory(DeviceType.WATER, "直饮水", MiuixIcons.CloudFill, 0.12),
    DeviceCategory(DeviceType.SHOWER, "淋浴", MiuixIcons.VolumeUp, 0.30),
    DeviceCategory(DeviceType.PURIFIER, "饮水机", MiuixIcons.Timer, 0.08),
    DeviceCategory(DeviceType.WASHER, "洗衣", MiuixIcons.Refresh, 1.20),
    DeviceCategory(DeviceType.DRYER, "烘干", MiuixIcons.Alarm, 0.80),
    DeviceCategory(DeviceType.HAIR_DRYER, "吹风机", MiuixIcons.Favorites, 0.20),
    DeviceCategory(DeviceType.CHARGER, "充电桩", MiuixIcons.Settings, 0.40),
    DeviceCategory(DeviceType.VENDING, "售货机", MiuixIcons.SearchDevice, 0.0),
)

val mockStations = listOf(
    Station("1 号楼东侧水房", 86, 4),
    Station("2 号楼淋浴间", 210, 2),
    Station("中心食堂洗衣房", 350, 6),
)

val mockBanners = listOf(
    Banner("新用户首单免单", "直饮水首次使用 0 元体验", Color(0xFF1FB67A)),
    Banner("充值满 50 送 8", "多充多送，随用随扣", Color(0xFF3482FF)),
    Banner("暑期洗衣节", "洗衣烘干 8.8 折", Color(0xFFFF8A3D)),
)

val mockMissions = listOf(
    Mission(1, "看视频得积分", "观看激励视频，奖励立即到账", 50, "1705776998", MissionType.WATCH_AD),
    Mission(2, "每日签到", "每天签到一次，连续有惊喜", 20, "sign", MissionType.SIGN_IN),
    Mission(3, "看全屏广告", "完成观看即可获得奖励", 40, "popsreen", MissionType.FULL_AD),
    Mission(4, "逛一逛合作页面", "浏览 15 秒得积分", 30, "linkshangou", MissionType.THIRD_LINK),
)

/** 初始历史（演示） */
val mockHistory = listOf(
    UsageRecord("直饮水", 42, 0.08),
    UsageRecord("洗衣", 38 * 60, 45.60),
    UsageRecord("淋浴", 6 * 60, 1.80),
)
