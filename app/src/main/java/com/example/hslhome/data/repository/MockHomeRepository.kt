package com.example.hslhome.data.repository

import com.example.hslhome.data.model.AccountSummary
import com.example.hslhome.data.model.Banner
import com.example.hslhome.data.model.DeviceCategory
import com.example.hslhome.data.model.HomeFeed
import com.example.hslhome.data.model.Mission
import com.example.hslhome.data.model.MissionType
import com.example.hslhome.data.model.Station
import kotlinx.coroutines.delay

/**
 * 首页数据仓库（Mock 实现）。
 *
 * 真实项目里把这个实现替换成 Retrofit API + 你自己的合法后端即可，
 * 上层 ViewModel 只依赖 [HomeDataSource] 接口，不关心数据来源。
 */
interface HomeDataSource {
    suspend fun loadHome(): HomeFeed
    suspend fun addPointsFromMission(mission: Mission): Int
}

class MockHomeRepository : HomeDataSource {

    override suspend fun loadHome(): HomeFeed {
        // 模拟网络耗时
        delay(600)
        return HomeFeed(
            account = AccountSummary(nickname = "用户体验者", validPoints = 3600),
            banners = listOf(
                Banner("b1", "新用户首单立减 5 元"),
                Banner("b2", "夏季洗澡特惠"),
                Banner("b3", "邀请好友得积分")
            ),
            categories = listOf(
                DeviceCategory(5, "直饮水", 3),
                DeviceCategory(6, "淋浴", 2),
                DeviceCategory(8, "饮水机", 5),
                DeviceCategory(10, "洗衣", 4),
                DeviceCategory(80, "烘干", 1),
                DeviceCategory(20, "吹风机", 6),
                DeviceCategory(30, "售货机", 0),
                DeviceCategory(120, "充电桩", 2),
                DeviceCategory(50, "门禁", 0)
            ),
            missions = listOf(
                Mission("1705776998", "看视频得积分", 200, MissionType.WATCH_AD),
                Mission("sign_in", "每日签到", 100, MissionType.SIGN_IN),
                Mission("popsreen", "浏览插屏广告", 80, MissionType.FULL_AD),
                Mission("linkshangou", "逛逛特惠专区", 150, MissionType.THIRD_LINK, validTimeSec = 5)
            ),
            stations = listOf(
                Station("s1", "东区 3 号楼水房", 120, 4),
                Station("s2", "图书馆负一层浴室", 340, 2),
                Station("s3", "北区体育馆洗衣房", 560, 6)
            )
        )
    }

    /**
     * 模拟"完成任务发积分"。真实场景需要：
     * 1) 广告 SDK 回调奖励到达；
     * 2) 由【服务端】校验并发放积分（客户端只上报事件）。
     * 这里为演示直接返回增加后的余额。
     */
    override suspend fun addPointsFromMission(mission: Mission): Int {
        delay(300)
        return mission.points
    }
}
