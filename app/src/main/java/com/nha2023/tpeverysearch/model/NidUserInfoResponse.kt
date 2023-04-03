package com.nha2023.tpeverysearch.model


//이 클래스를 만드는 이유는 ? Call 객체에줘야함
data class NidUserInfoResponse (var resultcode : String, var message : String, var response : NidUser)

data class NidUser (var id : String, var email : String)
