//package com.example.network.dto
//
//import com.google.gson.annotations.SerializedName
//import java.time.LocalDateTime
//
//// ⭐ Issue 응답 DTO (AWS 서버에서 받는 데이터)
//data class AwsIssueResponseDto(
//    @SerializedName("id") val id: Long,
//    @SerializedName("title") val title: String,
//    @SerializedName("description") val description: String?,
//    @SerializedName("type") val type: String,
//    @SerializedName("status") val status: String,
//    @SerializedName("priority") val priority: String,
//    @SerializedName("importance") val importance: String,
//    @SerializedName("assigneeId") val assigneeId: String,
//    @SerializedName("assigneeName") val assigneeName: String,
//    @SerializedName("reporterId") val reporterId: String,
//    @SerializedName("reporterName") val reporterName: String,
//    @SerializedName("createdDate") val createdDate: String,
//    @SerializedName("dueDate") val dueDate: String,
//    @SerializedName("updateDate") val updateDate: String?,
//    @SerializedName("repository") val repository: String,
//    @SerializedName("projectNo") val projectNo: String
//)
//
//// ⭐ Issue 생성 요청 DTO (AWS 서버로 보내는 데이터)
//data class AwsIssueCreateRequestDto(
//    @SerializedName("title") val title: String,
//    @SerializedName("description") val description: String,
//    @SerializedName("type") val type: String,
//    @SerializedName("status") val status: String,
//    @SerializedName("priority") val priority: String,
//    @SerializedName("importance") val importance: String,
//    @SerializedName("assigneeId") val assigneeId: String,
//    @SerializedName("assigneeName") val assigneeName: String,
//    @SerializedName("reporterId") val reporterId: String,
//    @SerializedName("reporterName") val reporterName: String,
//    @SerializedName("dueDate") val dueDate: String,
//    @SerializedName("repository") val repository: String,
//    @SerializedName("projectNo") val projectNo: String
//)
//
//// ⭐ Issue 수정 요청 DTO
//data class AwsIssueUpdateRequestDto(
//    @SerializedName("title") val title: String,
//    @SerializedName("description") val description: String,
//    @SerializedName("type") val type: String,
//    @SerializedName("status") val status: String,
//    @SerializedName("priority") val priority: String,
//    @SerializedName("importance") val importance: String,
//    @SerializedName("assigneeId") val assigneeId: String,
//    @SerializedName("assigneeName") val assigneeName: String,
//    @SerializedName("reporterId") val reporterId: String,
//    @SerializedName("reporterName") val reporterName: String,
//    @SerializedName("dueDate") val dueDate: String,
//    @SerializedName("repository") val repository: String,
//    @SerializedName("projectNo") val projectNo: String
//)

package com.example.network.dto

import com.google.gson.annotations.SerializedName

data class AwsIssueResponseDto(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("type") val type: String,
    @SerializedName("status") val status: String,
    @SerializedName("priority") val priority: String,
    @SerializedName("importance") val importance: String,
    @SerializedName("assigneeId") val assigneeId: String,
    @SerializedName("assigneeName") val assigneeName: String,
    @SerializedName("reporterId") val reporterId: String,
    @SerializedName("reporterName") val reporterName: String,
    @SerializedName("createdDate") val createdDate: String,
    @SerializedName("dueDate") val dueDate: String,
    @SerializedName("updateDate") val updateDate: String?,
    @SerializedName("repository") val repository: String,
    @SerializedName("projectNo") val projectNo: String
)

data class AwsIssueCreateRequestDto(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("type") val type: String,
    @SerializedName("status") val status: String,
    @SerializedName("priority") val priority: String,
    @SerializedName("importance") val importance: String,
    @SerializedName("assigneeId") val assigneeId: String,
    @SerializedName("assigneeName") val assigneeName: String,
    @SerializedName("reporterId") val reporterId: String,
    @SerializedName("reporterName") val reporterName: String,
    @SerializedName("dueDate") val dueDate: String,
    @SerializedName("repository") val repository: String,
    @SerializedName("projectNo") val projectNo: String
)