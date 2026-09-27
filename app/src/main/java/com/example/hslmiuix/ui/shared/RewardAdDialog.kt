package com.example.hslmiuix.ui.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hslmiuix.data.Mission
import kotlinx.coroutines.delay
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.CircularProgressIndicator
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * 模拟「看广告得积分」：5 秒倒计时后手动领取
 * （对应原 App 的 onAdRewardArrived → /acc/score/score-send 流程）
 *
 * 注意：Miuix 弹层组件必须位于 Scaffold 内容内部才会渲染。
 */
@Composable
fun RewardAdDialog(
    mission: Mission,
    onDone: () -> Unit,
    onCancel: () -> Unit,
) {
    var leftSeconds by remember { mutableIntStateOf(5) }
    LaunchedEffect(Unit) {
        while (leftSeconds > 0) {
            delay(1000L)
            leftSeconds--
        }
    }
    OverlayDialog(
        show = true,
        title = mission.title,
        summary = "模拟广告播放中（refId=${mission.refId}）· 完成得 ${mission.points} 积分",
        onDismissRequest = onCancel,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leftSeconds > 0) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    size = 28.dp,
                    strokeWidth = 3.dp,
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "${leftSeconds} 秒后可领取奖励",
                    style = MiuixTheme.textStyles.main,
                )
            } else {
                Text(
                    text = "广告已看完，点击领取奖励",
                    style = MiuixTheme.textStyles.main,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(
                text = "放弃",
                onClick = onCancel,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(20.dp))
            TextButton(
                text = "领取奖励",
                onClick = onDone,
                modifier = Modifier.weight(1f),
                enabled = leftSeconds == 0,
                colors = ButtonDefaults.textButtonColorsPrimary(),
            )
        }
    }
}
