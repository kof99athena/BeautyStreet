package com.nha2023.tpeverysearch.network

import com.nha2023.tpeverysearch.model.NidUserInfoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface RetrofitAPIService {

    //레트로핏이 해줬으면 하는 작업 : RetrofitAPIService
    //네아로 사용자 정보 API 받자
    @GET("/v1/nid/me") //버전 1, 네이버아이디, me
    fun getNisUserInfo(@Header("Authorization") autorization : String) : Call<NidUserInfoResponse>

}