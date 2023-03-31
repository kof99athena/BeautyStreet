package com.nha2023.tpeverysearch.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.nha2023.tpeverysearch.G
import com.nha2023.tpeverysearch.databinding.ActivityLoginBinding
import com.nha2023.tpeverysearch.model.UserAccount

class LoginActivity : AppCompatActivity() {

    val binding:ActivityLoginBinding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //둘러보기 버튼 클릭으로 로그인없이 Main 화면으로 이동
        binding.tvGo.setOnClickListener {
            startActivity( Intent(this, MainActivity::class.java) )
            finish()
        }

        //회원가입 버튼 클릭
        binding.tvSignup.setOnClickListener {
            //회원가입화면으로 전환
            startActivity(Intent(this, SignupActivity::class.java))
        }

        //이메일 로그인 버튼 클릭
        binding.layoutEmail.setOnClickListener {
            //이메일 로그인 화면으로 전환
            startActivity(Intent(this, EmailSigninActivity::class.java))
        }

        //간편로그인 버튼들 클릭
        binding.ivLoginKakao.setOnClickListener { clickedLoginKakao() }
        binding.ivLoginGoogle.setOnClickListener { clickedLoginGoogle() }
        binding.ivLoginNaver.setOnClickListener { clickedLoginNaver() }

        //카카오 keyHash값을 얻어오자
        var keyHash : String = Utility.getKeyHash(this)
        Log.i("keyhash",keyHash)

    }//onCreate method..

    private fun clickedLoginKakao(){
        //카카오 로그인에 공통 콜백 함수가 존재한다. 이거 이해 할 줄 알아야함.
        val callback : (OAuthToken?, Throwable?)-> Unit = { token, error->
            if(token!=null){
                //토큰이 있으니까 로그인 성공
                Toast.makeText(this, "카카오로그인 성공", Toast.LENGTH_SHORT).show()

                //사용자의 정보를 요청한다 (1. 회원번호, 2.email주소)
                UserApiClient.instance.me { user, error ->
                    //매개변수로 함수를 넣은거임
                    if(user!=null){
                        var id : String = user.id.toString()
                        var email : String = user.kakaoAccount?.email ?: ""
                        //동의 안햇을수도 있으니까 널어블, 그리고!! null 이면 안되니까 엘비스 연산자로 ?: "" null일때 "" 값이 결과이다.
                        Toast.makeText(this, "$email", Toast.LENGTH_SHORT).show()
                        G.userAccount = UserAccount(id,email)

                        //개인정보 잘 얻어왓으니 메인화면으로 이동
                        startActivity(Intent(this,MainActivity::class.java))
                        finish()
                    }
                }

            }else{
                Toast.makeText(this, "카카오로그인 실패", Toast.LENGTH_SHORT).show()
            }
        }

        //카카오톡이 설치되어있으면 카톡으로 로그인하고 아니면 카카오계정으로 로그인하자
        //UserApiClient  파이어베이스 파이어스토어같은 녀석이다
        if(UserApiClient.instance.isKakaoTalkLoginAvailable(this)){
            //토큰은 내정보 받아올수있는 승차권이다. 토큰이 개인정보를 얻어올수 있는 값이다. (토큰은개인정보가아님)
            UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
            //함수 파라미터에 특정 함수를 대입할수도 있음
        }else{
            UserApiClient.instance.loginWithKakaoAccount(this, callback=callback)
        }

    }

    private fun clickedLoginGoogle(){
        //구글 로그인에 초점을 맞춰서!
        //Google에 검색하기  [안드로이드 구글 로그인]

        //구글 로그인 옵션 객체 만들기 - 어떤거 동의하는지 카카오랑 비슷
        val signOptions : GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()


        //구글 로그인 화면(액티비티)를 실행하는 Intent를 통해 로그인구현 - 카카오는 SDK에 이미 있었다.
        val intent : Intent = GoogleSignIn.getClient(this,signOptions).signInIntent
            //클라이언트지 인텐트가 아니므로 .signInIntent를 써줘야한다. 인텐트 객체를 대신 만들어준다.
        resultLauncher.launch(intent)
    }


    //구글 로그인 액티비티에 실행결과를 받아오는 계약체결대행사 만들기
    val resultLauncher : ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), object : ActivityResultCallback<ActivityResult>{
        override fun onActivityResult(result: ActivityResult?) {

            //로그인 결과를 가져오는 인텐트(택배기사) 소환
            val intent : Intent? = result?.data

            //돌아온 Intent로 부터 구글 계정 정보를 가져오는 작업을 수행
            val task : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(intent) //백그라운드 작업이라서 시간이 좀 걸림 , 툭하니 리턴 Task<GoogleSignInAccount>얘가 작업을 가져옴


            val account : GoogleSignInAccount = task.result // 작업결과 가져와
            var id : String = account.id.toString() //id는 null일수없다
            var email : String = account.email ?: "" //엘비스
            Toast.makeText(this@LoginActivity, "$email", Toast.LENGTH_SHORT).show() //this@LoginActivity 람다식이 아니라 객체 안에 있으니까

            G.userAccount = UserAccount(id,email)

            //메인으로 이동
            startActivity(Intent(this@LoginActivity,MainActivity::class.java))
            finish()


        }

    })



    private fun clickedLoginNaver(){

    }
}



















