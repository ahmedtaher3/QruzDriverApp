package qruz.t.qruzdriverapp.ui.main.fragments.profile


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import org.json.JSONException
import org.json.JSONObject
import qruz.t.qruzdriverapp.Fragment.EarningsFragment
import qruz.t.qruzdriverapp.Fragment.Help
import qruz.t.qruzdriverapp.Fragment.SummaryFragment
import qruz.t.qruzdriverapp.Helper.CustomDialog
import qruz.t.qruzdriverapp.Helper.SharedHelper
import qruz.t.qruzdriverapp.Helper.URLHelper
import qruz.t.qruzdriverapp.R
import qruz.t.qruzdriverapp.Utilities.Utilities
import qruz.t.qruzdriverapp.base.BaseApplication
import qruz.t.qruzdriverapp.base.BaseFragment
import qruz.t.qruzdriverapp.databinding.FragmentProfileBinding
import qruz.t.qruzdriverapp.ui.auth.login.LoginActivity
import qruz.t.qruzdriverapp.ui.main.fragments.business.mytrips.BusinessFragment
import qruz.t.qruzdriverapp.ui.main.fragments.business.mytrips.SeatsFragment
import qruz.t.qruzdriverapp.ui.main.fragments.profile.changelang.ChangeLanguage
import qruz.t.qruzdriverapp.ui.main.fragments.profile.changepass.ChangePasswordFragment
import qruz.t.qruzdriverapp.ui.main.fragments.profile.changepass.RestPasswordFragment
import qruz.t.qruzdriverapp.ui.main.fragments.profile.update.UpdateProfileFragment
import java.util.*

class ProfileFragment : BaseFragment<FragmentProfileBinding>(), View.OnClickListener {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var fragmentProfileBinding: FragmentProfileBinding? = null
    var profileViewModel: ProfileViewModel? = null
    var utils = Utilities()
    lateinit var customDialog: CustomDialog
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentProfileBinding = viewDataBinding
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)


        token = SharedHelper.getKey(context, "access_token")

        fragmentProfileBinding?.logOut?.setOnClickListener(this)
        fragmentProfileBinding?.Summary?.setOnClickListener(this)
        fragmentProfileBinding?.Earnings?.setOnClickListener(this)
        fragmentProfileBinding?.myTrips?.setOnClickListener(this)
        fragmentProfileBinding?.changePass?.setOnClickListener(this)
        fragmentProfileBinding?.changeLang?.setOnClickListener(this)
        fragmentProfileBinding?.updateDriver?.setOnClickListener(this)
        fragmentProfileBinding?.seatsTrips?.setOnClickListener(this)

        fragmentProfileBinding?.driverName?.text = profileViewModel?.dataManager?.user?.name

        Glide.with(baseActivity).load(profileViewModel?.dataManager?.user?.avatar).placeholder(R.drawable.com_facebook_profile_picture_blank_portrait)
            .into(fragmentProfileBinding?.driverImage!!)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_profile
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.log_out -> {
                profileViewModel?.dataManager?.clear()
                startActivity(Intent(baseActivity, LoginActivity::class.java))

            }

            R.id.Summary -> {

                replace_fragment(SummaryFragment(), "SummaryFragment")

            }
            R.id.Earnings -> {

                replace_fragment(EarningsFragment(), "EarningsFragment")

            }
            R.id.changePass -> {

                replace_fragment(ChangePasswordFragment(), "ChangePasswordFragment")

            }

            R.id.myTrips -> {

                replace_fragment(BusinessFragment(), "BusinessFragment")

            }

            R.id.seatsTrips -> {

                replace_fragment(SeatsFragment(), "BusinessFragment")

            }

            R.id.changeLang -> {

                replace_fragment(ChangeLanguage(), "ChangeLanguage")

            }  R.id.updateDriver -> {

            replace_fragment(UpdateProfileFragment(), "UpdateProfileFragment")

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


    fun errorHandler(error: VolleyError) {
        utils.print("Error", error.toString())
        var json: String? = null
        val response = error.networkResponse
        if (response != null && response.data != null) {
            try {
                val errorObj = JSONObject(String(response.data))
                utils.print("ErrorHandler", "" + errorObj.toString())
                if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                    try {
                        displayMessage(errorObj.optString("message"))
                    } catch (e: Exception) {
                        displayMessage(context!!.resources.getString(R.string.something_went_wrong))
                    }
                } else if (response.statusCode == 401) {
                    SharedHelper.putKey(
                        context,
                        "loggedIn",
                        context!!.resources.getString(R.string.False)
                    )

                } else if (response.statusCode == 422) {
                    json = BaseApplication.trimMessage(String(response.data))
                    if (json !== "" && json != null) {
                        displayMessage(json)
                    } else {
                        displayMessage(context!!.resources.getString(R.string.please_try_again))
                    }
                } else if (response.statusCode == 503) {
                    displayMessage(context!!.resources.getString(R.string.server_down))
                } else {
                    displayMessage(context!!.resources.getString(R.string.please_try_again))
                }
            } catch (e: Exception) {
                displayMessage(context!!.resources.getString(R.string.something_went_wrong))
            }
        } else {
            displayMessage(context!!.resources.getString(R.string.please_try_again))
        }
    }

    fun displayMessage(toastString: String) {
        utils.print("displayMessage", "" + toastString)
        Snackbar.make(view!!, toastString, Snackbar.LENGTH_SHORT)
            .setAction("Action", null).show()
    }
}
