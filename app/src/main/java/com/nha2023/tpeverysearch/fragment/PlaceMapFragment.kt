package com.nha2023.tpeverysearch.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nha2023.tpeverysearch.R
import com.nha2023.tpeverysearch.activities.MainActivity
import com.nha2023.tpeverysearch.activities.PlaceUrlActivity
import com.nha2023.tpeverysearch.databinding.FragmentPlaceListBinding
import com.nha2023.tpeverysearch.databinding.FragmentPlaceMapBinding
import com.nha2023.tpeverysearch.model.Place
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import net.daum.mf.map.api.MapView.POIItemEventListener

class PlaceMapFragment : Fragment() {

    lateinit var binding: FragmentPlaceMapBinding //언제 초기화함? 밑에 onCreateView


    override fun onCreateView( //프래그먼트가 보여질 뷰를 만들때
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaceMapBinding.inflate(layoutInflater, container,false) //container는 부모사이즈를 인식해서 만들어준다. 지금당장 붙이면 안돼 . return할때 붙이는것이다
        return binding.root //우리로 치면 relative layout
        //맵뷰는 여기 없다.
    }

    val mapView : MapView by lazy { MapView(context) } // 맵뷰객체 생성  프래그먼트에서 운영체제는 context로 부른다.

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.containerMapview.addView(mapView) //여기까지는 기본지도가 보이고
        //이제 LIST내의 장소들을 마커로 찍어서 보여주는 작업이 필요하다.

        //순서중요! 지도관련 설정 위에서 해야한다.
        //마커 or 말풍선클릭 이벤트 - 반드시 마커를 추가하는거보다 먼저 등록해야한다.
        mapView.setPOIItemEventListener(markerEventListener) //markerEventListener 전역변수로 써야한다. 클래스이름이 맵뷰가 아니다.



        //이제 지도 관련설정 (지도위치, 마커추가 등)

        setMapAndMarkers()
    }

    private fun setMapAndMarkers(){
        // 맵 중심점 변경
        // 현재 내 위치 위도(래티튜드), 경도(롱기튜드) 좌표
        // 액티비티안에 프래그먼트가 존재한다. 그래서  var searchPlaceResponse, var myLocation 를 갖고있다.
        var lat : Double = (activity as MainActivity).myLocation?.latitude ?: 37.5663 //액티비티가 아직 내 위치를 못가져온다면? 서울시청으로 설정한다.
        var longi : Double = (activity as MainActivity).myLocation?.longitude ?: 126.9779 //액티비티가 아직 내 위치를 못가져온다면? 서울시청으로 설정한다.

        var mvMapPoint : MapPoint = MapPoint.mapPointWithGeoCoord(lat,longi)
        mapView.setMapCenterPointAndZoomLevel(mvMapPoint,5,true)
        //mapPoint : 위도 경도
        //zoomlevel : 확대하는거, 1이 제일 크다
        //animated : 줌 애니메이션쓸거임?

        mapView.zoomIn(true)
        mapView.zoomOut(true)

        //내 위치를 표시하는 마커가 필요하다.
        var marker = MapPOIItem() //객체 만들기
        marker.apply {  // apply를 써서 marker에게 요청할것들을 {  } 안에 써준다.
            itemName = "여기"
            mapPoint = mvMapPoint
            markerType = MapPOIItem.MarkerType.BluePin
            selectedMarkerType= MapPOIItem.MarkerType.YellowPin
        }

        mapView.addPOIItem(marker)

        //검색장소들의 마커 추가
        val documents : MutableList<Place>? = (activity as MainActivity).searchPlaceResponse?.documents//이미 MainAct가 갖고있다.
        documents?.forEach {
            val point : MapPoint = MapPoint.mapPointWithGeoCoord(it.y.toDouble(),it.x.toDouble())
            val marker = MapPOIItem().apply {
                mapPoint = point
                itemName = it.place_name
                markerType = MapPOIItem.MarkerType.RedPin
                selectedMarkerType = MapPOIItem.MarkerType.YellowPin

                //마커 객체에 보관하고 싶은 데이터가 있다면...
                //즉, 해당 마커에 관련된 정보를 가지고 있는 객체를 마커에 저장해두기.
                userObject = it.place_url
                //it은 Place이다


            }
        }
        //코틀린은 if문 돌릴때 for each문
        //sam변환
        //?. : null아니면 실행해라

        mapView.addPOIItem(marker)

    }


    //마커 or 말풍선 클릭 이벤트 리스너
    val markerEventListener : POIItemEventListener = object : POIItemEventListener{
        override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {
            //POI : 마커
            //마커 클릭했을때 발동!


        }

        override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {
            TODO("Not yet implemented")
            //여기 지금 금지됨, 아래꺼 쓰자
        }

        override fun onCalloutBalloonOfPOIItemTouched(
            p0: MapView?,
            p1: MapPOIItem?,
            p2: MapPOIItem.CalloutBalloonButtonType?
        ) {
            //말풍선 터치했을때 발동
            //두번째 파라미터 p1 : 클릭한 마커 객체
            p1?.userObject ?: return
            //userObject은 it인데 .찍으면 it관련된 변수가 안나온다.

            val place : Place = p1?.userObject as Place //다운캐스팅
            val intent = Intent(context,PlaceUrlActivity::class.java)
            intent.putExtra("place_url",place.place_url)
            startActivity(intent) //아탑터일때는 context를 붙인다.

        }

        override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
            //마커를 드래그했을때 발동하는거
        }

    }// 인터페이스라서 object : POIItemEventListener 익명객체를 갖고와서 불러야한다.


}


























