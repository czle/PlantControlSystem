package com.example.a20190418test

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import org.jetbrains.anko.startActivity

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start)

        Handler().postDelayed({
            startActivity<MainActivity>()

        }, 2000)
    }

    override fun onResume() {
        super.onResume()

        Handler().postDelayed({
            finish()
            startActivity<LoginActivity>()

        }, 2000)
    }
}
