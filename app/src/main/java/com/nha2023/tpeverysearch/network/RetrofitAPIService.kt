package com.nha2023.tpeverysearch.network

import com.nha2023.tpeverysearch.model.KakaoSearchPlaceResponse
import com.nha2023.tpeverysearch.model.NidUserInfoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface RetrofitAPIService {

    //레트로핏이 해줬으면 하는 작업 : RetrofitAPIService
    //네아로 사용자 정보 API 받자
    @GET("/v1/nid/me") //버전 1, 네이버아이디, me
    fun getNisUserInfo(@Header("Authorization") autorization : String) : Call<NidUserInfoResponse>

    //카카오 키워드 장소 검색 API
    //변화되는거면 파라미터에 넣고 아니라면 @에 넣는다
    @Headers("Authorization: KakaoAK ea32dc8705e4633ec147c165e400fdb3")
    @GET("/v2/local/search/keyword.json")
    fun searchPlace(@Query("query") query: String, @Query("y") latitude : String, @Query("x")longitude : String) : Call<KakaoSearchPlaceResponse> //레트로핏에게 이렇게 해달라고 요청한거임.
    //이 함수가 호출될때 레트로핏이 이 작업을 해줄것이다. <>안에는 이걸 받아야하는놈들을 넣자.
    //@Query("query") ,@Query("y") 요청파라미터 query,y로 읽어줄것이다.

}