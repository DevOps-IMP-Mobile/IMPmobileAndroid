package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.repository.FcmRepository
import com.example.domain.context.UserContext  // ✅ 수정
import com.example.ui.theme.MyApplicationTheme
import com.example.login.LoginScreen
import com.example.myapplication.navigation.MainScreen
import com.example.myapplication.navigation.Screen
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var fcmRepository: FcmRepository

    companion object {
        private const val TAG = "MainActivity"
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // FCM 토큰 가져오기
        getFCMToken()

        // 알림 권한 요청
        requestNotificationPermission()

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }

    /**
     * FCM 토큰 가져오기
     */
    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "❌ FCM 토큰 가져오기 실패", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d(TAG, "✅ FCM 토큰 발급 성공!")
            Log.d(TAG, "토큰: $token")

            // 로컬 DB에 저장
            saveTokenToLocalDB(token)
        }
    }

    /**
     * FCM 토큰을 로컬 DB에 저장
     */
    private fun saveTokenToLocalDB(token: String) {
        lifecycleScope.launch {
            try {
                val ctx = UserContext.instance
                val userId = ctx.userId
                val spUid = ctx.spUid

                if (userId.isNullOrEmpty()) {
                    Log.w(TAG, "⚠️ 로그인 정보가 없어서 SharedPreferences에 임시 저장")
                    saveTokenToPreferences(token)
                    return@launch
                }

                // Room DB에 저장
                val result = fcmRepository.saveFcmToken(
                    userId = userId,
                    fcmToken = token,
                    spUid = spUid
                )

                if (result.isSuccess) {
                    Log.d(TAG, "✅ FCM 토큰 로컬 DB 저장 성공")

                    // 저장된 토큰 확인
                    val savedToken = fcmRepository.getFcmToken(userId)
                    Log.d(TAG, "저장 확인: ${savedToken?.substring(0, 20)}...")
                } else {
                    Log.e(TAG, "❌ FCM 토큰 저장 실패", result.exceptionOrNull())
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ FCM 토큰 저장 중 예외", e)
            }
        }
    }

    /**
     * SharedPreferences에 임시 저장 (로그인 전)
     */
    private fun saveTokenToPreferences(token: String) {
        val prefs = getSharedPreferences("fcm_prefs", MODE_PRIVATE)
        prefs.edit().putString("pending_fcm_token", token).apply()
        Log.d(TAG, "✅ FCM 토큰 임시 저장 완료 (로그인 후 DB 저장 예정)")
    }

    /**
     * 알림 권한 요청 (Android 13+)
     */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    /**
     * 권한 요청 결과 처리
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,  // ✅ out 제거
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            NOTIFICATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "✅ 알림 권한 허용됨")
                } else {
                    Log.d(TAG, "❌ 알림 권한 거부됨")
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable("main") {
            MainScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }
    }
}