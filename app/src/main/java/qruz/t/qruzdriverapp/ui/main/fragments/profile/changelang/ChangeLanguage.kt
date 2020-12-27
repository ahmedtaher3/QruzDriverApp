package qruz.t.qruzdriverapp.ui.main.fragments.profile.changelang

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import qruz.t.qruzdriverapp.R
import qruz.t.qruzdriverapp.Utilities.LocaleUtils
import qruz.t.qruzdriverapp.base.BaseApplication
import qruz.t.qruzdriverapp.base.BaseFragment
import qruz.t.qruzdriverapp.data.local.DataManager
import qruz.t.qruzdriverapp.databinding.FragmentChangeLanguageBinding
import qruz.t.qruzdriverapp.ui.auth.splach.SplashActivity
import java.util.*

class ChangeLanguage : BaseFragment<FragmentChangeLanguageBinding>() {


    lateinit var binding: FragmentChangeLanguageBinding
    lateinit var dataManager: DataManager
    lateinit var languageDialog: AlertDialog

    override fun getLayoutId(): Int {
        return R.layout.fragment_change_language
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = viewDataBinding
        dataManager = (baseActivity.getApplication() as BaseApplication).getDataManager()

        val language: String = dataManager.getLang()

        if (language == "en") {
            binding.changeLangTextView.setText("English")
        } else {
            binding.changeLangTextView.setText("عربى")
        }

        binding.changeLang.setOnClickListener(View.OnClickListener {

            languageDialog = AlertDialog.Builder(baseActivity).create()

            val factory = LayoutInflater.from(baseActivity)
            val languageDialogview: View =
                factory.inflate(R.layout.language_change_popup, null)
            if (languageDialog != null && languageDialog.isShowing()) {
                return@OnClickListener
            }

            languageDialog.setCancelable(false)
            languageDialog.setView(languageDialogview)
            languageDialog.show()

            languageDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val imvLogoChangeLanguage =
                languageDialogview.findViewById<View>(R.id.imvLogoChangeLanguage) as ImageView
            val radioGroup =
                languageDialogview.findViewById<View>(R.id.myRadioGroup) as RadioGroup
            val rdbEnglish =
                languageDialogview.findViewById<View>(R.id.rdbEnglish) as RadioButton
            val rdbArabic =
                languageDialogview.findViewById<View>(R.id.rdbArabic) as RadioButton
            val dialogButton =
                languageDialogview.findViewById<View>(R.id.btn_update) as Button
            val language = dataManager.lang

            if (language == "en") {
                rdbEnglish.isChecked = true
            } else {
                rdbArabic.isChecked = true
            }
            dialogButton.setOnClickListener {
                if (rdbArabic.isChecked) {
                    LocaleUtils.setLocale(Locale("ar"))
                    LocaleUtils.updateConfig(
                        baseActivity.getApplication(),
                        baseActivity.getResources().getConfiguration()
                    )
                    dataManager.saveLang("ar")
                    startActivity(Intent(baseActivity, SplashActivity::class.java))
                    baseActivity.finish()
                    languageDialog.dismiss()
                } else {
                    LocaleUtils.setLocale(Locale("en"))
                    LocaleUtils.updateConfig(
                        baseActivity.getApplication(),
                        baseActivity.getResources().getConfiguration()
                    )
                    dataManager.saveLang("en")
                    startActivity(Intent(baseActivity, SplashActivity::class.java))
                    baseActivity.finish()
                    languageDialog.dismiss()
                }
            }

            languageDialogview.setOnClickListener { if (languageDialog != null) languageDialog.dismiss() }

        })
    }

}