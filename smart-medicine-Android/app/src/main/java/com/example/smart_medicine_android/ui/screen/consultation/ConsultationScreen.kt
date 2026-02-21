package com.example.smart_medicine_android.ui.screen.consultation

import androidx.compose.animation.core.KeyframesSpec
import androidx.compose.animation.core.KeyframesSpec.KeyframesSpecConfig
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smart_medicine_android.ui.theme.*

import org.commonmark.node.Block
import org.commonmark.node.BlockQuote
import org.commonmark.node.BulletList
import org.commonmark.node.Code
import org.commonmark.node.Document
import org.commonmark.node.Emphasis
import org.commonmark.node.FencedCodeBlock
import org.commonmark.node.Heading
import org.commonmark.node.IndentedCodeBlock
import org.commonmark.node.Link
import org.commonmark.node.ListItem
import org.commonmark.node.Node
import org.commonmark.node.OrderedList
import org.commonmark.node.Paragraph
import org.commonmark.node.SoftLineBreak
import org.commonmark.node.StrongEmphasis
import org.commonmark.node.Text
import org.commonmark.node.HardLineBreak

import org.commonmark.parser.Parser
import org.commonmark.renderer.text.TextContentRenderer

// 移除 TablesExtension 导入，使用自定义表格解析器避免类名冲突

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * AI 咨询页面 - 千问风格设计
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultationScreen(
    onBackClick: () -> Unit,
    viewModel: ConsultationViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    // 自动滚动到底部
    LaunchedEffect(uiState.history.size) {
        val hasWelcomeCard = uiState.history.isEmpty() && !uiState.isLoading
        val totalItems = (if (hasWelcomeCard) 1 else 0) + uiState.history.size

        if (totalItems > 0) {
            kotlinx.coroutines.delay(100)
            runCatching {
                listState.animateScrollToItem(maxOf(0, totalItems - 1))
            }
        }
    }

    Scaffold(
        topBar = {
            ConsultationTopBar(
                onBackClick = onBackClick,
                onNewConversation = viewModel::showNewConversationDialog
            )
        },
        bottomBar = {
            ConsultationInputBar(
                question = uiState.question,
                onQuestionChange = viewModel::onQuestionChange,
                onSendClick = viewModel::consult,
                isLoading = uiState.isLoading
            )
        },
        containerColor = Color(0xFFF7F8FA)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // 水印背景层
            AIWatermark()

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 欢迎卡片
                if (uiState.history.isEmpty() && !uiState.isLoading) {
                    item {
                        WelcomeCard()
                    }
                }

                // 历史记录（包括正在流式输出的对话）
                // 流式输出时，数据库中的 answer 会实时更新，Flow 会触发 UI 重组
                items(uiState.history, key = { it.id }) { consultation ->
                    // 状态判断：streaming 表示正在流式输出
                    val isStreaming = consultation.status == "streaming"
                    // 空内容时显示加载状态
                    val isLoading = isStreaming && consultation.answer.isNullOrEmpty()
                    // 有内容时显示流式消息（带光标）
                    val hasPartialContent = isStreaming && !consultation.answer.isNullOrEmpty()

                    // 调试日志
                    androidx.compose.runtime.SideEffect {
                        android.util.Log.d("ConsultationScreen", "Item: ${consultation.id}, status=${consultation.status}, answer length=${consultation.answer?.length ?: 0}, isStreaming=$isStreaming, hasPartialContent=$hasPartialContent")
                    }

                    ConsultationItem(
                        question = consultation.question,
                        answer = consultation.answer ?: "",
                        isLoading = isLoading,
                        isStreaming = hasPartialContent,
                        timestamp = consultation.createdAt
                    )
                }
            }

            // 错误提示
            uiState.errorMessage?.let { error ->
                LaunchedEffect(error) {
                    kotlinx.coroutines.delay(3000)
                    viewModel.clearError()
                }
            }
        }
    }

    // 新建会话确认对话框
    if (uiState.showNewConversationDialog) {
        NewConversationDialog(
            onConfirm = viewModel::newConversation,
            onDismiss = viewModel::hideNewConversationDialog
        )
    }
}

// ==================== 顶部栏 ====================

@Composable
private fun ConsultationTopBar(
    onBackClick: () -> Unit,
    onNewConversation: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = "AI 智能咨询",
                fontSize = 19.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A)
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = Color(0xFF1A1A1A)
                )
            }
        },
        actions = {
            IconButton(onClick = onNewConversation) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "新建会话",
                    tint = Color(0xFF1A1A1A)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}

// ==================== 欢迎卡片 ====================

@Composable
private fun WelcomeCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // AI 头像
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = PrimaryBlue.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        // 欢迎文字
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "您好！我是 AI 健康助手",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A)
            )
            Text(
                text = "有什么健康问题可以帮您解答",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF858585)
            )
        }
    }
}

// ==================== 咨询记录项 ====================

@Composable
private fun ConsultationItem(
    question: String,
    answer: String,
    timestamp: Long,
    isLoading: Boolean = false,
    isStreaming: Boolean = false
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 用户消息
        UserMessage(message = question)

        // AI 消息
        when {
            isLoading && answer.isEmpty() -> {
                // 加载中，还没有内容
                LoadingMessage()
            }
            isStreaming && answer.isNotEmpty() -> {
                // 流式输出中，显示带闪烁光标的内容
                StreamingMessage(message = answer)
            }
            answer.isNotEmpty() -> {
                // 完成，显示完整内容
                AIMessage(message = answer)
            }
        }
    }
}

// ==================== 用户消息 ====================

@Composable
private fun UserMessage(message: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Surface(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = RoundedCornerShape(18.dp),
            color = PrimaryBlue.copy(alpha = 0.15f),
            shadowElevation = 0.dp
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = PrimaryBlue
            )
        }
    }
}

// ==================== AI 消息 ====================

@Composable
private fun AIMessage(message: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Column(modifier = Modifier.widthIn(max = 320.dp)) {
            MarkdownText(
                markdown = message,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ==================== 加载中消息 ====================

@Composable
private fun LoadingMessage() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "AI 正在思考",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF858585)
            )
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = PrimaryBlue
            )
        }
    }
}

// ==================== 流式输出消息 ====================

@Composable
private fun StreamingMessage(message: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Row(
            modifier = Modifier.widthIn(max = 320.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // Markdown 内容 - 实时渲染
            MarkdownText(
                markdown = message,
                modifier = Modifier.weight(1f)
            )

            // 闪烁光标
            val infiniteTransition = rememberInfiniteTransition(label = "cursor")
            val alpha by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 0f,
                animationSpec = androidx.compose.animation.core.infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 500
                        0f at 0
                        1f at 250
                        0f at 500
                    }
                ),
                label = "cursor"
            )

            Surface(
                modifier = Modifier.size(4.dp, 18.dp),
                shape = RoundedCornerShape(1.dp),
                color = PrimaryBlue.copy(alpha = alpha)
            ) {}
        }
    }
}

// ==================== 输入栏 ====================

@Composable
private fun ConsultationInputBar(
    question: String,
    onQuestionChange: (String) -> Unit,
    onSendClick: () -> Unit,
    isLoading: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 输入框
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFFF0F1F3)
                ) {
                    BasicTextField(
                        value = question,
                        onValueChange = onQuestionChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        decorationBox = { innerTextField ->
                            if (question.isEmpty()) {
                                Text(
                                    text = "输入您的问题...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF858585)
                                )
                            }
                            innerTextField()
                        },
                        cursorBrush = SolidColor(PrimaryBlue),
                        maxLines = 4
                    )
                }

                // 发送按钮
                IconButton(
                    onClick = onSendClick,
                    enabled = question.isNotBlank() && !isLoading,
                    modifier = Modifier.size(48.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = if (question.isNotBlank() && !isLoading) PrimaryBlue else Color(0xFFE0E0E0)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "发送",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== 新建会话对话框 ====================

@Composable
private fun NewConversationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "新建会话",
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A)
            )
        },
        text = {
            Text(
                text = "确定要清空当前聊天记录并开始新会话吗？",
                color = Color(0xFF666666)
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("确定", color = PrimaryBlue)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消", color = Color(0xFF858585))
            }
        },
        containerColor = Color.White
    )
}

// ==================== Markdown 渲染 ====================

@Composable
private fun MarkdownText(
    markdown: String,
    modifier: Modifier = Modifier
) {
    // 简单的表格解析：检测markdown表格并在CommonMark之前处理
    val tableResult = detectAndParseTable(markdown)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (tableResult != null) {
            // 渲染表格前的内容
            if (tableResult.first.isNotEmpty()) {
                val parser = Parser.builder().build()
                val beforeDoc = parser.parse(tableResult.first)
                renderMarkdownNode(beforeDoc)
            }
            // 渲染表格
            RenderSimpleTable(tableResult.second)
            // 渲染表格后的内容
            if (tableResult.third.isNotEmpty()) {
                val parser = Parser.builder().build()
                val afterDoc = parser.parse(tableResult.third)
                renderMarkdownNode(afterDoc)
            }
        } else {
            // 没有表格，正常解析
            val parser = Parser.builder().build()
            val document = parser.parse(markdown)
            renderMarkdownNode(document)
        }
    }
}

@Composable
private fun renderMarkdownNode(node: Node) {
    when (node) {
        is Paragraph -> {
            Text(
                text = buildAnnotatedString { appendNodeContent(node) },
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF1A1A1A)
            )
        }
        is Heading -> {
            val style = when (node.level) {
                1 -> MaterialTheme.typography.titleLarge
                2 -> MaterialTheme.typography.titleMedium
                3 -> MaterialTheme.typography.titleSmall
                else -> MaterialTheme.typography.bodyLarge
            }
            Text(
                text = buildAnnotatedString { appendNodeContent(node) },
                style = style,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
        is org.commonmark.node.FencedCodeBlock -> {
            Surface(
                color = Color(0xFFF0F1F3),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = node.literal,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF333333),
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
        is org.commonmark.node.IndentedCodeBlock -> {
            Surface(
                color = Color(0xFFF0F1F3),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = node.literal,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF333333),
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
        is BulletList -> {
            var child = node.firstChild
            while (child != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text("•", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF1A1A1A))
                    Column(modifier = Modifier.weight(1f)) {
                        renderMarkdownNode(child)
                    }
                }
                child = child.next
            }
        }
        is OrderedList -> {
            var index = node.startNumber
            var child = node.firstChild
            while (child != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text("$index.", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF1A1A1A))
                    Column(modifier = Modifier.weight(1f)) {
                        renderMarkdownNode(child)
                    }
                }
                index++
                child = child.next
            }
        }
        is BlockQuote -> {
            Surface(
                color = PrimaryBlue.copy(alpha = 0.05f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        modifier = Modifier.width(3.dp).fillMaxHeight(),
                        shape = RoundedCornerShape(1.5.dp),
                        color = PrimaryBlue
                    ) {}
                    Column(modifier = Modifier.weight(1f)) {
                        var child = node.firstChild
                        while (child != null) {
                            Text(
                                text = buildAnnotatedString { appendNodeContent(child) },
                                style = MaterialTheme.typography.bodyMedium,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = Color(0xFF666666)
                            )
                            child = child.next
                        }
                    }
                }
            }
        }
        is Block -> {
            // 处理其他扩展块
            var child = node.firstChild
            while (child != null) {
                renderMarkdownNode(child)
                child = child.next
            }
        }
        else -> {
            var child = node.firstChild
            while (child != null) {
                renderMarkdownNode(child)
                child = child.next
            }
        }
    }
}

// ==================== 简单Markdown表格解析器 ====================

/**
 * 简单表格数据结构
 */
data class SimpleTable(
    val headers: List<String>,
    val rows: List<List<String>>
)

/**
 * 检测并解析Markdown表格
 * 返回: Triple(表格前内容, 表格数据, 表格后内容) 或 null（如果没有表格）
 */
private fun detectAndParseTable(markdown: String): Triple<String, SimpleTable, String>? {
    val lines = markdown.lines()
    var tableStartIndex = -1
    var separatorIndex = -1

    // 查找表格：连续的 | 行，中间有分隔行
    for (i in lines.indices) {
        val line = lines[i].trim()
        if (line.startsWith("|") && line.endsWith("|")) {
            if (tableStartIndex == -1) {
                tableStartIndex = i
            }
            // 检查下一行是否是分隔行
            if (i + 1 < lines.size) {
                val nextLine = lines[i + 1].trim()
                if (nextLine.matches(Regex("\\|?\\s*:?-+:?\\s*(\\|\\s*:?-+:?\\s*)*\\|?"))) {
                    separatorIndex = i + 1
                    break
                }
            }
        } else if (tableStartIndex != -1) {
            // 遇到非表格行，重置
            tableStartIndex = -1
        }
    }

    if (tableStartIndex == -1 || separatorIndex == -1) {
        return null
    }

    // 提取表头
    val headerLine = lines[tableStartIndex]
    val headers = parseTableRow(headerLine)

    // 提取表体行
    val rows = mutableListOf<List<String>>()
    for (i in separatorIndex + 1 until lines.size) {
        val line = lines[i].trim()
        if (line.startsWith("|") && line.endsWith("|")) {
            rows.add(parseTableRow(line))
        } else {
            break
        }
    }

    val tableEndIndex = separatorIndex + rows.size

    // 构建结果
    val beforeContent = lines.take(tableStartIndex).joinToString("\n").trim()
    val afterContent = lines.drop(tableEndIndex + 1).joinToString("\n").trim()

    return Triple(beforeContent, SimpleTable(headers, rows), afterContent)
}

/**
 * 解析表格行
 */
private fun parseTableRow(line: String): List<String> {
    // 移除首尾的 |
    val content = line.trim().removePrefix("|").removeSuffix("|")
    // 按 | 分割，并去除空白
    return content.split("|").map { it.trim() }
}

/**
 * 渲染简单表格
 */
@Composable
private fun RenderSimpleTable(table: SimpleTable) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Color(0xFFE0E0E0)
        )
    ) {
        Column(
            modifier = Modifier.padding(1.dp)
        ) {
            // 表头
            if (table.headers.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PrimaryBlue.copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    table.headers.forEach { header ->
                        MarkdownCellText(
                            markdown = header,
                            modifier = Modifier.weight(1f),
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Divider(color = PrimaryBlue.copy(alpha = 0.3f), thickness = 1.dp)
            }

            // 表体
            table.rows.forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { cell ->
                        MarkdownCellText(
                            markdown = cell,
                            modifier = Modifier.weight(1f),
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF333333),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Divider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)
            }
        }
    }
}

/**
 * 渲染表格单元格内的markdown文本（仅支持行内格式：粗体、斜体、代码、链接）
 */
@Composable
private fun MarkdownCellText(
    markdown: String,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Normal,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null
) {
    val parser = Parser.builder().build()
    val document = parser.parse(markdown)

    Text(
        text = buildAnnotatedString {
            var current = document.firstChild
            while (current != null) {
                appendInlineNodeContent(current, color)
                current = current.next
            }
        },
        modifier = modifier,
        style = MaterialTheme.typography.bodySmall,
        fontWeight = fontWeight,
        color = color,
        textAlign = textAlign
    )
}

/**
 * 追加行内节点内容（用于表格单元格）
 */
private fun androidx.compose.ui.text.AnnotatedString.Builder.appendInlineNodeContent(
    node: Node?,
    defaultColor: Color
) {
    var current = node
    while (current != null) {
        val next = current.next
        when (current) {
            is Text -> {
                append(current.literal)
            }
            is Emphasis -> {
                val child = current.firstChild
                withStyle(SpanStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)) {
                    appendInlineNodeContent(child, defaultColor)
                }
            }
            is StrongEmphasis -> {
                val child = current.firstChild
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    appendInlineNodeContent(child, defaultColor)
                }
            }
            is Code -> {
                val literal = current.literal
                withStyle(
                    SpanStyle(
                        fontFamily = FontFamily.Monospace,
                        background = Color(0xFFE8F4FD),
                        color = PrimaryBlue
                    )
                ) {
                    append(literal)
                }
            }
            is Link -> {
                val child = current.firstChild
                withStyle(
                    SpanStyle(
                        color = PrimaryBlue,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    appendInlineNodeContent(child, defaultColor)
                }
            }
            is Paragraph -> {
                val child = current.firstChild
                appendInlineNodeContent(child, defaultColor)
            }
            else -> {
                val child = current.firstChild
                appendInlineNodeContent(child, defaultColor)
            }
        }
        current = next
    }
}

private fun androidx.compose.ui.text.AnnotatedString.Builder.appendNodeContent(node: Node?) {
    var current = node?.firstChild
    while (current != null) {
        when (current) {
            is Text -> {
                append(current.literal)
            }
            is Emphasis -> {
                withStyle(SpanStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)) {
                    appendNodeContent(current)
                }
            }
            is StrongEmphasis -> {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    appendNodeContent(current)
                }
            }
            is Code -> {
                val codeNode = current
                withStyle(
                    SpanStyle(
                        fontFamily = FontFamily.Monospace,
                        background = Color(0xFFE8F4FD),
                        color = PrimaryBlue
                    )
                ) {
                    append(codeNode.literal)
                }
            }
            is Link -> {
                withStyle(
                    SpanStyle(
                        color = PrimaryBlue,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    appendNodeContent(current)
                }
            }
            is HardLineBreak -> append("\n")
            is SoftLineBreak -> append(" ")
            else -> appendNodeContent(current)
        }
        current = current.next
    }
}

// ==================== AI 水印 ====================

/**
 * AI 咨询水印
 * 显示免责声明，提示用户 AI 内容仅供参考
 */
@Composable
private fun AIWatermark() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        // 水印容器 - 旋转放置
        Box(
            modifier = Modifier
                .rotate(-30f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // 第一行水印
                WatermarkText(
                    text = "AI输出内容仅作为信息参考"
                )
                // 第二行水印
                WatermarkText(
                    text = "不构成医疗诊断、治疗或处方依据"
                )
                // 第三行水印
                WatermarkText(
                    text = "诊疗决策应以具备资质的医务人员为准"
                )
            }
        }
    }
}

/**
 * 水印文字组件
 */
@Composable
private fun WatermarkText(text: String) {
    Text(
        text = text,
        fontSize = 16.sp,
        color = PrimaryBlue.copy(alpha = 0.20f),
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}
