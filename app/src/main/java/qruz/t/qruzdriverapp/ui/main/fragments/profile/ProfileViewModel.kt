package qruz.t.qruzdriverapp.ui.main.fragments.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import qruz.t.qruzdriverapp.base.BaseApplication
import qruz.t.qruzdriverapp.data.local.DataManager

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    val dataManager: DataManager
    val progress: MutableLiveData<Int>


    init {

        dataManager = (getApplication() as BaseApplication).dataManager!!
        progress = MutableLiveData<Int>()

    }
}