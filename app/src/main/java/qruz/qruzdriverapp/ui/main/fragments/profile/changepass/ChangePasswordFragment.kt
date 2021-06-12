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
import qruz.qruzdriverapp.databinding.FragmentChangePasswordBinding

/**
 * A simple [Fragment] subclass.
 */
class ChangePasswordFragment : BaseFragment<FragmentChangePasswordBinding>() {

    lateinit var binding: FragmentChangePasswordBinding
    lateinit var viewModel: ChangePasswordViewModel

    override fun getLayoutId(): Int {
        return R.layout.fragment_change_password
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = viewDataBinding
        viewModel = ViewModelProviders.of(this).get(ChangePasswordViewModel::class.java)

        binding.changePass.setOnClickListener(View.OnClickListener {

            if (TextUtils.isEmpty(binding.currentPassword.text.toString()) || TextUtils.isEmpty(
                    binding.newPassword.text.toString()
                ) || TextUtils.isEmpty(binding.newPasswordConfirmation.text.toString())
            ) {
                Toast.makeText(baseActivity, "fill data first", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.getDriverTrips(
                    binding.currentPassword.text.toString(),
                    binding.newPassword.text.toString(),
                    binding.newPasswordConfirmation.text.toString()
                )

            }

        })
        subscribeObservers()
    }

    private fun subscribeObservers() {

        viewModel.responseLive.observe(this, Observer {


            if (!it.hasErrors()) {
                if (it.data()?.changeDriverPassword()?.status()!!) {
                    Toast.makeText(
                        baseActivity,
                        it.data()?.changeDriverPassword()?.message(),
                        Toast.LENGTH_SHORT
                    ).show()

                    baseActivity.onBackPressed()
                } else {
                    Toast.makeText(
                        baseActivity,
                        it.data()?.changeDriverPassword()?.message(),
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
