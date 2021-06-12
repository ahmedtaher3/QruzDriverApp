package qruz.t.qruzdriverapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import qruz.t.qruzdriverapp.Fragment.EarningsFragment
import qruz.t.qruzdriverapp.Fragment.SummaryFragment
import qruz.t.qruzdriverapp.R
import qruz.t.qruzdriverapp.Utilities.CommonUtilities
import qruz.t.qruzdriverapp.base.BaseFragment
import qruz.t.qruzdriverapp.databinding.FragmentChangePasswordBinding
import qruz.t.qruzdriverapp.databinding.FragmentTempWelcomeBinding
import qruz.t.qruzdriverapp.ui.auth.login.LoginActivity
import qruz.t.qruzdriverapp.ui.main.fragments.business.mytrips.BusinessFragment
import qruz.t.qruzdriverapp.ui.main.fragments.business.mytrips.SeatsFragment
import qruz.t.qruzdriverapp.ui.main.fragments.profile.changelang.ChangeLanguage
import qruz.t.qruzdriverapp.ui.main.fragments.profile.changepass.ChangePasswordFragment
import qruz.t.qruzdriverapp.ui.main.fragments.profile.update.UpdateProfileFragment

/**
 * A simple [Fragment] subclass.
 */
class TempWelcomeFragment : BaseFragment<FragmentTempWelcomeBinding>(), View.OnClickListener {

    lateinit var binding: FragmentTempWelcomeBinding
    var mainViewModel: MainViewModel? = null
    override fun getLayoutId(): Int {
        return R.layout.fragment_temp_welcome
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = viewDataBinding
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)


        binding?.driverName?.text = mainViewModel?.dataManager?.user?.name

        Glide.with(baseActivity).load(mainViewModel?.dataManager?.user?.avatar)
            .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait)
            .into(binding?.driverImage!!)


        binding?.myTrips.setOnClickListener(this)
        binding?.seatsTrips.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {


            R.id.myTrips -> {

                replace_fragment(BusinessFragment(), "BusinessFragment")

            }

            R.id.seatsTrips -> {

                replace_fragment(SeatsFragment(), "BusinessFragment")

            }


        }
    }

    private fun replace_fragment(fragment: Fragment, tag: String) {
        // baseActivity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        baseActivity.getSupportFragmentManager()
            .beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_left,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_left
            )
            .add(
                R.id.Main_Container,
                fragment
            )
            .addToBackStack(tag)
            .commit()
    }


}
