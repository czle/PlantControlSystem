package com.example.a20190418test

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.support.annotation.NonNull
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.internal.ApiExceptionUtil
import com.google.android.gms.tasks.OnCanceledListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    lateinit var mFirebaseAnalytics: FirebaseAnalytics
    lateinit var items: ArrayList<LOGINDTO>
    lateinit var jArray: JSONArray
    lateinit var jObj: JSONObject
    var LoginResult = 0
    private var RC_SIGN_IN: Int = 1000
    private lateinit var auth: FirebaseAuth
    lateinit var mGoogleApiClient: GoogleApiClient
    lateinit var googleSignInClient: GoogleSignInClient

    var mRootRef: DatabaseReference = FirebaseDatabase.getInstance().reference
    var conditionRef: DatabaseReference = mRootRef.child("id")
    var pwdRef: DatabaseReference = mRootRef.child("pwd")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        auth = FirebaseAuth.getInstance();
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        google_login.setOnClickListener {
            signIn()
        }
        BtnLogin.setOnClickListener {

            BtnLogin.setBackgroundResource(R.drawable.loginsquare2)
            conditionRef.setValue(editTxtID.text.toString())
            pwdRef.setValue(editTxtPW.text.toString())



            LoginService(editTxtID.text.toString(), editTxtPW.text.toString())

        }
        btnJoin.setOnClickListener {
            finish()
            startActivity<JoinActivity>()

        }
        btnForwardFindPassword.setOnClickListener {
            finish()
            startActivity<findPassword>()
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
                toast("로그인 성공")
                startActivity<MainActivity>()
            } catch (e: ApiException) {
                toast("로그인 실패")
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                } else {
                    toast("로그인")
                }

            }
    }

    override fun onResume() {
        super.onResume()
        LoginResult = 0
        BtnLogin.setBackgroundResource(R.drawable.loginsquare)
        FirebaseAuth.getInstance().signOut()
    }


    override fun onConnectionFailed(p0: ConnectionResult) {
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        startActivity<StartActivity>()
    }

    private fun LoginService(ID: String, PWD: String) {

        if (!ID.isEmpty() || !PWD.isEmpty()) {
            auth.signInWithEmailAndPassword(ID, PWD)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        toast("로그인에 성공하였습니다.")
                        Handler().postDelayed({
                            startActivity<MainActivity>()
                        }, 1000)

                    } else {
                        toast("로그인에 실패하였습니다.")
                    }

                    // ...
                }
        } else {
            toast("아이디 혹은 비밀번호를 입력해주세요")
        }

    }


}
