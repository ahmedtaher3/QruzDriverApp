package qruz.t.qruzdriverapp.ui.auth.splach;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;


import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import qruz.t.qruzdriverapp.R;
import qruz.t.qruzdriverapp.base.BaseActivity;
import qruz.t.qruzdriverapp.databinding.ActivitySplashBinding;
import qruz.t.qruzdriverapp.ui.auth.login.LoginActivity;

public class SplashActivity extends BaseActivity<ActivitySplashBinding> {
    private static final String TAG = "SplashActivity";
    ActivitySplashBinding activitySplashBinding;

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySplashBinding = getViewDataBinding();
        Single.timer(3000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Long aLong) {
                        Log.d(TAG, "onSuccess: ");

                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);

                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });


    }


}
