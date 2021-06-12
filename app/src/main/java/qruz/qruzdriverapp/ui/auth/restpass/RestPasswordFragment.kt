package qruz.qruzdriverapp.ui.main.fragments.profile.changepass

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import qruz.qruzdriverapp.R
import qruz.qruzdriverapp.Utilities.CommonUtilities
import qruz.qruzdriverapp.base.BaseFragment
import qruz.qruzdriverapp.databinding.FragmentRestPasswordBinding

/**
 * A simple [Fragment] subclass.
 */
class RestPasswordFragment : BaseFragment<FragmentRestPasswordBinding>() {

    lateinit var binding: FragmentRestPasswordBinding
    lateinit var viewModel: RestPasswordViewModel

    override fun getLayoutId(): Int {
        return R.layout.fragment_rest_password
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = viewDataBinding
        viewModel = ViewModelProviders.of(this).get(RestPasswordViewModel::class.java)

        binding.resetPass.setOnClickListener(View.OnClickListener {

            if (TextUtils.isEmpty(binding.resetEmail.text.toString())) {
                Toast.makeText(baseActivity, "fill data first", Toast.LENGTH_SHORT).show()
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
                        baseActivity,
                        it.data()?.forgotPassword()?.message(),
                        Toast.LENGTH_SHORT
                    ).show()

                    baseActivity.onBackPressed()
                } else {
                    Toast.makeText(
                        baseActivity,
                        it.data()?.forgotPassword()?.message(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(baseActivity, it.errors().get(0).message(), Toast.LENGTH_SHORT)
                    .show()
            }

        })


        viewModel?.progress?.observe(this, Observer { progress ->

            when (progress) {
                0 -> {
                    CommonUtilities.hideDialog()
                }
                1 -> {
                    CommonUtilities.showStaticDialog(baseActivity)
                }
            }
        })

    }


}
