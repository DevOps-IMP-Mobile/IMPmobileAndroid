//package com.example.network.api
//
//import com.example.network.dto.AwsIssueResponseDto
//import com.example.network.dto.AwsIssueCreateRequestDto
//import com.example.network.dto.AwsIssueUpdateRequestDto
//import retrofit2.http.*
//
//interface AwsIssueApiService {
//
//    // 이슈 조회
//    @GET("issues/{id}")
//    suspend fun getIssue(@Path("id") id: Long): AwsIssueResponseDto
//
//    // 이슈 생성
//    @POST("issues")
//    suspend fun createIssue(@Body request: AwsIssueCreateRequestDto): AwsIssueResponseDto
//
//    // 이슈 수정
//    @PUT("issues/{id}")
//    suspend fun updateIssue(
//        @Path("id") id: Long,
//        @Body request: AwsIssueUpdateRequestDto
//    ): AwsIssueResponseDto
//
//    // 이슈 삭제
//    @DELETE("issues/{id}")
//    suspend fun deleteIssue(@Path("id") id: Long)
//}

package com.example.network.api

import com.example.network.dto.AwsIssueResponseDto
import com.example.network.dto.AwsIssueCreateRequestDto
import retrofit2.http.*

interface AwsIssueApiService {

    @POST("issues")
    suspend fun createIssue(@Body request: AwsIssueCreateRequestDto): AwsIssueResponseDto
}