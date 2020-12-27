package qruz.t.qruzdriverapp.ui.main

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import qruz.t.qruzdriverapp.R
import qruz.t.qruzdriverapp.Utilities.CommonUtilities
import qruz.t.qruzdriverapp.base.BaseFragment
import qruz.t.qruzdriverapp.databinding.FragmentChangePasswordBinding
import qruz.t.qruzdriverapp.databinding.FragmentTempWelcomeBinding

/**
 * A simple [Fragment] subclass.
 */
class TempWelcomeFragment : BaseFragment<FragmentTempWelcomeBinding>() {

    lateinit var binding: FragmentTempWelcomeBinding

    override fun getLayoutId(): Int {
        return R.layout.fragment_temp_welcome
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = viewDataBinding

    }



}
