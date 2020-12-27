package qruz.t.qruzdriverapp.ui.dialogs.startion

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.orhanobut.logger.Logger
 import com.qruz.data.remote.ApolloClientUtils
import qruz.t.qruzdriverapp.base.BaseApplication
import qruz.t.qruzdriverapp.data.local.DataManager

class StationViewModel(application: Application) : AndroidViewModel(application) {
    val dataManager: DataManager
     val progress: MutableLiveData<Int>


    init {

        dataManager = (getApplication() as BaseApplication).dataManager!!
         progress = MutableLiveData<Int>()
     }



}