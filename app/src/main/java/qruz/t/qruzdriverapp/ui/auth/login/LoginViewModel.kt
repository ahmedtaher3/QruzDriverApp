package qruz.t.qruzdriverapp.ui.auth.login

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
import com.qruz.DriverLoginMutation
import com.qruz.data.remote.ApolloClientUtils.setupApollo
import qruz.t.qruzdriverapp.base.BaseApplication
import qruz.t.qruzdriverapp.data.local.DataManager

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    val dataManager: DataManager
    val responseLive: MutableLiveData<Response<DriverLoginMutation.Data>>
    val progress: MutableLiveData<Int>

    init {
        dataManager = (getApplication() as BaseApplication).dataManager!!
        responseLive = MutableLiveData<Response<DriverLoginMutation.Data>>()
        progress = MutableLiveData<Int>()
    }

    public fun login(userEmail: String, userPass: String) {
        progress.postValue(1)
    /*    setupApollo(dataManager.accessToken)?.mutate(
            DriverLoginMutation.builder().email(
                userEmail
            ).password(userPass).platform("android").device_id("null").build()
        )
            ?.enqueue(object : ApolloCall.Callback<DriverLoginMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                    progress.postValue(0)
                    Logger.d(e.message)

                }

                override fun onResponse(response: Response<DriverLoginMutation.Data>) {

                    responseLive.postValue(response)
                    progress.postValue(0)

                }
            })

*/
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(" device_id ", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                    progress.postValue(0)
                }

                Logger.d(task.result?.token.toString())
                Log.w("MainActivity", task.result?.token.toString())
                setupApollo(dataManager.accessToken)?.mutate(
                    DriverLoginMutation.builder().email(
                        userEmail
                    ).password(userPass).platform("android").device_id(task.result?.token.toString()).build()
                )
                    ?.enqueue(object : ApolloCall.Callback<DriverLoginMutation.Data>() {
                        override fun onFailure(e: ApolloException) {
                            progress.postValue(0)
                            Logger.d(e.message)

                        }

                        override fun onResponse(response: Response<DriverLoginMutation.Data>) {

                            responseLive.postValue(response)
                            progress.postValue(0)

                        }
                    })


            })

    }


}