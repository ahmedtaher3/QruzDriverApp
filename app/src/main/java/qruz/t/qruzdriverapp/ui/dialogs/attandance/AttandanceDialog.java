package qruz.t.qruzdriverapp.ui.dialogs.attandance;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

import com.apollographql.apollo.ApolloCall.Callback;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.android.gms.maps.model.LatLng;
import com.orhanobut.logger.Logger;
import com.qruz.BusinessTripAttendanceQuery;
import com.qruz.CreateBusinessTripAttendanceMutation;
import com.qruz.data.remote.ApolloClientUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import qruz.t.qruzdriverapp.R;
import qruz.t.qruzdriverapp.Utilities.CommonUtilities;
import qruz.t.qruzdriverapp.base.BaseApplication;
import qruz.t.qruzdriverapp.data.local.DataManager;

public class AttandanceDialog extends Dialog implements AttandanceAdapter.OnChangeStatusClick {

    public Activity c;
    public Dialog d;
    DataManager dataManager;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    Button done;
    AttandanceAdapter adapter;

    String tripName;
    String tripID;
    LatLng latLng;

    public AttandanceDialog(Activity activity, String str, String str2, LatLng latLng) {
        super(activity);
        this.c = activity;
        this.tripName = str;
        this.tripID = str2;
        this.latLng = latLng;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.station_attandance);
        dataManager = ((BaseApplication) this.c.getApplication()).getDataManager();


        adapter = new AttandanceAdapter(new ArrayList(), c, this);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        done = findViewById(R.id.done);
        recyclerView.setAdapter(adapter);


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        getUsers();
    }

    /* access modifiers changed from: 0000 */
    public void getUsers() {
        this.progressBar.setVisibility(View.VISIBLE);


        ApolloClientUtils.INSTANCE.setupApollo(this.dataManager.getAccessToken()).query(BusinessTripAttendanceQuery.builder().trip_id(this.tripID).build()).enqueue(new Callback<BusinessTripAttendanceQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<BusinessTripAttendanceQuery.Data> response) {

                c.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });

                adapter.setUsers(response.data().businessTripAttendance());
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {

                c.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    /* access modifiers changed from: 0000 */
    public void setStatus(Boolean status, String userName, String userid) {
        progressBar.setVisibility(View.GONE);
        Logger.d(tripID + "  "+ status + "  "+tripName +"  "+ userid+"  "+ CommonUtilities.convertToDate(System.currentTimeMillis()));


        ApolloClientUtils.INSTANCE.setupApollo(this.dataManager.getAccessToken()).mutate(CreateBusinessTripAttendanceMutation.builder()
                .trip_id(tripID)
                .is_absent(status)
                .log_id(this.dataManager.getLogId())
                .longitude(String.valueOf(latLng.longitude))
                .latitude(String.valueOf(latLng.latitude))
                .trip_name(tripName)
                .driver_id(this.dataManager.getUser().getId())
                .user_id(userid)
                .user_name(userName)
                .build()).enqueue(new Callback<CreateBusinessTripAttendanceMutation.Data>() {
            public void onResponse(Response<CreateBusinessTripAttendanceMutation.Data> response) {
                Logger.d(response.data());

                c.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });

            }

            public void onFailure(ApolloException apolloException) {
                Logger.d(apolloException.getCause());

                c.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }


    @Override
    public void setOnChangeStatusClick(@NotNull String userId,@NotNull String userName, boolean status) {
        setStatus(status , userName, userId);
    }
}
