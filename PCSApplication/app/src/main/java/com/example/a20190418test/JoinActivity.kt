package com.example.a20190418test

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_join.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class JoinActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        auth = FirebaseAuth.getInstance()

        btnJoinSuccess.setOnClickListener {

            var ID = editTxtIDEmail.text.toString()
            var PWD = editTxtPWD.text.toString()
            var PWDCheck = editTxtPWDCheck.text.toString()

            if (ID.length > 0 && PWD.length > 0 && PWDCheck.length > 0) {

                if (PWD.equals(PWDCheck)) {
                    auth.createUserWithEmailAndPassword(ID, PWD)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                toast("가입을 축하드립니다.")
                                val user = auth.currentUser
                                startActivity<LoginActivity>()
                            } else {
                                if (task.exception!!.toString() == "com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account.")
                                    toast("이미 가입되어있는 아이디 입니다.")
                                Log.d("mew", "createUserWithEmail:failure", task.exception)
                            }
                        }
                } else {
                    toast("비밀번호가 일치하지 않습니다.")
                }
            } else {
                toast("아이디 혹은 비밀번호를 입력해주세요")
            }

        }


    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
    }

}
