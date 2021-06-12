package qruz.qruzdriverapp.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.orhanobut.logger.Logger
import com.qruz.DriverLiveTripQuery
import com.qruz.data.remote.ApolloClientUtils
import qruz.qruzdriverapp.base.BaseApplication
import qruz.qruzdriverapp.data.local.DataManager

class MainViewModel (application: Application) : AndroidViewModel(application) {
    val dataManager: DataManager
    val responseLive: MutableLiveData<Response<DriverLiveTripQuery.Data>>
    val progress: MutableLiveData<Int>

    init {
        dataManager = (getApplication() as BaseApplication).dataManager!!
        responseLive = MutableLiveData<Response<DriverLiveTripQuery.Data>>()
        progress = MutableLiveData<Int>()
    }

    public fun checkLiveTrip(driverID: String) {
        progress.postValue(1)
        ApolloClientUtils.setupApollo(dataManager.accessToken)?.query(DriverLiveTripQuery.builder().id(driverID).build())
            ?.enqueue(object : ApolloCall.Callback<DriverLiveTripQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    progress.postValue(0)
                    Logger.d(e.message)

                }

                override fun onResponse(response: Response<DriverLiveTripQuery.Data>) {

                    responseLive.postValue(response)
                    progress.postValue(0)

                }
            })
    }


}