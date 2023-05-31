package com.nha2023.tpeverysearch.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.nha2023.tpeverysearch.R
import com.nha2023.tpeverysearch.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)


        Log.i("actionBar",supportActionBar.toString())
        //툴바를 액션바로 설정
        setSupportActionBar(binding.toolbar)
        //액션바에 업버튼 만들기
        supportActionBar?.setDisplayHomeAsUpEnabled(true) //get ~ , 없으면 에러 -> 자바에서는 if써서 해결

//        if(supportActionBar==null) return
//        supportActionBar.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.twotone_navigate_before_24)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.btnSignup.setOnClickListener { clickSignUp() }
    }//onCreate method..

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun clickSignUp(){
        //Firebase Firestore DB에 사용자 정보 저장하기

        var email:String= binding.etEmail.text.toString() // "" 이건 null이 아니다.
        var password:String= binding.etPassword.text.toString()
        var passwordConfirm:String= binding.etPasswordConfirm.text.toString()

        //유효성 검사 - 패스워드와 패스워드 확인이 맞는지 검사
        if(password != passwordConfirm){
            AlertDialog.Builder(this).setMessage("패스워드확인에 문제가 있습니다. 다시 확인해주시기 바랍니다.").create().show()
            binding.etPasswordConfirm.selectAll()
            return
        }

        //Firestore DB instance 얻어오기
        val db = FirebaseFirestore.getInstance()

        //저장데이터 값(이메일, 비밀번호)를 HashMap으로 저장하기
        val user : MutableMap<String,String> = mutableMapOf()
        user.put("email",email)
        user["password"] = password


        //중복된 email을 가진 회원정보가 있을수도 잇으니 확인..
        db.collection("emailUsers").whereEqualTo("email",email).get().addOnSuccessListener {
            //같은 값을 가진 도큐먼트가 있다면? 사이즈가 0개 이상일것이므로
            if(it.documents.size>0){
                //0개 이상이면 중복이라는것이다. 저장하면 안된다.
                AlertDialog.Builder(this).setMessage("중복된 이메일이 있습니다. 다시 확인하시길 바랍니다.").show() //create까지 자동으로 해준다.
                binding.etEmail.requestFocus()
                binding.etEmail.selectAll()//포커싱있을때만 가능하다
            }else{

                //컬렉션 명은 "emailUsers"로 지정 [ RDBMS의 테이블명 같은 역할]
                //랜덤하게 만들어지는 document명을 회원 id값으로 사용할 예정 - 그래서 별도의 식별자를 안쓴다.
                //db.collection("emailUsers").document().set(user) //없으면 만들고 있으면 거기 넣는다.
                db.collection("emailUsers").add(user).addOnSuccessListener {
                    AlertDialog.Builder(this)
                        .setMessage("축하합니다. /n 회원 가입이 완료 되었습니다. ")
                        .create().show()


                }// 축약형

            }
        }



    }
}















