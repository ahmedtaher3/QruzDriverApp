package qruz.t.qruzdriverapp.ui.main.fragments.business.seats

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.orhanobut.logger.Logger
import com.qruz.*
import com.qruz.data.remote.ApolloClientUtils
import qruz.t.qruzdriverapp.R
import qruz.t.qruzdriverapp.Utilities.CommonUtilities
import qruz.t.qruzdriverapp.base.BaseApplication
import qruz.t.qruzdriverapp.data.local.DataManager

class SeatsViewModel (application: Application) : AndroidViewModel(application) {
    val dataManager: DataManager
    val responseLive: MutableLiveData<Response<SeatTripQuery.Data>>
    val responseLiveStartTrip: MutableLiveData<Response<StartSeatTripMutation.Data>>
    val responseLiveDetails: MutableLiveData<Response<SingleSeatsTripTransactionsQuery.Data>>
    val responseLiveEndTrip: MutableLiveData<Response<EndSeatsTripMutation.Data>>
    val progress: MutableLiveData<Int>

    init {
        dataManager = (getApplication() as BaseApplication).dataManager!!
        responseLive = MutableLiveData()
        responseLiveDetails = MutableLiveData()
        responseLiveStartTrip = MutableLiveData()
        responseLiveEndTrip = MutableLiveData<Response<EndSeatsTripMutation.Data>>()
        progress = MutableLiveData<Int>()
    }

    public fun tripLiveTrip(driverID: String) {
        progress.postValue(1)

        Logger.d(dataManager.accessToken + "      \n   "  + driverID)

        ApolloClientUtils.setupApollo(dataManager.accessToken)?.query(SeatTripQuery.builder().id(driverID).build())
            ?.enqueue(object : ApolloCall.Callback<SeatTripQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    progress.postValue(0)
                    Logger.d(e.message)
                    Logger.d(e.localizedMessage)
                    Logger.d(e.cause)

                }

                override fun onResponse(response: Response<SeatTripQuery.Data>) {
                    progress.postValue(0)
                    responseLive.postValue(response)


                }
            })
    }


    public fun tripDetails(startAt: String) {
        progress.postValue(1)


        ApolloClientUtils.setupApollo(dataManager.accessToken)?.query(SingleSeatsTripTransactionsQuery.builder()
            .trip_id(dataManager.tripId)
            .trip_time(startAt)
            .build())
            ?.enqueue(object : ApolloCall.Callback<SingleSeatsTripTransactionsQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    progress.postValue(0)
                    Logger.d(e.message)
                    Logger.d(e.localizedMessage)
                    Logger.d(e.cause)

                }

                override fun onResponse(response: Response<SingleSeatsTripTransactionsQuery.Data>) {
                    progress.postValue(0)
                    responseLiveDetails.postValue(response)


                }
            })
    }


    public fun startTrip(tripTime: String , tripId: String , lat: String , long: String) {
        Logger.d(tripId)
        progress.postValue(1)
        ApolloClientUtils.setupApollo(dataManager.accessToken)?.mutate(StartSeatTripMutation.builder().trip_time(tripTime).trip_id(tripId).latitude(lat).longitude(long).build())
            ?.enqueue(object : ApolloCall.Callback<StartSeatTripMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                    progress.postValue(0)
                    Logger.d(e.message)

                }

                override fun onResponse(response: Response<StartSeatTripMutation.Data>) {
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



    public fun updateSeatsTripDriverLocation(lat: String , long: String) {


        ApolloClientUtils.setupApollo(dataManager.accessToken)?.mutate(UpdateSeatsTripDriverLocationMutation.builder().log_id(dataManager.logId).latitude(lat).longitude(long).build())
            ?.enqueue(object : ApolloCall.Callback<UpdateSeatsTripDriverLocationMutation.Data>() {
                override fun onFailure(e: ApolloException) {


                }

                override fun onResponse(response: Response<UpdateSeatsTripDriverLocationMutation.Data>) {

                    Logger.d("doneee")

                }
            })
    }


    public fun endTrip(tripId: String, log_id: String  , lat: String , long: String) {

        progress.postValue(1)
        ApolloClientUtils.setupApollo(dataManager.accessToken)?.mutate(EndSeatsTripMutation.builder().trip_id(tripId).log_id(log_id).latitude(lat).longitude(long).build())
            ?.enqueue(object : ApolloCall.Callback<EndSeatsTripMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                    progress.postValue(0)
                    Logger.d(e.message)

                }

                override fun onResponse(response: Response<EndSeatsTripMutation.Data>) {
                    progress.postValue(0)
                    responseLiveEndTrip.postValue(response)


                }
            })
    }




    public fun sendNotification(stationID: String,
                                stationName: String,
                                tripName: String,
                                lat: String , long: String) {

        progress.postValue(1)
        ApolloClientUtils.setupApollo(this.dataManager.getAccessToken())
           ?.mutate(
                NearYouMutation.builder()
                    .station_id(stationID)
                    .station_name(stationName)
                    .trip_id(this.dataManager.getTripId())
                    .latitude(lat)
                    .longitude(long)
                    .log_id(this.dataManager.getLogId())
                    .trip_name(tripName)
                    .build()
            )?.enqueue(object : ApolloCall.Callback<NearYouMutation.Data?>() {
                override fun onResponse(response: Response<NearYouMutation.Data?>) {


                    if (response.hasErrors())
                    {
                        progress.postValue(0)
                        Logger.d(response.errors())

                    }
                    else
                    {
                        progress.postValue(2)
                        Logger.d(response.data())

                    }

                }

                override fun onFailure(apolloException: ApolloException) {

                    progress.postValue(0)
                    Logger.d(apolloException.message)
                }
            })

    }
}