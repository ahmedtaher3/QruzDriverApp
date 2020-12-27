package qruz.t.qruzdriverapp.ui.auth.signup

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.orhanobut.logger.Logger
import com.qruz.CreateDriverMutation
import com.qruz.DriverLoginMutation
import com.qruz.data.remote.ApolloClientUtils.setupApollo
import qruz.t.qruzdriverapp.base.BaseApplication
import qruz.t.qruzdriverapp.data.local.DataManager

class SignUpViewModel(application: Application) : AndroidViewModel(application) {
    val dataManager: DataManager
    val responseLive: MutableLiveData<Response<CreateDriverMutation.Data>>
    val progress: MutableLiveData<Int>

    init {
        dataManager = (getApplication() as BaseApplication).dataManager!!
        responseLive = MutableLiveData<Response<CreateDriverMutation.Data>>()
        progress = MutableLiveData<Int>()
    }

    public fun signUp(name: String, phone: String, vehicle: String, city: String) {
        progress.postValue(1)




        setupApollo(dataManager.accessToken)?.mutate(
            CreateDriverMutation.builder().city(city).name(name).phone(phone).vehicle(vehicle).build()
        )
            ?.enqueue(object : ApolloCall.Callback<CreateDriverMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                    progress.postValue(0)
                    Logger.d(e.message)

                }

                override fun onResponse(response: Response<CreateDriverMutation.Data>) {

                    responseLive.postValue(response)
                    progress.postValue(0)

                }
            })


    }


}