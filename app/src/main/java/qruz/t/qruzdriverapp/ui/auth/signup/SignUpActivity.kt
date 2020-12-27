package qruz.t.qruzdriverapp.ui.auth.signup
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import qruz.t.qruzdriverapp.Helper.SharedHelper
import qruz.t.qruzdriverapp.R
import qruz.t.qruzdriverapp.Utilities.CommonUtilities
import qruz.t.qruzdriverapp.base.BaseActivity
import qruz.t.qruzdriverapp.databinding.ActivityLoginBinding
import qruz.t.qruzdriverapp.databinding.ActivitySignUpBinding
import qruz.t.qruzdriverapp.model.User
import qruz.t.qruzdriverapp.ui.auth.login.LoginViewModel
import qruz.t.qruzdriverapp.ui.main.MainActivity

class SignUpActivity : BaseActivity<ActivitySignUpBinding>() {


    lateinit var binding: ActivitySignUpBinding
    lateinit var viewModel: SignUpViewModel

    override fun getLayoutId(): Int {
        return R.layout.activity_sign_up
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = viewDataBinding
        viewModel = ViewModelProviders.of(this).get(SignUpViewModel::class.java)

        binding.signUp.setOnClickListener(View.OnClickListener {

            if (binding.userName.text.toString().isNullOrEmpty() ||
                binding.userMobile.text.toString().isNullOrEmpty() ||
                binding.userVehicle.text.toString().isNullOrEmpty() ||
                binding.userCity.text.toString().isNullOrEmpty()
            ) {

                Toast.makeText(this, "please complete data", Toast.LENGTH_LONG).show();

                return@OnClickListener
            }

            viewModel.signUp(
                binding.userName.text.toString(),
                binding.userMobile.text.toString(),
                binding.userVehicle.text.toString(),
                binding.userCity.text.toString()
            )

        })

        binding.cancel.setOnClickListener(View.OnClickListener {
            finish()
        })


        setObserves()

    }



    fun setObserves() {
        viewModel?.responseLive?.observe(this, Observer { response ->

            if (!response.hasErrors()) {
                Toast.makeText(
                    this@SignUpActivity,
                    "Your request has been sent successfully.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()

            } else {

                runOnUiThread(Runnable {
                    Toast.makeText(
                        this@SignUpActivity,
                        response.errors()[0].message(),
                        Toast.LENGTH_SHORT
                    ).show()
                })
                Logger.d(response.errors().toString())

            }

        })
        viewModel?.progress?.observe(this, Observer { progress ->

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


}