package qruz.t.qruzdriverapp.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.orhanobut.logger.Logger
import qruz.t.qruzdriverapp.Fragment.Map
import qruz.t.qruzdriverapp.R
import qruz.t.qruzdriverapp.Utilities.CommonUtilities
import qruz.t.qruzdriverapp.base.BaseActivity
import qruz.t.qruzdriverapp.base.BaseApplication
import qruz.t.qruzdriverapp.data.local.DataManager
import qruz.t.qruzdriverapp.databinding.ActivityMainBinding
import qruz.t.qruzdriverapp.ui.main.fragments.business.map.MapFragment
import qruz.t.qruzdriverapp.ui.main.fragments.profile.ProfileFragment

class MainActivity : BaseActivity<ActivityMainBinding>(),
    BottomNavigationView.OnNavigationItemSelectedListener {
    var activityMainBinding: ActivityMainBinding? = null
    var mainViewModel: MainViewModel? = null
    var active: String? = null
    var live: Boolean? = false
    var dataManager: DataManager? = null

    val MY_PERMISSIONS_REQUEST_FINE_LOCATION = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = viewDataBinding
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        dataManager = (getApplication() as BaseApplication).dataManager!!
        activityMainBinding?.navView?.setOnNavigationItemSelectedListener(this)

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_FINE_LOCATION
            )
        } else {

            // Permission has already been granted

            replace_fragment(TempWelcomeFragment(), "TempWelcomeFragment")

        }


    }



    override fun getLayoutId(): Int {
        return R.layout.activity_main

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {


            R.id.business -> {
                if (!active.equals("TempWelcomeFragment")) {

                        replace_fragment(TempWelcomeFragment(), "TempWelcomeFragment")

                }

            }

            R.id.account -> {


                if (!active.equals("ProfileFragment")) {
                    replace_fragment(ProfileFragment(), "ProfileFragment")

                } else {

                    if (supportFragmentManager.backStackEntryCount > 1) {
                        onBackPressed()
                    }

                }


            }
        }
        return true
    }


    private fun replace_fragment(fragment: Fragment, tag: String) {
        active = tag
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        getSupportFragmentManager()
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

    override fun onBackPressed() {


        if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
        } else {
            super.onBackPressed()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    replace_fragment(TempWelcomeFragment(), "TempWelcomeFragment")

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    finish()
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }


}