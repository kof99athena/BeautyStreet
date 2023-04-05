package com.nha2023.tpeverysearch.model

data class KakaoSearchPlaceResponse(var meta : PlaceMeta, var documents : MutableList<Place>) //3개짜리 메타클래스 만들기,  List<> 도 많이쓴다. 값변경 불가
data class PlaceMeta(var total_count : Int, var pageable_count : Int, var is_end : Boolean)
//코틀린에서 Integer는 Int이다
// var is_end : Boolean -> 스크롤을 내리다가 마지막 페이지가 보이면 page값을 증가시켜 다음페이지 요청할수있다.

data class Place(
    var id : String,
    var place_name : String,
    var category_name : String,
    var phone : String,
    var address_name : String,
    var road_address_name : String,
    var x : String, // longitude (경도)
    var y : String, // latitude(위도)
    var place_url : String,
    var distance : String //중심좌료까지의 거리.. 요청파라미터로 x,y 좌표를 준 경우만 존재.. 단위는 meter이다.
)