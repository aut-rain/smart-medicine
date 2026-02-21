package com.example.smart_medicine_android.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smart_medicine_android.ui.theme.*

/**
 * 登录/注册页面 - 简化布局
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // 监听登录成功
    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onLoginSuccess()
        }
    }

    SmartMedicineTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundPrimary)
        ) {
            // 顶部装饰背景
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                PrimaryBlue,
                                PrimaryBlueDark
                            )
                        )
                    )
            )

            // 可滚动内容
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))

                // Logo 区域
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.LocalHospital,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "智慧医疗",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 登录/注册表单
                AuthFormCard(
                    isLoginMode = uiState.isLoginMode,
                    account = uiState.account,
                    email = uiState.email,
                    password = uiState.password,
                    verificationCode = uiState.verificationCode,
                    onAccountChange = viewModel::onAccountChange,
                    onEmailChange = viewModel::onEmailChange,
                    onPasswordChange = viewModel::onPasswordChange,
                    onVerificationCodeChange = viewModel::onVerificationCodeChange,
                    isLoading = uiState.isLoading,
                    errorMessage = uiState.errorMessage,
                    onLoginClick = viewModel::login,
                    onRegisterClick = viewModel::register,
                    onSendCodeClick = viewModel::sendVerificationCode,
                    onToggleMode = viewModel::toggleMode,
                    onClearError = viewModel::clearError
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ==================== 认证表单卡片 ====================

@Composable
private fun AuthFormCard(
    isLoginMode: Boolean,
    account: String,
    email: String,
    password: String,
    verificationCode: String,
    onAccountChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onVerificationCodeChange: (String) -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onSendCodeClick: () -> Unit,
    onToggleMode: () -> Unit,
    onClearError: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // 标题
            Text(
                text = if (isLoginMode) "欢迎回来" else "创建账号",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isLoginMode) "登录您的账号" else "注册新账号开始使用",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 错误提示
            if (errorMessage != null) {
                ErrorBanner(message = errorMessage)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 表单内容
            if (isLoginMode) {
                LoginFormContent(
                    account = account,
                    password = password,
                    onAccountChange = onAccountChange,
                    onPasswordChange = onPasswordChange,
                    isLoading = isLoading,
                    onLoginClick = onLoginClick,
                    onClearError = onClearError
                )
            } else {
                RegisterFormContent(
                    account = account,
                    email = email,
                    password = password,
                    verificationCode = verificationCode,
                    onAccountChange = onAccountChange,
                    onEmailChange = onEmailChange,
                    onPasswordChange = onPasswordChange,
                    onVerificationCodeChange = onVerificationCodeChange,
                    isLoading = isLoading,
                    onRegisterClick = onRegisterClick,
                    onSendCodeClick = onSendCodeClick,
                    onClearError = onClearError
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 切换登录/注册
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (isLoginMode) "还没有账号？ " else "已有账号？ ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                TextButton(
                    onClick = onToggleMode,
                    enabled = !isLoading
                ) {
                    Text(
                        text = if (isLoginMode) "立即注册" else "立即登录",
                        color = PrimaryBlue
                    )
                }
            }
        }
    }
}

// ==================== 登录表单内容 ====================

@Composable
private fun LoginFormContent(
    account: String,
    password: String,
    onAccountChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    isLoading: Boolean,
    onLoginClick: () -> Unit,
    onClearError: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    // 账号输入
    OutlinedTextField(
        value = account,
        onValueChange = {
            onAccountChange(it)
            onClearError()
        },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("账号") },
        leadingIcon = {
            Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryBlue)
        },
        singleLine = true,
        enabled = !isLoading,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryBlue,
            focusedLabelColor = PrimaryBlue,
            cursorColor = PrimaryBlue
        )
    )

    Spacer(modifier = Modifier.height(16.dp))

    // 密码输入
    OutlinedTextField(
        value = password,
        onValueChange = {
            onPasswordChange(it)
            onClearError()
        },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("密码") },
        leadingIcon = {
            Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryBlue)
        },
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    if (passwordVisible) Icons.Default.Visibility
                    else Icons.Default.VisibilityOff,
                    contentDescription = null
                )
            }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None
        else PasswordVisualTransformation(),
        singleLine = true,
        enabled = !isLoading,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryBlue,
            focusedLabelColor = PrimaryBlue,
            cursorColor = PrimaryBlue
        )
    )

    Spacer(modifier = Modifier.height(24.dp))

    // 登录按钮
    Button(
        onClick = onLoginClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        enabled = account.isNotBlank() && password.isNotBlank() && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryBlue,
            disabledContainerColor = PrimaryBlue.copy(alpha = 0.5f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = "登录",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

// ==================== 注册表单内容 ====================

@Composable
private fun RegisterFormContent(
    account: String,
    email: String,
    password: String,
    verificationCode: String,
    onAccountChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onVerificationCodeChange: (String) -> Unit,
    isLoading: Boolean,
    onRegisterClick: () -> Unit,
    onSendCodeClick: () -> Unit,
    onClearError: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    // 账号输入
    OutlinedTextField(
        value = account,
        onValueChange = {
            onAccountChange(it)
            onClearError()
        },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("账号") },
        leadingIcon = {
            Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryBlue)
        },
        singleLine = true,
        enabled = !isLoading,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryBlue,
            focusedLabelColor = PrimaryBlue,
            cursorColor = PrimaryBlue
        )
    )

    Spacer(modifier = Modifier.height(12.dp))

    // 邮箱输入
    OutlinedTextField(
        value = email,
        onValueChange = {
            onEmailChange(it)
            onClearError()
        },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("邮箱") },
        leadingIcon = {
            Icon(Icons.Default.Email, contentDescription = null, tint = PrimaryBlue)
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        enabled = !isLoading,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryBlue,
            focusedLabelColor = PrimaryBlue,
            cursorColor = PrimaryBlue
        )
    )

    Spacer(modifier = Modifier.height(12.dp))

    // 验证码输入
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = verificationCode,
            onValueChange = {
                onVerificationCodeChange(it)
                onClearError()
            },
            modifier = Modifier.weight(1f),
            label = { Text("验证码") },
            leadingIcon = {
                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = PrimaryBlue)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            enabled = !isLoading,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                focusedLabelColor = PrimaryBlue,
                cursorColor = PrimaryBlue
            )
        )

        OutlinedButton(
            onClick = onSendCodeClick,
            enabled = !isLoading && email.isNotBlank(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("发送")
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // 密码输入
    OutlinedTextField(
        value = password,
        onValueChange = {
            onPasswordChange(it)
            onClearError()
        },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("密码") },
        leadingIcon = {
            Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryBlue)
        },
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    if (passwordVisible) Icons.Default.Visibility
                    else Icons.Default.VisibilityOff,
                    contentDescription = null
                )
            }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None
        else PasswordVisualTransformation(),
        singleLine = true,
        enabled = !isLoading,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryBlue,
            focusedLabelColor = PrimaryBlue,
            cursorColor = PrimaryBlue
        )
    )

    Spacer(modifier = Modifier.height(24.dp))

    // 注册按钮
    Button(
        onClick = onRegisterClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        enabled = account.isNotBlank() && email.isNotBlank() &&
                  password.isNotBlank() && verificationCode.isNotBlank() && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryBlue,
            disabledContainerColor = PrimaryBlue.copy(alpha = 0.5f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = "注册",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

// ==================== 错误提示 ====================

@Composable
private fun ErrorBanner(message: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ErrorRed.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = ErrorRed,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = ErrorRed
        )
    }
}
