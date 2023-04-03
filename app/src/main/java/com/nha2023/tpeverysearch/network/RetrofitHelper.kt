package com.nha2023.tpeverysearch.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class RetrofitHelper {
    companion object {
        fun getRetrofitInstance(baseUrl : String) : Retrofit {  //baseUrl : String하는 이유는?
            val retrofit : Retrofit = Retrofit.Builder()
                .baseUrl(baseUrl).addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create()).build()

            return retrofit
        }
    }
    //별도로 클래스르 안만들고 쓰는 ! static :companion object
    //baseUrl : String하는 이유는? 카카오톡에서도 이 작업을 할 수있는데 www.~를 쓰면 똑같은 url을쓰게되니까
}