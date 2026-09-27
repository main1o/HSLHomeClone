package com.example.hslhome.data.model

/** 首页顶部轮播 */
data class Banner(
    val id: String,
    val title: String,
    val imageUrl: String? = null
)

/** 设备分类宫格项，如 直饮水 / 淋浴 / 洗衣 */
data class DeviceCategory(
    val code: Int,
    val name: String,
    val availableCount: Int = 0
)

/** 附近站点 */
data class Station(
    val id: String,
    val name: String,
    val distanceMeters: Int,
    val freeCount: Int
)

/** 积分任务（看广告 / 签到 / 跳外链等） */
data class Mission(
    val refId: String,
    val name: String,
    val points: Int,
    val type: MissionType,
    val validTimeSec: Int = 3
)

enum class MissionType { WATCH_AD, SIGN_IN, FULL_AD, THIRD_LINK }

/** 用户账户概要（积分/代金券） */
data class AccountSummary(
    val nickname: String,
    val validPoints: Int
) {
    /** 1000 积分 = 1 元代金券（示例换算规则，可自行调整） */
    val couponYuan: String
        get() = String.format("%.2f", validPoints / 1000.0)
}

/** 首页聚合数据 */
data class HomeFeed(
    val account: AccountSummary,
    val banners: List<Banner>,
    val categories: List<DeviceCategory>,
    val missions: List<Mission>,
    val stations: List<Station>
)
