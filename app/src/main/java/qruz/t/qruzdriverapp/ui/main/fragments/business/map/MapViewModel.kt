package qruz.t.qruzdriverapp.ui.main.fragments.business.map

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.orhanobut.logger.Logger
import com.qruz.*
import com.qruz.data.remote.ApolloClientUtils
import qruz.t.qruzdriverapp.base.BaseApplication
import qruz.t.qruzdriverapp.data.local.DataManager

class MapViewModel (application: Application) : AndroidViewModel(application) {
    val dataManager: DataManager
    val responseLive: MutableLiveData<Response<TripQuery.Data>>
    val responseLiveStartTrip: MutableLiveData<Response<StartTripMutation.Data>>
    val responseLiveEndTrip: MutableLiveData<Response<EndTripMutation.Data>>
    val progress: MutableLiveData<Int>

    init {
        dataManager = (getApplication() as BaseApplication).dataManager!!
        responseLive = MutableLiveData<Response<TripQuery.Data>>()
        responseLiveStartTrip = MutableLiveData<Response<StartTripMutation.Data>>()
        responseLiveEndTrip = MutableLiveData<Response<EndTripMutation.Data>>()
        progress = MutableLiveData<Int>()
    }

    public fun tripLiveTrip(driverID: String) {
        progress.postValue(1)

        Logger.d(dataManager.accessToken + "      \n   "  + driverID)

        ApolloClientUtils.setupApollo(dataManager.accessToken)?.query(TripQuery.builder().id(driverID).build())
            ?.enqueue(object : ApolloCall.Callback<TripQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    progress.postValue(0)
                    Logger.d(e.message)
                    Logger.d(e.localizedMessage)
                    Logger.d(e.cause)

                }

                override fun onResponse(response: Response<TripQuery.Data>) {
                    progress.postValue(0)
                    responseLive.postValue(response)


                }
            })
    }

    public fun startTrip(tripId: String , lat: String , long: String) {
        Logger.d(tripId)
        progress.postValue(1)
        ApolloClientUtils.setupApollo(dataManager.accessToken)?.mutate(StartTripMutation.builder().trip_id(tripId).latitude(lat).longitude(long).build())
            ?.enqueue(object : ApolloCall.Callback<StartTripMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                    progress.postValue(0)
                    Logger.d(e.message)

                }

                override fun onResponse(response: Response<StartTripMutation.Data>) {
                    progress.postValue(0)
                    responseLiveStartTrip.postValue(response)


                }
            })
    }

    public fun updateBusinessTripDriverLocation(lat: String , long: String) {


        ApolloClientUtils.setupApollo(dataManager.accessToken)?.mutate(UpdateBusinessTripDriverLocationMutation.builder().trip_id(dataManager.tripId).log_id(dataManager.logId).latitude(lat).longitude(long).build())
            ?.enqueue(object : ApolloCall.Callback<UpdateBusinessTripDriverLocationMutation.Data>() {
                override fun onFailure(e: ApolloException) {


                }

                override fun onResponse(response: Response<UpdateBusinessTripDriverLocationMutation.Data>) {

                    Logger.d("doneee")

                }
            })
    }

    public fun endTrip(tripId: String, log_id: String  , lat: String , long: String) {

        progress.postValue(1)
        ApolloClientUtils.setupApollo(dataManager.accessToken)?.mutate(EndTripMutation.builder().trip_id(tripId).log_id(log_id).latitude(lat).longitude(long).build())
            ?.enqueue(object : ApolloCall.Callback<EndTripMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                    progress.postValue(0)
                    Logger.d(e.message)

                }

                override fun onResponse(response: Response<EndTripMutation.Data>) {
                    progress.postValue(0)
                    responseLiveEndTrip.postValue(response)


                }
            })
    }
}