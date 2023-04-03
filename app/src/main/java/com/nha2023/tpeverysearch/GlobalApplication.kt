package com.nha2023.tpeverysearch

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application(){

    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this,"653d9a6efe0239444d7fa8d29752ff6a")
        //개발자 사이트에 등록한 [네이티브앱키]이다.
       //어플리케이션 정도 되면 운영체제가 있다, 마지막 파라미터는 디폴트값이다 (XX= ~~ 식으로 디폴트값이 대입연산자로 보임)
    }



}