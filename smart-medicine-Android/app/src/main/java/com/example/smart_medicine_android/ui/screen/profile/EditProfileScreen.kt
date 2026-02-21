package com.example.smart_medicine_android.ui.screen.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.smart_medicine_android.data.network.model.UserInfo
import com.example.smart_medicine_android.ui.theme.*
import java.io.File

/**
 * 编辑个人资料屏幕
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onSaveSuccess: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: EditProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var sex by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var avatar by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    // 图片选择器
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                // 获取 MIME 类型
                val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"

                // 根据 MIME 类型确定文件扩展名
                val extension = when {
                    mimeType.contains("png") -> ".png"
                    mimeType.contains("gif") -> ".gif"
                    mimeType.contains("webp") -> ".webp"
                    else -> ".jpg"
                }

                // 将 Uri 转换为 File
                val inputStream = context.contentResolver.openInputStream(uri)
                val tempFile = File.createTempFile("avatar_", extension, context.cacheDir)

                // 复制文件内容
                tempFile.outputStream().use { output ->
                    inputStream?.use { input ->
                        input.copyTo(output)
                    }
                }

                // 上传头像
                viewModel.uploadAvatar(tempFile)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 当用户信息加载完成后，更新表单字段
    LaunchedEffect(uiState.userInfo) {
        uiState.userInfo?.let { userInfo ->
            name = userInfo.userName ?: ""
            age = userInfo.userAge?.toString() ?: ""
            sex = userInfo.userSex ?: ""
            phone = userInfo.userTel ?: ""
            avatar = userInfo.imgPath ?: ""
        }
    }

    // 监听上传的头像URL
    LaunchedEffect(uiState.uploadedAvatarUrl) {
        uiState.uploadedAvatarUrl?.let { url ->
            avatar = url
        }
    }

    // 保存成功后返回
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onSaveSuccess()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "编辑资料",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Surface(
                            modifier = Modifier.size(36.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = BackgroundSecondary
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "返回",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = TextPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
            ) {
            // 头像区域
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 头像显示和选择
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = CircleShape,
                    color = BackgroundSecondary,
                    shadowElevation = 8.dp
                ) {
                    // 可点击的头像区域
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clickable(
                                enabled = !uiState.isUploadingAvatar
                            ) {
                                imagePickerLauncher.launch("image/*")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.isUploadingAvatar) {
                            // 上传中显示进度指示器
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                strokeWidth = 3.dp,
                                color = PrimaryBlue
                            )
                        } else if (avatar.isNotEmpty()) {
                            // 显示实际头像
                            AsyncImage(
                                model = avatar,
                                contentDescription = "头像",
                                modifier = Modifier.size(100.dp),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else {
                            // 显示默认图标
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = TextTertiary
                            )
                        }
                    }
                }
                Text(
                    text = when {
                        uiState.isUploadingAvatar -> "上传中..."
                        uiState.uploadedAvatarUrl != null -> "更换成功，请保存"
                        else -> "点击更换头像"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = when {
                        uiState.uploadedAvatarUrl != null -> Color(0xFF4CAF50)
                        else -> PrimaryBlue
                    }
                )
            }

            // 表单区域
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 姓名
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("姓名") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = uiState.nameError != null,
                        supportingText = uiState.nameError?.let { { Text(it) } },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = null,
                                tint = PrimaryBlue
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            errorBorderColor = ErrorRed
                        )
                    )

                    // 年龄
                    OutlinedTextField(
                        value = age,
                        onValueChange = {
                            if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                age = it
                            }
                        },
                        label = { Text("年龄") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = uiState.ageError != null,
                        supportingText = uiState.ageError?.let { { Text(it) } },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Cake,
                                contentDescription = null,
                                tint = PrimaryBlue
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            errorBorderColor = ErrorRed
                        )
                    )

                    // 性别
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "性别",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextSecondary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val options = listOf("男", "女")
                            options.forEach { option ->
                                FilterChip(
                                    selected = sex == option,
                                    onClick = { sex = option },
                                    label = { Text(option) },
                                    shape = RoundedCornerShape(20.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PrimaryBlue,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    // 手机号
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("手机号") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = uiState.phoneError != null,
                        supportingText = uiState.phoneError?.let { { Text(it) } },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Phone,
                                contentDescription = null,
                                tint = PrimaryBlue
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            errorBorderColor = ErrorRed
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 保存按钮
            Button(
                onClick = {
                    viewModel.updateProfile(
                        userName = name,
                        userAge = age.toIntOrNull(),
                        userSex = sex.takeIf { it.isNotEmpty() },
                        userTel = phone.takeIf { it.isNotEmpty() },
                        imgPath = avatar.takeIf { it.isNotEmpty() }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp)
                    .shadow(8.dp, RoundedCornerShape(25.dp), spotColor = PrimaryBlue.copy(alpha = 0.3f)),
                enabled = !uiState.isSaving && !uiState.isUploadingAvatar,
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Save,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "保存",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // 错误提示
        uiState.errorMessage?.let { error ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Snackbar(
                    shape = RoundedCornerShape(12.dp),
                    containerColor = ErrorRed,
                    contentColor = Color.White,
                    action = {
                        TextButton(onClick = viewModel::clearError) {
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
                        Text(error)
                    }
                }
            }
        }
    }
}
}
