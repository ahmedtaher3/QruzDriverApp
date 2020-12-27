package qruz.t.qruzdriverapp.Utilities;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.Nullable;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.orhanobut.logger.Logger;
import com.qruz.UpdateDriverLocationMutation;
import com.qruz.data.remote.ApolloClientUtils;

import org.jetbrains.annotations.NotNull;

import qruz.t.qruzdriverapp.base.BaseApplication;
import qruz.t.qruzdriverapp.data.local.DataManager;


/**
 * Created by filipp on 6/16/2016.
 */
public class GPS_Service extends Service {

    private LocationListener listener;
    private LocationManager locationManager;
    private DataManager dataManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        dataManager = ((BaseApplication) getApplication()).getDataManager();

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Logger.d(location.toString());
                updateLocation(location.getLatitude(), location.getLongitude());
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        //noinspection MissingPermission
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 7000, 0, listener);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            //noinspection MissingPermission
            locationManager.removeUpdates(listener);
        }
    }


    void updateLocation(double lat, double log) {

        ApolloClientUtils.INSTANCE.setupApollo(dataManager.getAccessToken()).mutate(UpdateDriverLocationMutation.builder().trip_id(dataManager.getTripId()).driver_id(dataManager.getUser().getId()).latitude(String.valueOf(lat)).longitude(String.valueOf(log)).build()).enqueue(new ApolloCall.Callback<UpdateDriverLocationMutation.Data>() {
            @Override
            public void onResponse(@NotNull Response<UpdateDriverLocationMutation.Data> response) {
                Logger.d(response.data());
                if (!response.hasErrors()) {


                } else {
                    Logger.d(response.errors().get(0).message());
                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                Logger.d(e.getMessage());
            }
        });
    }

}
