package qruz.qruzdriverapp.ui.auth.restpass

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import qruz.qruzdriverapp.R
import qruz.qruzdriverapp.Utilities.CommonUtilities
import qruz.qruzdriverapp.base.BaseActivity
import qruz.qruzdriverapp.databinding.ActivityRestPasswordBinding
import qruz.qruzdriverapp.databinding.FragmentRestPasswordBinding
import qruz.qruzdriverapp.ui.main.fragments.profile.changepass.RestPasswordViewModel

class RestPasswordActivity : BaseActivity<ActivityRestPasswordBinding>() {



    lateinit var binding: ActivityRestPasswordBinding
    lateinit var viewModel: RestPasswordViewModel

    override fun getLayoutId(): Int {
        return R.layout.activity_rest_password
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RestPasswordViewModel::class.java)
        binding=viewDataBinding



        binding.resetPass.setOnClickListener(View.OnClickListener {

            if (TextUtils.isEmpty(binding.resetEmail.text.toString())) {
                Toast.makeText(this, "fill data first", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.restPass(
                    binding.resetEmail.text.toString()

                )

            }

        })
        subscribeObservers()
    }

    private fun subscribeObservers() {

        viewModel.responseLive.observe(this, Observer {


            if (!it.hasErrors()) {
                if (it.data()?.forgotPassword()?.status()!!) {
                    Toast.makeText(
                        this,
                        it.data()?.forgotPassword()?.message(),
                        Toast.LENGTH_SHORT
                    ).show()

                    onBackPressed()
                } else {
                    Toast.makeText(
                        this,
                        it.data()?.forgotPassword()?.message(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(this, it.errors().get(0).message(), Toast.LENGTH_SHORT)
                    .show()
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
