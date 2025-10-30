package com.example.myapplication.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCM"
        private const val CHANNEL_ID = "issue_notification_channel"
        private const val CHANNEL_NAME = "이슈 알림"
    }

    /**
     * FCM 토큰이 새로 생성되거나 갱신될 때 호출됩니다
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "새로운 FCM 토큰 생성: $token")

        // TODO: 백엔드 API로 토큰 전송
        // 예: POST /api/users/fcm-token
        sendTokenToServer(token)
    }

    /**
     * FCM 메시지를 수신했을 때 호출됩니다
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d(TAG, "FCM 메시지 수신!")
        Log.d(TAG, "From: ${message.from}")
        Log.d(TAG, "Data: ${message.data}")

        // 데이터 페이로드 처리
        message.data.let { data ->
            if (data.isNotEmpty()) {
                Log.d(TAG, "데이터 페이로드: $data")

                val title = data["title"] ?: "새 알림"
                val body = data["body"] ?: "알림이 도착했습니다"
                val issueId = data["issue_id"]
                val projectNo = data["project_no"]
                val type = data["type"] // ISSUE_CREATED, ISSUE_UPDATED 등

                showNotification(title, body, issueId, projectNo, type)
            }
        }

        // 알림 페이로드 처리 (선택적)
        message.notification?.let { notification ->
            Log.d(TAG, "알림 페이로드: ${notification.title} - ${notification.body}")
            showNotification(
                notification.title ?: "알림",
                notification.body ?: ""
            )
        }
    }

    /**
     * 알림을 화면에 표시합니다
     */
    private fun showNotification(
        title: String,
        body: String,
        issueId: String? = null,
        projectNo: String? = null,
        type: String? = null
    ) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android 8.0 이상에서는 알림 채널 생성 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "프로젝트 이슈 관련 알림"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 알림 클릭 시 앱 실행 Intent
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            // 이슈 상세 화면으로 이동하기 위한 데이터
            issueId?.let { putExtra("issue_id", it) }
            projectNo?.let { putExtra("project_no", it) }
            type?.let { putExtra("notification_type", it) }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 알림 생성
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // TODO: 앱 아이콘으로 교체
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body)) // 긴 텍스트 지원
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // 클릭 시 자동 삭제
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL) // 소리, 진동, LED
            .build()

        // 알림 표시 (타임스탬프를 ID로 사용하여 여러 알림 동시 표시 가능)
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)

        Log.d(TAG, "알림 표시 완료: $title")
    }

    /**
     * FCM 토큰을 백엔드 서버로 전송합니다
     */
    private fun sendTokenToServer(token: String) {
        // TODO: 백엔드 API 구현 후 여기서 호출
        // 예시:
        // val retrofit = RetrofitClient.instance
        // val api = retrofit.create(UserApiService::class.java)
        // GlobalScope.launch {
        //     try {
        //         api.updateFcmToken(token)
        //         Log.d(TAG, "FCM 토큰 서버 전송 성공")
        //     } catch (e: Exception) {
        //         Log.e(TAG, "FCM 토큰 서버 전송 실패", e)
        //     }
        // }

        Log.d(TAG, "FCM 토큰 서버 전송 필요: $token")
    }
}