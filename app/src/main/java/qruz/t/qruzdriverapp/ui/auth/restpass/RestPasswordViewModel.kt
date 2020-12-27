package qruz.t.qruzdriverapp.ui.main.fragments.profile.changepass

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.orhanobut.logger.Logger
import com.qruz.ChangeDriverPasswordMutation
import com.qruz.DriverTripsQuery
import com.qruz.ForgotPasswordMutation
import com.qruz.data.remote.ApolloClientUtils
import qruz.t.qruzdriverapp.base.BaseApplication
import qruz.t.qruzdriverapp.data.local.DataManager

class RestPasswordViewModel(application: Application) : AndroidViewModel(application) {
    val dataManager: DataManager
    val responseLive: MutableLiveData<Response<ForgotPasswordMutation.Data>>
    val progress: MutableLiveData<Int>


    init {

        dataManager = (getApplication() as BaseApplication).dataManager!!
        responseLive = MutableLiveData<Response<ForgotPasswordMutation.Data>>()
        progress = MutableLiveData<Int>()

    }

    fun restPass(email :String) {


        progress.postValue(1)
        ApolloClientUtils.setupApollo(dataManager.accessToken)
            ?.mutate(ForgotPasswordMutation.builder().email(email).build())
            ?.enqueue(object : ApolloCall.Callback<ForgotPasswordMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                    progress.postValue(0)
                    Logger.d(e.message)
                }

                override fun onResponse(response: Response<ForgotPasswordMutation.Data>) {
                    progress.postValue(0)
                    responseLive.postValue(response)
                    Logger.d(response.data())
                }
            })
    }

}