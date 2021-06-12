package qruz.qruzdriverapp.ui.main.fragments.profile.changepass

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.orhanobut.logger.Logger
import com.qruz.ChangeDriverPasswordMutation
import com.qruz.DriverTripsQuery
import com.qruz.data.remote.ApolloClientUtils
import qruz.qruzdriverapp.base.BaseApplication
import qruz.qruzdriverapp.data.local.DataManager

class ChangePasswordViewModel(application: Application) : AndroidViewModel(application) {
    val dataManager: DataManager
    val responseLive: MutableLiveData<Response<ChangeDriverPasswordMutation.Data>>
    val progress: MutableLiveData<Int>


    init {

        dataManager = (getApplication() as BaseApplication).dataManager!!
        responseLive = MutableLiveData<Response<ChangeDriverPasswordMutation.Data>>()
        progress = MutableLiveData<Int>()

    }

    fun getDriverTrips(current_password :String,new_password :String,new_password_confirmation :String) {


        progress.postValue(1)
        ApolloClientUtils.setupApollo(dataManager.accessToken)
            ?.mutate(ChangeDriverPasswordMutation.builder().id(dataManager.user.id).current_password(current_password).new_password(new_password).new_password_confirmation(new_password_confirmation).build())
            ?.enqueue(object : ApolloCall.Callback<ChangeDriverPasswordMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                    progress.postValue(0)
                    Logger.d(e.message)
                }

                override fun onResponse(response: Response<ChangeDriverPasswordMutation.Data>) {
                    progress.postValue(0)
                    responseLive.postValue(response)
                    Logger.d(response.data())
                }
            })
    }

}