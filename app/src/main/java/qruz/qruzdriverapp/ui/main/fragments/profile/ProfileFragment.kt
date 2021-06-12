package qruz.qruzdriverapp.ui.main.fragments.profile


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
import qruz.qruzdriverapp.Fragment.EarningsFragment
import qruz.qruzdriverapp.Fragment.Help
import qruz.qruzdriverapp.Fragment.SummaryFragment
import qruz.qruzdriverapp.Helper.CustomDialog
import qruz.qruzdriverapp.Helper.SharedHelper
import qruz.qruzdriverapp.Helper.URLHelper
import qruz.qruzdriverapp.R
import qruz.qruzdriverapp.Utilities.Utilities
import qruz.qruzdriverapp.base.BaseApplication
import qruz.qruzdriverapp.base.BaseFragment
import qruz.qruzdriverapp.databinding.FragmentProfileBinding
import qruz.qruzdriverapp.ui.auth.login.LoginActivity
import qruz.qruzdriverapp.ui.main.fragments.business.mytrips.BusinessFragment
import qruz.qruzdriverapp.ui.main.fragments.profile.changelang.ChangeLanguage
import qruz.qruzdriverapp.ui.main.fragments.profile.changepass.ChangePasswordFragment
import qruz.qruzdriverapp.ui.main.fragments.profile.changepass.RestPasswordFragment
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
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

        fragmentProfileBinding?.driverName?.text = profileViewModel?.dataManager?.user?.name

        Glide.with(baseActivity).load(profileViewModel?.dataManager?.user?.avatar)
            .into(fragmentProfileBinding?.driverImage!!)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_profile
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.log_out -> {
                customDialog = CustomDialog(activity)
                customDialog.setCancelable(false)
                customDialog.show()

                val param = JSONObject()
                try {
                    param.put("service_status", "offline")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
                    Method.POST,
                    URLHelper.UPDATE_AVAILABILITY_API,
                    param,
                    Response.Listener { response ->
                        customDialog.dismiss()
                        if (response != null) {


                            profileViewModel?.dataManager?.clear()
                            startActivity(Intent(baseActivity, LoginActivity::class.java))
                            baseActivity.finish()

                        }
                    },
                    Response.ErrorListener { error ->
                        customDialog.dismiss()
                        utils.print("Error", error.toString())
                        errorHandler(error)
                    }) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val headers =
                            HashMap<String, String>()
                        headers["X-Requested-With"] = "XMLHttpRequest"
                        headers["Authorization"] = "Bearer $token"
                        return headers
                    }
                }
                BaseApplication.getInstance().addToRequestQueue(jsonObjectRequest)



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
            R.id.changeLang -> {

                replace_fragment(ChangeLanguage(), "ChangeLanguage")

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
