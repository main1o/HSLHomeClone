package com.example.hslmiuix.ui.history

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hslmiuix.data.Store
import com.example.hslmiuix.data.UsageRecord
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.extended.Timer
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * 使用记录（对应原 App 订单 / 用水记录页）
 */
@Composable
fun HistoryScreen(onBack: () -> Unit) {
    val total = Store.history.sumOf { it.costYuan }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = "使用记录",
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
                item(key = "summary") {
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
                                imageVector = MiuixIcons.Timer,
                                contentDescription = null,
                                modifier = Modifier.size(26.dp),
                                tint = MiuixTheme.colorScheme.primary,
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = "共 ${Store.history.size} 条记录",
                                    style = MiuixTheme.textStyles.main,
                                )
                                Text(
                                    text = "累计消费 %.2f 元（含积分抵扣）".format(total),
                                    style = MiuixTheme.textStyles.body2,
                                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                )
                            }
                        }
                    }
                }

                item(key = "list") {
                    SmallTitle(text = "最近使用")
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    ) {
                        if (Store.history.isEmpty()) {
                            Text(
                                text = "暂无记录，去首页选择一台设备开始使用吧",
                                style = MiuixTheme.textStyles.body2,
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                modifier = Modifier.padding(20.dp),
                            )
                        } else {
                            Store.history.forEach { record ->
                                RecordRow(record = record)
                            }
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun RecordRow(record: UsageRecord) {
    val minutes = record.seconds / 60
    val seconds = record.seconds % 60
    BasicComponent(
        title = record.deviceName,
        summary = "使用 %02d:%02d · 扣费 %.2f 元".format(minutes, seconds, record.costYuan),
        startAction = {
            Icon(
                modifier = Modifier.padding(end = 16.dp),
                imageVector = MiuixIcons.Timer,
                contentDescription = null,
                tint = MiuixTheme.colorScheme.onSurfaceVariantActions,
            )
        },
        endActions = {
            Text(
                text = "%.2f 元".format(record.costYuan),
                style = MiuixTheme.textStyles.button,
                color = MiuixTheme.colorScheme.onSurface,
            )
        },
    )
}
