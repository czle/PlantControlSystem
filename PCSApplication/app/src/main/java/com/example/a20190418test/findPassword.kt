package com.example.a20190418test

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_find_password.*
import org.jetbrains.anko.toast

class findPassword : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_password)

        btnFindPassword.setOnClickListener {
            var pass: String = findPasswordEditTxt.text.toString()
            if (pass.length > 0) {

                val auth = FirebaseAuth.getInstance()

                auth.sendPasswordResetEmail(pass)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            toast("이메일 전송이 완료되었습니다.")
                        }else{
                            toast("등록되지 않은 이메일 입니다.")
                        }
                    }
            } else {
                toast("이메일을 입력해주세요")
            }


        }

    }
}
