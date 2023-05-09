package com.nha2023.tpeverysearch.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.nha2023.tpeverysearch.G
import com.nha2023.tpeverysearch.R
import com.nha2023.tpeverysearch.databinding.ActivityEmailSigninBinding
import com.nha2023.tpeverysearch.model.UserAccount

class EmailSigninActivity : AppCompatActivity() {

    lateinit var binding: ActivityEmailSigninBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailSigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_kakako) // 맘에 안들면 뒤버튼을 바꾸자
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.btnSignIn.setOnClickListener { clickSignIn() }
    }


    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun clickSignIn(){
        var email : String = binding.etEmail.text.toString()
        var password : String = binding.etPassword.text.toString()

        //파이어베이스 파이어스토어 DB에서 이메일과 패스워드를 확인하자
        var db : FirebaseFirestore = FirebaseFirestore.getInstance()
        db.collection("emailUsers")
            .whereEqualTo("email",email)
            .whereEqualTo("password",password)
            .get().addOnSuccessListener {
                if(it.documents.size>0){
                    //로그인이 성공됨
                    var id : String = it.documents[0].id //이건 id를 가져온다. documents[0].id의 id는 이미정해져잇는 문구이다
                    G.userAccount = UserAccount(id,email)

                    //로그인 성공했으니.. 곧바로 메인액티비티로 이동한다
                    var intent : Intent = Intent(this,MainActivity::class.java)

                    //백스택을 깔끔하게 없애고 intent를 start해야한다.
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)


                    //청소하고 메인을 시작한다.
                    startActivity(intent)

                }else{
                    AlertDialog.Builder(this).setMessage("이메일과 비밀번호를 다시 확인해주세요").show()
                    binding.etEmail.requestFocus()
                    binding.etEmail.selectAll()
                }
            }
    }
}