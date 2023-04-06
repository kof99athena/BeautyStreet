package com.nha2023.tpeverysearch.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import com.nha2023.tpeverysearch.R
import com.nha2023.tpeverysearch.databinding.ActivityPlaceUrlBinding

class PlaceUrlActivity : AppCompatActivity() {

    val binding : ActivityPlaceUrlBinding by lazy { ActivityPlaceUrlBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //bind 안해도 된다. 왜냐면 val 니까


        //웹뷰를 만들때 필수속성 세가지
        binding.webView.webViewClient = WebViewClient()
        //기본적으로 안드로이드는 웹 주소를 보여달라하면 핸드폰에 잇는 크롬 브라우저를 먼저 킨다.
        //그래서 얘를 써야한다. 현재 웹뷰안에서 웹문서 열리도록 한다.
        //이걸 안주면 크롬으로 열린다... 아주귀찮아

        binding.webView.webChromeClient = WebChromeClient()
        //웹문서안에서 다이얼로그 같은 것을 발동하도록

        //기본으로 웹뷰는 자바스크립트가 사용금지되어있다. settings객체를 불러오자.
        binding.webView.settings.javaScriptEnabled= true
        //웹뷰는 기본적으로 보안문제로 JS 동작을 막아놓았기에.. 이를 허용하는 코드를 쓰자

        var place_url : String = intent.getStringExtra("place_Url") ?: "" //앞의 것이 null이면 ""을 준다.
        binding.webView.loadUrl(place_url) //()안에 null을 안쓴다.


        //여기서 웹페이지 넘어가서 계속 본 후 뒤로가기를 누르면 LIST화면이 뚝 나온다. 중간과정생략된다.
        //뒤로가기는 액티비티로넘어가는 성질이 있다.
        //WebView는 history를 관리하고 있으니까 그놈을 써야한다.


    }

    override fun onBackPressed() {
        if(binding.webView.canGoBack()) binding.webView.goBack()
        else super.onBackPressed() //뒤로 갈게 없으면 온백프레스해서 액티비티화면으로 넘어간다.
        //여기를해야 뒤로갈때 상세페이지를 단계적으로뒤로가게한다.
    }



}