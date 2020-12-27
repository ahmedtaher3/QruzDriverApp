package qruz.t.qruzdriverapp.ui.main.fragments.profile.update

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.orhanobut.logger.Logger
import com.qruz.UpdateDriverMutation
import com.qruz.data.remote.ApolloClientUtils
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import qruz.t.qruzdriverapp.base.BaseApplication
import qruz.t.qruzdriverapp.data.Retrofit.ApiServices
import qruz.t.qruzdriverapp.data.Retrofit.RetrofitClient
import qruz.t.qruzdriverapp.data.local.DataManager
import retrofit2.Retrofit
import java.io.File

class UpdateProfileViewModel(application: Application) : AndroidViewModel(application) {
    val dataManager: DataManager
    val responseLive: MutableLiveData<Response<UpdateDriverMutation.Data>>
    val progress: MutableLiveData<Int>
    var myAPI: ApiServices? = null
    var retrofit: Retrofit? = null

    init {

        dataManager = (getApplication() as BaseApplication).dataManager!!
        responseLive = MutableLiveData<Response<UpdateDriverMutation.Data>>()
        progress = MutableLiveData<Int>()

        retrofit = RetrofitClient.getInstance()
        myAPI = retrofit!!.create(ApiServices::class.java)

    }

    fun updateDriver(name: String, email: String, phone: String) {


        progress.postValue(1)
        ApolloClientUtils.setupApollo(dataManager.accessToken)
            ?.mutate(
                UpdateDriverMutation.builder().id(dataManager.user.id).name(name).email(email)
                    .phone(phone).build()
            )
            ?.enqueue(object : ApolloCall.Callback<UpdateDriverMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                    progress.postValue(0)
                    Logger.d(e.message)
                }

                override fun onResponse(response: Response<UpdateDriverMutation.Data>) {
                    progress.postValue(0)
                    responseLive.postValue(response)
                    Logger.d(response.data())
                }
            })

    }

    fun updateImage(file : File) {


        val bodyFile: MultipartBody.Part = MultipartBody.Part.createFormData(
            "image", file.getName(),
            RequestBody.create(MediaType.parse("multipart/form-data"), file)
        )
        val id: RequestBody =
            RequestBody.create(MediaType.parse("multipart/form-data"), dataManager.user.id)

        progress.postValue(1)
        myAPI?.update_image("Sheet1", id, bodyFile)!!
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ResponseBody> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(data: ResponseBody) {

                    progress.postValue(0)


                }

                override fun onError(e: Throwable) {
                    progress.postValue(0)
                    println(e.message)
                    System.out.println(e.message)
                    Toast.makeText(getApplication(), e.message, Toast.LENGTH_LONG).show()
                }

                override fun onComplete() {}
            })

    }


}