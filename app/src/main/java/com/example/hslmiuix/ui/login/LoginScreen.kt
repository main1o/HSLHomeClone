package com.example.hslmiuix.ui.login

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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SnackbarHost
import top.yukonga.miuix.kmp.basic.SnackbarHostState
import top.yukonga.miuix.kmp.basic.Switch
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Phone
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme
import com.example.hslmiuix.data.Store

/**
 * 登录页：手机号 + 验证码（演示验证码固定 1234）
 */
@Composable
fun LoginScreen() {
    var phone by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var agreed by remember { mutableStateOf(false) }
    var countingDown by remember { mutableIntStateOf(0) }
    var showAgreement by remember { mutableStateOf(false) }
    var showFail by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val keyboard = LocalSoftwareKeyboardController.current

    if (countingDown > 0) {
        LaunchedEffect(countingDown) { delay(1000L); countingDown-- }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(72.dp))
                Icon(
                    imageVector = MiuixIcons.CloudFill,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MiuixTheme.colorScheme.primary,
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "慧家生活",
                    style = MiuixTheme.textStyles.headline1,
                )
                Text(
                    text = "直饮水 · 淋浴 · 洗衣 · 充电，一码通行",
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
                Spacer(Modifier.height(36.dp))

                TextField(
                    value = phone,
                    onValueChange = { if (it.length <= 11) phone = it },
                    label = "手机号",
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    leadingIcon = {
                        Icon(
                            imageVector = MiuixIcons.Phone,
                            contentDescription = null,
                            modifier = Modifier.padding(horizontal = 12.dp),
                        )
                    },
                )
                Spacer(Modifier.height(14.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = code,
                        onValueChange = { if (it.length <= 4) code = it },
                        label = "验证码（演示：1234）",
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(Modifier.width(10.dp))
                    TextButton(
                        text = if (countingDown > 0) "${countingDown}s" else "获取验证码",
                        onClick = {
                            if (phone.length != 11) {
                                showFail = "请输入 11 位手机号"
                            } else if (countingDown == 0) {
                                countingDown = 60
                                scope.launch { snackbarHostState.showSnackbar("验证码已发送（演示：1234）") }
                            }
                        },
                        enabled = countingDown == 0,
                        colors = ButtonDefaults.textButtonColorsPrimary(),
                    )
                }
                Spacer(Modifier.height(20.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Switch(
                        checked = agreed,
                        onCheckedChange = { agreed = it },
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "我已阅读并同意 ",
                        style = MiuixTheme.textStyles.body2,
                    )
                    Text(
                        text = "《用户协议》",
                        style = MiuixTheme.textStyles.body2.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = MiuixTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(2.dp)
                            .clickable { showAgreement = true },
                    )
                }
                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        keyboard?.hide()
                        when {
                            !agreed -> scope.launch { snackbarHostState.showSnackbar("请先同意用户协议") }
                            phone.length != 11 -> scope.launch { snackbarHostState.showSnackbar("请输入 11 位手机号") }
                            code != "1234" -> scope.launch { snackbarHostState.showSnackbar("验证码错误（演示验证码为 1234）") }
                            else -> Store.login(phone)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColorsPrimary(),
                ) {
                    Text("登 录")
                }
            }

            // 用户协议/提示弹窗（Miuix OverlayDialog 必须位于 Scaffold 内部）
            if (showAgreement) {
                AgreementDialog { showAgreement = false }
            }
            val fail = showFail
            if (fail != null) {
                OverlayDialog(
                    show = true,
                    title = "提示",
                    summary = fail,
                    onDismissRequest = { showFail = null },
                ) {
                    TextButton(
                        text = "知道了",
                        onClick = { showFail = null },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColorsPrimary(),
                    )
                }
            }
        }
    }
}

@Composable
private fun AgreementDialog(onDismiss: () -> Unit) {
    OverlayDialog(
        show = true,
        title = "用户协议（演示）",
        summary = "本应用为学习用 Demo：数据全部为本地 Mock，不连接任何真实服务端；" +
            "登录验证码固定为 1234，积分与余额仅用于演示「看广告得积分 → 抵扣消费」的闭环。",
        onDismissRequest = onDismiss,
    ) {
        Card {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp),
                modifier = Modifier.height(180.dp),
            ) {
                item {
                    Text(
                        text = "1. 本 Demo 不收集任何个人信息。\n" +
                            "2. 设备启动/结束、计费、积分发放均为前端模拟。\n" +
                            "3. 仅用于研究 UI 框架与交互形态，请勿用于生产。",
                        style = MiuixTheme.textStyles.body2,
                        modifier = Modifier.padding(12.dp),
                    )
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        TextButton(
            text = "关闭",
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.textButtonColorsPrimary(),
        )
    }
}
