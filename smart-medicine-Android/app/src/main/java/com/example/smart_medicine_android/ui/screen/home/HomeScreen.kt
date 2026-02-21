package com.example.smart_medicine_android.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import com.example.smart_medicine_android.data.network.model.IllnessDto
import com.example.smart_medicine_android.data.network.model.MedicineDto
import com.example.smart_medicine_android.ui.theme.*

/**
 * 首页 - 带标签切换
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onIllnessClick: (Int) -> Unit,
    onConsultationClick: () -> Unit,
    onProfileClick: () -> Unit,
    onMedicineClick: (Int) -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    // 搜索类型和选中标签页同步：0=疾病, 1=药品
    var searchType by remember { mutableIntStateOf(0) }
    val tabs = listOf("热门疾病", "常用药品")

    // 当搜索类型改变时，同时也改变选中的标签页
    fun onSearchTypeChange(newType: Int) {
        searchType = newType
        // 清空搜索
        viewModel.search("")
    }

    // 当标签页改变时，同时也改变搜索类型
    fun onTabChange(index: Int) {
        searchType = index
    }

    SmartMedicineTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                HomeTopBar(
                    onConsultationClick = onConsultationClick
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(
                    top = paddingValues.calculateTopPadding(),
                    bottom = 80.dp // 为底部导航栏留出空间
                )
            ) {
                // ==================== 搜索栏（带类型筛选）====================
                item {
                    SearchBarWithFilter(
                        query = uiState.searchQuery,
                        searchType = searchType,
                        onQueryChange = { viewModel.search(it, searchType) },
                        onSearchTypeChange = { onSearchTypeChange(it) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // ==================== 标签切换 ====================
                item {
                    TabRow(
                        selectedTabIndex = searchType,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        containerColor = Color.Transparent,
                        divider = {}
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = searchType == index,
                                onClick = { onTabChange(index) },
                                text = {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = if (searchType == index) FontWeight.Bold else FontWeight.Normal,
                                        color = if (searchType == index) PrimaryBlue else TextSecondary
                                    )
                                }
                            )
                        }
                    }
                }

                // ==================== 内容区域 ====================
                when {
                    uiState.isLoading -> {
                        item { LoadingState() }
                    }
                    !uiState.searchQuery.isBlank() -> {
                        // 搜索结果 - 根据搜索类型显示
                        when (searchType) {
                            0 -> {
                                // 疾病搜索结果
                                if (uiState.illnesses.isEmpty()) {
                                    item { EmptyState(message = "未找到相关疾病") }
                                } else {
                                    item {
                                        SectionHeader(
                                            title = "疾病搜索结果",
                                            subtitle = "${uiState.illnesses.size} 个结果"
                                        )
                                    }
                                    items(uiState.illnesses.chunked(2)) { chunk ->
                                        DiseaseGridRow(
                                            diseases = chunk,
                                            onItemClick = onIllnessClick
                                        )
                                    }
                                }
                            }
                            1 -> {
                                // 药品搜索结果
                                if (uiState.medicines.isEmpty()) {
                                    item { EmptyState(message = "未找到相关药品") }
                                } else {
                                    item {
                                        SectionHeader(
                                            title = "药品搜索结果",
                                            subtitle = "${uiState.medicines.size} 个结果"
                                        )
                                    }
                                    items(uiState.medicines.chunked(2)) { chunk ->
                                        MedicineGridRow(
                                            medicines = chunk,
                                            onItemClick = onMedicineClick
                                        )
                                    }
                                }
                            }
                        }
                    }
                    else -> {
                        // 正常模式 - 根据 searchType 显示内容
                        when (searchType) {
                            0 -> {
                                // 热门疾病
                                if (uiState.illnesses.isEmpty()) {
                                    item { EmptyState(message = "暂无疾病数据") }
                                } else {
                                    item {
                                        SectionHeader(
                                            title = "热门疾病",
                                            subtitle = "${uiState.illnesses.size} 个推荐"
                                        )
                                    }
                                    items(uiState.illnesses.chunked(2)) { chunk ->
                                        DiseaseGridRow(
                                            diseases = chunk,
                                            onItemClick = onIllnessClick
                                        )
                                    }
                                }
                            }
                            1 -> {
                                // 常用药品
                                if (uiState.medicines.isEmpty()) {
                                    item { EmptyState(message = "暂无药品数据") }
                                } else {
                                    item {
                                        SectionHeader(
                                            title = "常用药品",
                                            subtitle = "${uiState.medicines.size} 个推荐"
                                        )
                                    }
                                    items(uiState.medicines.chunked(2)) { chunk ->
                                        MedicineGridRow(
                                            medicines = chunk,
                                            onItemClick = onMedicineClick
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ==================== 错误提示 ====================
                uiState.errorMessage?.let { error ->
                    item {
                        ErrorSnackBar(
                            message = error,
                            onDismiss = viewModel::clearError,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

// ==================== 顶部栏 ====================

@Composable
private fun HomeTopBar(onConsultationClick: () -> Unit) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = PrimaryBlue.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.LocalHospital,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Text(
                    text = "智慧医疗",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }
        },
        actions = {
            IconButton(onClick = onConsultationClick) {
                BadgedBox(badge = { Badge() }) {
                    Icon(
                        imageVector = Icons.Outlined.MailOutline,
                        contentDescription = "消息",
                        tint = TextSecondary
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = TextPrimary
        )
    )
}

// ==================== 搜索栏（带类型筛选）====================

@Composable
private fun SearchBarWithFilter(
    query: String,
    searchType: Int,
    onQueryChange: (String) -> Unit,
    onSearchTypeChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilterMenu by remember { mutableStateOf(false) }
    val searchTypes = listOf("疾病", "药品")

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .shadow(4.dp, RoundedCornerShape(26.dp), spotColor = CardShadow),
        shape = RoundedCornerShape(26.dp),
        color = BackgroundSecondary
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 类型筛选按钮
            Box {
                Surface(
                    onClick = { showFilterMenu = true },
                    shape = RoundedCornerShape(20.dp),
                    color = PrimaryBlue.copy(alpha = 0.1f),
                    modifier = Modifier.height(36.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = searchTypes[searchType],
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = PrimaryBlue
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "选择类型",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                DropdownMenu(
                    expanded = showFilterMenu,
                    onDismissRequest = { showFilterMenu = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    searchTypes.forEachIndexed { index, type ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = type,
                                    color = if (searchType == index) PrimaryBlue else TextPrimary
                                )
                            },
                            onClick = {
                                onSearchTypeChange(index)
                                showFilterMenu = false
                            },
                            trailingIcon = if (searchType == index) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = PrimaryBlue
                                    )
                                }
                            } else null
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "搜索",
                tint = TextTertiary,
                modifier = Modifier.size(20.dp)
            )
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 4.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (query.isEmpty()) {
                            Text(
                                text = "搜索${searchTypes[searchType]}名称...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextTertiary
                            )
                        }
                        innerTextField()
                    }
                },
                cursorBrush = SolidColor(TextPrimary),
                maxLines = 1
            )
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "清除",
                        tint = TextTertiary
                    )
                }
            }
        }
    }
}

// ==================== 区块标题 ====================

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = TextTertiary
            )
        }
    }
}

// ==================== 疾病网格行 ====================

@Composable
private fun DiseaseGridRow(
    diseases: List<IllnessDto>,
    onItemClick: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        diseases.forEach { disease ->
            disease.id?.let { id ->
                Box(modifier = Modifier.weight(1f)) {
                    DiseaseCard(disease = disease, onClick = { onItemClick(id) })
                }
            }
        }
        // 补齐空白
        repeat(2 - diseases.size) {
            Box(modifier = Modifier.weight(1f)) {}
        }
    }
    Spacer(modifier = Modifier.height(12.dp))
}

// ==================== 药品网格行 ====================

@Composable
private fun MedicineGridRow(
    medicines: List<MedicineDto>,
    onItemClick: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        medicines.forEach { medicine ->
            medicine.id?.let { id ->
                Box(modifier = Modifier.weight(1f)) {
                    MedicineCard(medicine = medicine, onClick = { onItemClick(id) })
                }
            }
        }
        // 补齐空白
        repeat(2 - medicines.size) {
            Box(modifier = Modifier.weight(1f)) {}
        }
    }
    Spacer(modifier = Modifier.height(12.dp))
}

// ==================== 疾病卡片 ====================

@Composable
private fun DiseaseCard(
    disease: IllnessDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(6.dp, RoundedCornerShape(16.dp), spotColor = CardShadow),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = disease.illnessName ?: "未知疾病",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = CircleShape,
                    color = SuccessGreen.copy(alpha = 0.15f),
                    modifier = Modifier.size(8.dp)
                ) {}
            }
            disease.illnessSymptom?.let { symptom ->
                Text(
                    text = symptom,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = disease.kindName ?: "疾病",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "查看详情",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ==================== 药品卡片 ====================

@Composable
private fun MedicineCard(
    medicine: MedicineDto,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(6.dp, RoundedCornerShape(16.dp), spotColor = CardShadow),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 药品图片
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = PrimaryBlue.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (!medicine.imgPath.isNullOrEmpty()) {
                        val painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(context)
                                .data(medicine.imgPath)
                                .crossfade(true)
                                .build()
                        )
                        if (painter.state is AsyncImagePainter.State.Error) {
                            Icon(
                                imageVector = Icons.Outlined.Medication,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(medicine.imgPath)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = medicine.medicineName,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Medication,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
            Text(
                text = medicine.medicineName ?: "未知药品",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            medicine.medicineEffect?.let { effect ->
                Text(
                    text = effect,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                medicine.medicinePrice?.let { price ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = WarningOrange.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "¥$price",
                            style = MaterialTheme.typography.labelSmall,
                            color = WarningOrange,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } ?: Spacer(modifier = Modifier.width(40.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "查看详情",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ==================== 加载状态 ====================

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Text(
                text = "加载中...",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

// ==================== 空状态 ====================

@Composable
private fun EmptyState(message: String = "暂无数据") {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = TextTertiary
        )
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
    }
}

// ==================== 错误提示 ====================

@Composable
private fun ErrorSnackBar(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Snackbar(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        containerColor = ErrorRed,
        contentColor = Color.White,
        action = {
            TextButton(onClick = onDismiss) {
                Text("关闭", color = Color.White)
            }
        }
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Text(message)
        }
    }
}
