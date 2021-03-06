package qruz.t.qruzdriverapp.ui.main.fragments.business.mytrips

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.orhanobut.logger.Logger
import com.qruz.DriverLiveTripsQuery
import com.qruz.DriverTripsQuery
import com.qruz.data.remote.ApolloClientUtils
import qruz.t.qruzdriverapp.base.BaseApplication
import qruz.t.qruzdriverapp.data.local.DataManager

class BusinessViewModel(application: Application) : AndroidViewModel(application) {
    val dataManager: DataManager
    val responseLive: MutableLiveData<Response<DriverTripsQuery.Data>>
    val responseLiveTrips: MutableLiveData<Response<DriverLiveTripsQuery.Data>>
    val progress: MutableLiveData<Int>


    init {

        dataManager = (getApplication() as BaseApplication).dataManager!!
        responseLive = MutableLiveData<Response<DriverTripsQuery.Data>>()
        responseLiveTrips = MutableLiveData()
        progress = MutableLiveData<Int>()
     }

    fun getDriverTrips(day : String) {

        Logger.d(day.toLowerCase() +dataManager.user.id )

        progress.postValue(1)
        ApolloClientUtils.setupApollo(dataManager.accessToken)
            ?.query(DriverTripsQuery.builder().id(dataManager.user.id).day(day.toLowerCase()).build())
            ?.enqueue(object : ApolloCall.Callback<DriverTripsQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    progress.postValue(0)
                    Logger.d(e.message)
                 }

                override fun onResponse(response: Response<DriverTripsQuery.Data>) {

                    responseLive.postValue(response)
                    progress.postValue(0)
                    Logger.d(response.data())
                }
            })
    }

    fun getDriverLiveTrips() {


        progress.postValue(1)
        ApolloClientUtils.setupApollo(dataManager.accessToken)
            ?.query(DriverLiveTripsQuery.builder().id(dataManager.user.id).build())
            ?.enqueue(object : ApolloCall.Callback<DriverLiveTripsQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    progress.postValue(0)
                    Logger.d(e.message)
                }

                override fun onResponse(response: Response<DriverLiveTripsQuery.Data>) {

                    responseLiveTrips.postValue(response)
                    progress.postValue(0)
                    Logger.d(response.data())
                }
            })
    }
}