package qruz.t.qruzdriverapp.ui.dialogs.startion;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyboardShortcutGroup;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apollographql.apollo.ApolloCall.Callback;
import com.apollographql.apollo.api.Error;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.android.gms.maps.model.LatLng;
import com.orhanobut.logger.Logger;
import com.qruz.BusinessTripSubscribersQuery;
import com.qruz.BusinessTripSubscribersQuery.BusinessTripSubscriber;
import com.qruz.BusinessTripSubscribersQuery.Data;
import com.qruz.DropSeatsTripUserMutation;
import com.qruz.DropUsersMutation;
import com.qruz.NearYouMutation;
import com.qruz.PickSeatsTripUserMutation;
import com.qruz.PickUsersMutation;
import com.qruz.SeatsTripUsersQuery;
import com.qruz.UpdateSeatsTripBookingMutation;
import com.qruz.data.remote.ApolloClientUtils;
import com.qruz.type.UserObj;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import qruz.t.qruzdriverapp.R;
import qruz.t.qruzdriverapp.Utilities.CommonUtilities;
import qruz.t.qruzdriverapp.base.BaseApplication;
import qruz.t.qruzdriverapp.data.local.DataManager;
import qruz.t.qruzdriverapp.model.StationSeatUser;
import qruz.t.qruzdriverapp.model.StationUser;

public class SeatsStationDialog extends Dialog implements StationUsersSeatsAdapter.OnStationUsersClick, StationUsersSeatsDropAdapter.OnStationUsersClick {
    int STATUS_INT = 1;
    String STATUS_TEXT;
    /* access modifiers changed from: private */
    public Activity c;
    public Dialog d;
    DataManager dataManager;

    ProgressBar progressBar;
    StationUsersSeatsAdapter stationAdapter;
    StationUsersSeatsDropAdapter stationUsersSeatsDropAdapter;
    String stationID;
    String stationName;
    RecyclerView stationRecyclerView;
    String tripID;
    String tripName;
    LatLng latLng;
    SearchView searchView;
    StationInterface stationInterface;
    String startAT;


    public SeatsStationDialog(Activity activity, String str, String stationName, String str2, String tripName, LatLng latLng, StationInterface stationInterface, String STATUS_TEXT, String startAT) {
        super(activity);
        this.c = activity;
        this.stationID = str;
        this.stationName = stationName;
        this.tripID = str2;
        this.tripName = tripName;
        this.latLng = latLng;
        this.stationInterface = stationInterface;
        this.STATUS_TEXT = STATUS_TEXT;
        this.startAT = startAT;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.station_seat_dialog);
        dataManager = ((BaseApplication) this.c.getApplication()).getDataManager();
        progressBar = (ProgressBar) findViewById(R.id.stationProgressBar);
        searchView = (SearchView) findViewById(R.id.searchView);
        stationRecyclerView = (RecyclerView) findViewById(R.id.stationRecyclerView);
        stationRecyclerView.setLayoutManager(new LinearLayoutManager(this.c));

        stationAdapter = new StationUsersSeatsAdapter(new ArrayList(), this.c, this);
        stationUsersSeatsDropAdapter = new StationUsersSeatsDropAdapter(new ArrayList(), this.c, this);


        if (STATUS_TEXT == "PICK_UP") {
            stationRecyclerView.setAdapter(stationAdapter);

        } else {
            stationRecyclerView.setAdapter(stationUsersSeatsDropAdapter);

        }


        getUsers(STATUS_TEXT);
    }

    public void getUsers(String str) {
        this.progressBar.setVisibility(View.VISIBLE);
        ApolloClientUtils.INSTANCE.setupApollo(this.dataManager.getAccessToken()).query(SeatsTripUsersQuery.builder().station_id(stationID)
                .trip_time(startAT).trip_id(this.tripID).status(str).build()).enqueue(new Callback<SeatsTripUsersQuery.Data>() {


            @Override
            public void onResponse(@NotNull final Response<SeatsTripUsersQuery.Data> response) {


                Logger.d(response.errors());
                if (!response.hasErrors()) {
                    SeatsStationDialog.this.c.runOnUiThread(new Runnable() {
                        public void run() {
                            SeatsStationDialog.this.progressBar.setVisibility(View.GONE);

                            ArrayList arrayList = new ArrayList();
                            for (SeatsTripUsersQuery.SeatsTripUser seatsTripUser : ((SeatsTripUsersQuery.Data) response.data()).seatsTripUsers()) {
                                StationSeatUser stationUser = new StationSeatUser(
                                        seatsTripUser.id()
                                        , seatsTripUser.name()
                                        , seatsTripUser.phone()
                                        , seatsTripUser.payable()
                                        , seatsTripUser.wallet_balance()
                                        , seatsTripUser.booking_id()
                                        , seatsTripUser.seats()
                                        , seatsTripUser.boarding_pass()
                                        , seatsTripUser.paid()

                                );
                                arrayList.add(stationUser);
                            }
                            stationAdapter.setUsers(arrayList);
                            stationUsersSeatsDropAdapter.setUsers(arrayList);
                            return;
                        }
                    });

                }

            }

            public void onFailure(ApolloException apolloException) {
                Logger.d(apolloException.getMessage());
            }
        });
    }


    @Override
    public void setOnPhonesClick(@NotNull StationSeatUser model) {

    }


    @Override
    public void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> data, @Nullable Menu menu, int deviceId) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void setOnConfirmClick(@NotNull StationSeatUser model) {
        Toast.makeText(c, "setOnConfirmClick", Toast.LENGTH_SHORT).show();

        CommonUtilities.showStaticDialog(c);
        ApolloClientUtils.INSTANCE.setupApollo(this.dataManager.getAccessToken()).mutate(PickSeatsTripUserMutation.builder().booking_id(model.getBooking_id()).build()).enqueue(new Callback<PickSeatsTripUserMutation.Data>() {

            @Override
            public void onResponse(@NotNull final Response<PickSeatsTripUserMutation.Data> response) {

                Logger.d(response.data());
                if (!response.hasErrors()) {

                    CommonUtilities.hideDialog();

                    dismiss();
                }

            }

            public void onFailure(ApolloException apolloException) {
                Logger.d(apolloException.getMessage());
                CommonUtilities.hideDialog();
            }
        });


    }

    @Override
    public void setOnAbsentClick(@NotNull StationSeatUser model) {

        Toast.makeText(c, "setOnAbsentClick", Toast.LENGTH_SHORT).show();

        CommonUtilities.showStaticDialog(c);
        ApolloClientUtils.INSTANCE.setupApollo(this.dataManager.getAccessToken()).mutate(UpdateSeatsTripBookingMutation.builder().user_id(model.getBooking_id()).build()).enqueue(new Callback<UpdateSeatsTripBookingMutation.Data>() {

            @Override
            public void onResponse(@NotNull final Response<UpdateSeatsTripBookingMutation.Data> response) {

                if (!response.hasErrors()) {

                    CommonUtilities.hideDialog();
                    Logger.d(response.data());
                    dismiss();
                }else
                { CommonUtilities.hideDialog();
                    Logger.d(response.errors());
                }

            }

            public void onFailure(ApolloException apolloException) {
                Logger.d(apolloException.getMessage());
                CommonUtilities.hideDialog();
            }
        });


    }

    @Override
    public void setOnDropPhonesClick(@NotNull StationSeatUser model) {

    }

    @Override
    public void setOnDropConfirmClick(@NotNull StationSeatUser model) {

        SeatsUserDropDialog cdd = new SeatsUserDropDialog(c, model , startAT);
        cdd.setCanceledOnTouchOutside(true);
        cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cdd.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        cdd.show();

    }


}
