package qruz.t.qruzdriverapp.ui.auth.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.facebook.CallbackManager
import com.google.android.material.shape.CornerFamily
import com.google.gson.Gson
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import qruz.t.qruzdriverapp.Helper.SharedHelper
import qruz.t.qruzdriverapp.R
import qruz.t.qruzdriverapp.Utilities.CommonUtilities
import qruz.t.qruzdriverapp.base.BaseActivity
import qruz.t.qruzdriverapp.databinding.ActivityLoginBinding
import qruz.t.qruzdriverapp.model.User
import qruz.t.qruzdriverapp.ui.auth.restpass.RestPasswordActivity
import qruz.t.qruzdriverapp.ui.auth.signup.SignUpActivity
import qruz.t.qruzdriverapp.ui.main.MainActivity


class LoginActivity : BaseActivity<ActivityLoginBinding?>(), View.OnClickListener {

    var activityLoginBinding: ActivityLoginBinding? = null
    var loginViewModel: LoginViewModel? = null
    var callbackManager: CallbackManager? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.addLogAdapter(AndroidLogAdapter())

        activityLoginBinding = viewDataBinding
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)





        if (loginViewModel?.dataManager?.loggingMode!!) {

            startActivity(Intent(this, MainActivity::class.java))
            finish()

        }




        setClickLisener()
        setObservers()

    }

    fun setClickLisener() {
        activityLoginBinding!!.SignIn.setOnClickListener(this)
        activityLoginBinding!!.ForgetPassword.setOnClickListener(this)
        activityLoginBinding!!.SignUp.setOnClickListener(this)
    }


    fun setObservers() {
        loginViewModel?.responseLive?.observe(this, Observer { response ->

            if (!response.hasErrors()) {

                Logger.d(response.data()?.driverLogin()?.driver().toString())

                SharedHelper.putKey(
                    this@LoginActivity,
                    "access_token",
                    response.data()?.driverLogin()?.access_token()
                )

                var user = User()
                user.id = response.data()?.driverLogin()?.driver()?.id().toString()
                user.name = response.data()?.driverLogin()?.driver()?.name()
                user.email = response.data()?.driverLogin()?.driver()?.email()
                user.phone = response.data()?.driverLogin()?.driver()?.phone()
                user.avatar = response.data()?.driverLogin()?.driver()?.avatar().toString()

                user.licenseExpiresOn =
                    response.data()?.driverLogin()?.driver()?.license_expires_on().toString()

                Logger.d(response.data()?.driverLogin()?.access_token())

                loginViewModel?.dataManager?.saveLoggingMode(true)

                val gson = Gson()
                val json = gson.toJson(user)
                loginViewModel?.dataManager?.saveUser(json)

                loginViewModel?.dataManager?.saveAccessToken(response.data()?.driverLogin()?.access_token())

                startActivity(Intent(this, MainActivity::class.java))
                finish()

            } else {

                runOnUiThread(Runnable {
                    Toast.makeText(
                        this@LoginActivity,
                        response.errors()[0].message(),
                        Toast.LENGTH_SHORT
                    ).show()
                })
                Logger.d(response.errors().toString())

            }

        })
        loginViewModel?.progress?.observe(this, Observer { progress ->

            when (progress) {
                0 -> {
                    CommonUtilities.hideDialog()
                }
                1 -> {
                    CommonUtilities.showStaticDialog(this)
                }
            }
        })
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {


            R.id.SignIn -> {
                loginViewModel?.login(
                    activityLoginBinding?.userEmail?.text.toString(),
                    activityLoginBinding?.userPass?.text.toString()
                )

            }

            R.id.ForgetPassword -> {

                startActivity(Intent(this, RestPasswordActivity::class.java))
            }

            R.id.SignUp -> {

                startActivity(Intent(this, SignUpActivity::class.java))
            }


        }
    }


}