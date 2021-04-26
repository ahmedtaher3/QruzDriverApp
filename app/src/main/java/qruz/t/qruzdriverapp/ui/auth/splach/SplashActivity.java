package qruz.t.qruzdriverapp.ui.auth.splach;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;


import com.orhanobut.logger.Logger;

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
import qruz.t.qruzdriverapp.ui.dialogs.chat.ChatActivity;
import qruz.t.qruzdriverapp.ui.dialogs.chat.DirectChatActivity;
import qruz.t.qruzdriverapp.ui.main.MainActivity;

public class SplashActivity extends BaseActivity<ActivitySplashBinding> {
    private static final String TAG = "SplashActivity";
    Bundle data;
    ActivitySplashBinding activitySplashBinding;

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySplashBinding = getViewDataBinding();

        try {
            data = getIntent().getExtras();
        } catch (Exception e) {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }


        Single.timer(2000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Long aLong) {
                        Log.d(TAG, "onSuccess: ");


                        if (data != null) {


                            if (data.getString("view").equals("BusinessTripGroupChat")) {




                                Intent intent = new Intent(SplashActivity.this, ChatActivity.class);
                                intent.putExtra("NAME", data.getString("title"));
                                startActivity(intent);
                                finish();
                            } else if (data.getString("view").equals("BusinessTripDirectMessage")) {



                                Intent intent = new Intent(SplashActivity.this, DirectChatActivity.class);
                                intent.putExtra("NAME", data.getString("title"));
                                intent.putExtra("ID", data.getString("sender_id"));
                                startActivity(intent);
                                finish();
                            } else {



                                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }


                        } else {
                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }


                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

    }


}
