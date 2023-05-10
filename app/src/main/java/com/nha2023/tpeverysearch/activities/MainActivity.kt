package com.nha2023.tpeverysearch.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.kakao.util.maps.helper.Utility
import com.nha2023.tpeverysearch.R
import com.nha2023.tpeverysearch.databinding.ActivityMainBinding
import com.nha2023.tpeverysearch.fragment.PlaceListFragment
import com.nha2023.tpeverysearch.fragment.PlaceMapFragment
import com.nha2023.tpeverysearch.model.KakaoSearchPlaceResponse
import com.nha2023.tpeverysearch.network.RetrofitAPIService
import com.nha2023.tpeverysearch.network.RetrofitHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {

    val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    //검색에 필요한 요청데이터 : query(검색장소명) , 내주변 좌표 x(경도 longitude),y(위도 latitude) ->카카오에서 요청하는 데이터는 3개이다.
    //1. 검색 장소명
    var searchQuery : String = "네일샵" //앱 초기 검색어 - 내 주변 개방 화장실

    //2. 현재 내 위치 정보 객체 (위도, 경도를 멤버로 보유한 객체)
    var myLocation : Location?= null //내 위치를 못 찾아 올수도 있으니까 null로 해준다
    //내 위치를 null로 주면 서울 시청을 나온다.

    //[ Google Fused Location API 사용 : play-services-location ]
    //위치는 아주 위험하다 퍼미션 (동적 퍼미션)
    val providerClient : FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(this) } //사용자측에서 제공하는 위치
    //멤버변수에서는 context를 바로 쓰지 못한다. myLocation에 내 위치를 준다.


    //검색 결과 응답객체 참조변수 , 마커가 찍힌 위치를 얘가 갖고있다.
    var searchPlaceResponse : KakaoSearchPlaceResponse?= null //초기화 해야한다. null일수도 있다.


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //작업
        //툴바를 제목줄로 대체 - 옵션메뉴랑 연결되도록
        setSupportActionBar(binding.toolbar)

        //키 해쉬값을 얻어오자
        var keyHash : String = Utility.getKeyHash(this)
        Log.i("keyhash",keyHash)

        //앱이 시작하자마자 처음 보여질 프래그먼트를 동적으로 추가하자.
        supportFragmentManager.beginTransaction().add(R.id.container_fragment,PlaceListFragment()).commit() //PlaceListFragment() 객체를 만들어준다.

        //탭 버튼을 누르면 화면이 바뀌어야한다. (framelayout이 바뀌어야한다.)
        binding.tabLayout.addOnTabSelectedListener(object :OnTabSelectedListener{
            //두번 눌러지는 방법임. onTabSelected/onTabReselected
            override fun onTabSelected(tab: TabLayout.Tab?) {
                //여기에만 써주면 된다.
                if(tab?.text=="리스트로 보기"){
                    supportFragmentManager.beginTransaction().replace(R.id.container_fragment,PlaceListFragment()).commit()

                }else if(tab?.text=="지도로 보기"){
                    supportFragmentManager.beginTransaction().replace(R.id.container_fragment,PlaceMapFragment()).commit()

                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

        //검색기능 만들기 - 소프트키보드의 검색버튼을 클릭하였을때...
        binding.etSearch.setOnEditorActionListener { textView, i, keyEvent ->
            searchQuery = binding.etSearch.text.toString()

            //찐 검색작업 ! 카카오 검색 API를 이용하여 작업하기 - 카카오니까 레트로핏 이용
            searchPlace()//검색어가 바뀌면 검색다시함



            false
            //true를쓰면 키이벤트가 끝난다. 이대로라면 다른 버튼 (다른 키패드) 누르면 작동이 안된다. (부모이벤트에게 전달)
            //코틀린에서는 return을 쓰면 안된다.
        }

        //특정 키워드 단축 검색 버튼들에 리스너 처리하는 함수를 호출
        setChoiceButtonListener()


        //시작하면 내 위치 정보 제공에 대한 동적 퍼미션을 요청하자
        //FINE_LOCATION만 체크해도 코어스도 같이 한다. 그룹이라서!
        if(checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
            //거부 -> 퍼미션 요청 대행사를 받아서 계약체결한다.
             permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)

        }else{
            //허용했으니까 바로 내위치 요청하면 된다.
            requestMyLocation()
        }

        binding.ivMyLocation.setOnClickListener { requestMyLocation() }

    }//onCreate method..

    //퍼미션 요청 대행사 계약 및 등록
    val permissionLauncher : ActivityResultLauncher<String> = registerForActivityResult(ActivityResultContracts.RequestPermission(),object : ActivityResultCallback<Boolean>{
        override fun onActivityResult(result: Boolean?) {

            if(result!!) requestMyLocation() //결과가  true일때!
            else Toast.makeText(this@MainActivity, "검색기능이 제한됩니다. ", Toast.LENGTH_SHORT).show()

        }

    })


    //내위치 요청 작업 메소드
    private fun requestMyLocation(){
        //위치정보 기준을 설정하는 요청 객체 
        val request : com.google.android.gms.location.LocationRequest = com.google.android.gms.location.LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,1000).build()
        //위치정보는 얘가 가져옴 , 외우지말라고
        //PRIORITY_HIGH_ACCURACY  : GPS로 우선 적용해주세요

        //실시간 위치정보 갱신 요청 - 이 정보는 위치정보가 있을때만 쓸수있다. 동적허가 받앗는지 실행문이 써야한다. -> 그걸 onCreate메소드에서 썼으니까 이 지역에서는 못본다.
        //그걸 명시해줘야했다. addpermissioncheck를 자동으로 실행해줘야한다
        //providerClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        providerClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())

        
    }

    //위치검색결과 콜백객체
    private val locationCallback : LocationCallback= object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            myLocation = p0.lastLocation
            //정보 얻어왔으니까 실시간 업데이트 종료
            providerClient.removeLocationUpdates(this) //this는 메인이 아니다. locationCallback이다

            //위치 정보 얻었으니 검색 시작
            searchPlace()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.account -> startActivity(Intent(this,AccountActivity::class.java))
        }

        return super.onOptionsItemSelected(item)
    }

    //카카오 장소검색 API 파싱하는 작업 메소드
    private fun searchPlace(){
        //Toast.makeText(this, "$searchQuery - ${myLocation?.latitude}, ${myLocation?.longitude}", Toast.LENGTH_SHORT).show()
        //카카오 플랫폼에서 제공하고 있는 장소API을 이용하여 장소를 불러오자.
        //REST API  --> Retrofit
        val retrofit : Retrofit = RetrofitHelper.getRetrofitInstance("https://dapi.kakao.com")
        //https는 무조건 써줘야한다.
        val retrofitService =retrofit.create(RetrofitAPIService::class.java)
        retrofitService.searchPlace(searchQuery,myLocation?.latitude.toString(),myLocation?.longitude.toString()).enqueue(object : Callback<KakaoSearchPlaceResponse>{
            override fun onResponse(
                call: Call<KakaoSearchPlaceResponse>,
                response: Response<KakaoSearchPlaceResponse>
            ) {
                searchPlaceResponse = response.body()
                //Toast.makeText(this@MainActivity, "${searchPlaceResponse?.meta?.total_count}", Toast.LENGTH_SHORT).show()
                //? : 널세이프가 필요하다.

                //무조건 검색이 완료되면 ListFragment 부터 보여주기
                supportFragmentManager.beginTransaction().replace(R.id.container_fragment,PlaceListFragment()).commit()

                //번거롭지만 tab버튼의 위치를 ListFragment tab으로 변경
                binding.tabLayout.getTabAt(0)?.select()  //0번방에 안만들을수도 있어서

            }

            override fun onFailure(call: Call<KakaoSearchPlaceResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "실패 : ${t.toString()}", Toast.LENGTH_SHORT).show()
            }

        })

    //.enqueue() 는 Call에게 찍은것이다. 메소드에게 찍은게 아니다.



    }

    //잘쓰는 특정 키워드만 버튼만들어서 리스너 달기
    private fun setChoiceButtonListener(){
        binding.layoutChoice.choiceWc.setOnClickListener {clickChoice(it)  }
        binding.layoutChoice.choiceMovie.setOnClickListener {clickChoice(it)  }
        binding.layoutChoice.choiceFastfood.setOnClickListener {clickChoice(it) }
        binding.layoutChoice.choiceGas.setOnClickListener {clickChoice(it)  }
        binding.layoutChoice.choicePharm.setOnClickListener {clickChoice(it)  }
        binding.layoutChoice.choicePharm1.setOnClickListener {clickChoice(it)  }
        binding.layoutChoice.choicePharm2.setOnClickListener {clickChoice(it) }
        binding.layoutChoice.choiceCoffee.setOnClickListener {clickChoice(it)  }
        binding.layoutChoice.choiceCoffee1.setOnClickListener {clickChoice(it)  }
        binding.layoutChoice.choiceCoffee2.setOnClickListener {clickChoice(it)  }
        //어떤 버튼을 눌러도 it이 반응한다.
    }

    //프로퍼티 영역, 초이스된 아이디 저장
    var choiceID = R.id.choice_wc


    private fun  clickChoice(view : View){
        //기존 선택되었던 버튼을 찾아서 배경이미지를 흰색 원 그림으로 변경
        findViewById<ImageView>(choiceID).setBackgroundResource(R.drawable.bg_choice) //흰색배경

        //현재 클릭된 버튼의 배경을 회색 원 그림으로 변경
        view.setBackgroundResource(R.drawable.bg_choice_select)

        //다음클릭시에 이전 클릭된 뷰의 ID를 기억하더록 ..
        choiceID = view.id //view는 현재 클릭된 아이

        //choice한 것에 따라 검색 장소명을 변경하여 다시 검색..
        when(choiceID){
            R.id.choice_wc -> searchQuery= "네일샵"
            R.id.choice_movie -> searchQuery= "속눈썹"
            R.id.choice_fastfood -> searchQuery= "미용실"
            R.id.choice_gas -> searchQuery= "화장품"
            R.id.choice_pharm -> searchQuery= "피부관리"
            R.id.choice_pharm1 -> searchQuery= "요가"
            R.id.choice_pharm2 -> searchQuery= "필라테스"
            R.id.choice_coffee -> searchQuery= "의류판매"
            R.id.choice_coffee1-> searchQuery= "체형관리"
            R.id.choice_coffee2 -> searchQuery= "메이크업"
        }

        //새로운 검색 시작
        searchPlace()


        //검색창에 이미 글쓰가 있다면 지우기
        binding.etSearch.text.clear()
        binding.etSearch.clearFocus() //커서를 없앤다.

    }
}