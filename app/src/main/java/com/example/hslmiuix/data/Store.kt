package com.example.hslmiuix.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

/**
 * Demo 用全局状态仓库（内存版）。
 * 接入真实后端时，把这些方法替换为 API 调用即可（对应原 App 的
 * /dev/start、/dev/end、/acc/score/score-send 等接口的语义）。
 */
object Store {

    // ===== 账号 =====
    var loggedIn by mutableStateOf(false)
        private set
    var phone by mutableStateOf("")
        private set
    var nickname by mutableStateOf("同学")
        private set

    /** 余额（元），演示用 */
    var balance by mutableFloatStateOf(28.50f)
        private set

    /** 积分：1000 积分 = 1 元代金券（沿用原 App 换算） */
    var points by mutableIntStateOf(3260)
        private set

    val couponYuan: Double get() = points / 1000.0

    var signedInToday by mutableStateOf(false)
        private set

    /** 是否使用代金券抵扣（用积分抵当前消费） */
    var useCoupon by mutableStateOf(true)

    /** 充值档位（元）：演示原 App「充值满赠」 */
    val rechargeTiers = listOf(10.0, 30.0, 50.0, 100.0, 200.0)

    /** 充值赠送规则：满 30 送 3，满 50 送 8，满 100 送 20，满 200 送 50 */
    fun rechargeBonus(amount: Double): Double = when {
        amount >= 200 -> 50.0
        amount >= 100 -> 20.0
        amount >= 50 -> 8.0
        amount >= 30 -> 3.0
        else -> 0.0
    }

    /** 充值（演示直接入账，真实场景应对接微信支付回调） */
    fun recharge(amount: Double) {
        balance += (amount + rechargeBonus(amount)).toFloat()
    }

    /** 外观模式：0 跟随系统 / 1 浅色 / 2 深色 */
    var appearance by mutableIntStateOf(0)
        private set

    fun setAppearance(mode: Int) {
        appearance = mode
    }

    fun login(phone: String): Boolean {
        if (phone.length != 11) return false
        this.phone = phone
        this.nickname = "用户${phone.takeLast(4)}"
        loggedIn = true
        return true
    }

    fun logout() {
        loggedIn = false
    }

    /** 完成任务发积分（真实场景应服务端发放，参考原 App score-send + nativeSign） */
    fun addPoints(mission: Mission) {
        points += mission.points
    }

    /** 积分商城兑换：直接扣积分（演示） */
    fun redeem(costPoints: Int): Boolean {
        if (points < costPoints) return false
        points -= costPoints
        return true
    }

    fun signIn(): Boolean {
        if (signedInToday) return false
        signedInToday = true
        points += 20
        return true
    }

    fun spend(amountYuan: Double): Boolean {
        // 先用代金券（积分）抵扣，剩余扣余额
        if (useCoupon && couponYuan >= amountYuan) {
            points -= (amountYuan * 1000).toInt()
            return true
        }
        if (balance + couponYuan.toFloat() >= amountYuan.toFloat()) {
            if (useCoupon && couponYuan > 0) {
                val usePoints = minOf(points, (amountYuan * 1000).toInt())
                points -= usePoints
                balance -= (usePoints / 1000.0).toFloat()
            }
            balance -= amountYuan.toFloat()
            if (balance < 0f) balance = 0f
            return true
        }
        return false
    }

    // ===== 设备使用会话（模拟 /dev/start → 状态轮询 → /dev/end） =====

    var deviceState by mutableStateOf(DeviceState.OFF)
        private set

    /** 已用秒数 */
    var elapsedSeconds by mutableIntStateOf(0)
        private set

    /** 本次会话预估费用（元） */
    val runningCost: Double get() = elapsedSeconds / 60.0 * (currentDevice?.pricePerMinute ?: 0.0)

    var currentDevice by mutableStateOf<DeviceCategory?>(null)
        private set

    /** 使用记录（对应原 App 订单/用水记录） */
    val history = mutableStateListOf<UsageRecord>().apply { addAll(mockHistory) }

    private fun recordUsage(device: DeviceCategory, seconds: Int, cost: Double) {
        history.add(0, UsageRecord(device.name, seconds, cost))
        if (history.size > 30) history.removeAt(history.lastIndex)
    }

    /** 启动会话：模拟设备 PREPARE → WORK */
    suspend fun startDevice(device: DeviceCategory) {
        currentDevice = device
        elapsedSeconds = 0
        deviceState = DeviceState.STARTING
        delay(2000) // 模拟启动准备（原 App 淋浴 prepare 阶段）
        if (deviceState == DeviceState.STARTING) deviceState = DeviceState.RUNNING
    }

    /** 运行计时协程在设备页用 snapshotFlow/LaunchedEffect 驱动 */
    fun tick() {
        if (deviceState == DeviceState.RUNNING) elapsedSeconds++
    }

    /** 结束会话：模拟 flush/结算，返回结算金额；余额不足返回 -1 */
    suspend fun endDevice(): Double {
        if (deviceState != DeviceState.RUNNING && deviceState != DeviceState.STARTING) return 0.0
        deviceState = DeviceState.SETTLING
        delay(1200) // 模拟原 App 结束后 FLUSH 冲洗/结算等待
        val cost = String.format("%.2f", runningCost).toDouble()
        val device = currentDevice
        val seconds = elapsedSeconds
        if (cost > 0 && !spend(cost)) {
            deviceState = DeviceState.OFF
            return -1.0
        }
        if (device != null && cost > 0) recordUsage(device, seconds, cost)
        deviceState = DeviceState.OFF
        return cost
    }
}
