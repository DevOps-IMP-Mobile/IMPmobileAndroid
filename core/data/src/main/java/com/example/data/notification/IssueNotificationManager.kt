package com.example.data.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.domain.model.issue.IssuePriority
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 이슈 알림을 관리하는 클래스
 * 현재: 로컬 알림
 * 향후: FCM으로 전환 가능
 */
@Singleton
class IssueNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val TAG = "IssueNotification"
        private const val CHANNEL_ID = "issue_notifications"
        private const val CHANNEL_NAME = "이슈 알림"
        private const val CHANNEL_DESCRIPTION = "새로운 이슈 생성 및 업데이트 알림"

        // 알림 ID는 이슈별로 고유하게 생성
        private fun getNotificationId(issueId: String): Int {
            return issueId.hashCode()
        }
    }

    init {
        createNotificationChannel()
    }

    /**
     * 알림 채널 생성 (Android 8.0 이상)
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                enableLights(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * 알림 권한이 있는지 확인
     */
    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 이상 - POST_NOTIFICATIONS 권한 확인
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Android 12 이하 - 알림 설정 확인
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    /**
     * 이슈 생성 알림 표시
     *
     * @param issueId 이슈 ID
     * @param issueTitle 이슈 제목
     * @param priority 우선순위
     * @param projectName 프로젝트명 (선택)
     */

    @SuppressLint("MissingPermission")
    fun showIssueCreatedNotification(
        issueId: String,
        issueTitle: String,
        priority: IssuePriority,
        projectName: String? = null
    ) {
        // ✅ 권한 체크 - 없으면 조용히 종료
        if (!hasNotificationPermission()) {
            Log.w(TAG, "알림 권한이 없습니다. 설정에서 알림을 허용해주세요.")
            return
        }

        val priorityText = when (priority) {
            IssuePriority.CRITICAL -> "🔴 긴급"
            IssuePriority.HIGH -> "🟠 높음"
            IssuePriority.NORMAL -> "🟡 보통"
            IssuePriority.LOW -> "🟢 낮음"
        }

        val title = "새로운 이슈가 생성되었습니다"
        val content = buildString {
            append("[$priorityText] $issueTitle")
            if (!projectName.isNullOrEmpty()) {
                append("\n프로젝트: $projectName")
            }
        }

        // 알림 클릭 시 이동할 Intent (나중에 실제 Activity로 연결)
        val intent = createIssueDetailIntent(issueId)
        val pendingIntent = PendingIntent.getActivity(
            context,
            getNotificationId(issueId),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // TODO: 실제 앱 아이콘으로 변경
            .setContentTitle(title)
            .setContentText("[$priorityText] $issueTitle")
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(getNotificationPriority(priority))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val i: Int = try {
            NotificationManagerCompat.from(context).notify(
                getNotificationId(issueId),
                notification
            )
            Log.d(TAG, "알림 표시 성공: $issueTitle")
        } catch (e: SecurityException) {
            Log.e(TAG, "알림 표시 실패: SecurityException", e)
        } catch (e: Exception) {
            Log.e(TAG, "알림 표시 실패", e)
        }
    }

    /**
     * 이슈 우선순위에 따른 알림 우선순위 매핑
     */
    private fun getNotificationPriority(priority: IssuePriority): Int {
        return when (priority) {
            IssuePriority.CRITICAL -> NotificationCompat.PRIORITY_MAX
            IssuePriority.HIGH -> NotificationCompat.PRIORITY_HIGH
            IssuePriority.NORMAL -> NotificationCompat.PRIORITY_DEFAULT
            IssuePriority.LOW -> NotificationCompat.PRIORITY_LOW
        }
    }

    /**
     * 이슈 상세 화면으로 이동하는 Intent 생성
     * TODO: 실제 IssueDetailActivity로 연결 필요
     */
    private fun createIssueDetailIntent(issueId: String): Intent {
        // 현재는 MainActivity로 이동 (나중에 실제 화면으로 변경)
        val intent = Intent(context, Class.forName("com.example.impmobileandroid.MainActivity"))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra("issue_id", issueId)
        intent.putExtra("navigate_to", "issue_detail")
        return intent
    }

    /**
     * 특정 알림 취소
     */
    fun cancelNotification(issueId: String) {
        try {
            NotificationManagerCompat.from(context).cancel(getNotificationId(issueId))
        } catch (e: Exception) {
            Log.e(TAG, "알림 취소 실패", e)
        }
    }

    /**
     * 모든 알림 취소
     */
    fun cancelAllNotifications() {
        try {
            NotificationManagerCompat.from(context).cancelAll()
        } catch (e: Exception) {
            Log.e(TAG, "모든 알림 취소 실패", e)
        }
    }
}
