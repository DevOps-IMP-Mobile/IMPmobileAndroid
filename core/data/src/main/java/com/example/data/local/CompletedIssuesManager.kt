package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompletedIssuesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("completed_issues", Context.MODE_PRIVATE)

    private val COMPLETED_KEY = "completed_issue_ids"

    /**
     * 완료한 이슈 ID 추가
     */
    fun addCompletedIssue(issueId: String) {
        val currentIds = getCompletedIssues().toMutableSet()
        currentIds.add(issueId)
        prefs.edit().putStringSet(COMPLETED_KEY, currentIds).apply()
    }

    /**
     * 완료한 이슈 ID 목록 조회
     */
    fun getCompletedIssues(): Set<String> {
        return prefs.getStringSet(COMPLETED_KEY, emptySet()) ?: emptySet()
    }

    /**
     * 이슈가 완료되었는지 확인
     */
    fun isCompleted(issueId: String): Boolean {
        return getCompletedIssues().contains(issueId)
    }

    /**
     * 완료 목록 초기화 (필요시)
     */
    fun clearCompletedIssues() {
        prefs.edit().remove(COMPLETED_KEY).apply()
    }
}