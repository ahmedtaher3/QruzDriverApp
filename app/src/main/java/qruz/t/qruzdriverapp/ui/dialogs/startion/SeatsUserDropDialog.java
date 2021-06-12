package qruz.t.qruzdriverapp.ui.dialogs.startion;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyboardShortcutGroup;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apollographql.apollo.ApolloCall.Callback;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.android.gms.maps.model.LatLng;
import com.orhanobut.logger.Logger;
import com.qruz.DropSeatsTripUserMutation;
import com.qruz.PickSeatsTripUserMutation;
import com.qruz.SeatsTripUsersQuery;
import com.qruz.UpdateSeatsTripBookingMutation;
import com.qruz.data.remote.ApolloClientUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import qruz.t.qruzdriverapp.R;
import qruz.t.qruzdriverapp.Utilities.CommonUtilities;
import qruz.t.qruzdriverapp.base.BaseApplication;
import qruz.t.qruzdriverapp.data.local.DataManager;
import qruz.t.qruzdriverapp.model.StationSeatUser;

public class SeatsUserDropDialog extends Dialog {
    int STATUS_INT = 1;
    String trip_time;
    /* access modifiers changed from: private */
    public Activity c;
    public Dialog d;
    DataManager dataManager;
    StationSeatUser model;
    Double paid;
    Button confirm, other, cancel;
    TextView name, bookingID, seatsCount, tripCost, preCost, totalCost;

    public SeatsUserDropDialog(Activity activity, StationSeatUser model, String trip_time) {
        super(activity);
        c = activity;
        this.model = model;
        this.trip_time = trip_time;

    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.seat_user_drop_dialog);
        dataManager = ((BaseApplication) this.c.getApplication()).getDataManager();
        confirm = (Button) findViewById(R.id.confirm);
        other = (Button) findViewById(R.id.other);
        cancel = (Button) findViewById(R.id.cancel);
        name = (TextView) findViewById(R.id.name);
        bookingID = (TextView) findViewById(R.id.bookingID);

        seatsCount = (TextView) findViewById(R.id.seatsCount);
        tripCost = (TextView) findViewById(R.id.tripCost);
        preCost = (TextView) findViewById(R.id.preCost);
        totalCost = (TextView) findViewById(R.id.totalCost);

        name.setText(model.getName());
        bookingID.setText(model.getBooking_id());

        seatsCount.setText(String.valueOf(model.getSeats()));
        tripCost.setText(String.valueOf(model.getPayable()));
        preCost.setText(String.valueOf(model.getWallet_balance()));


        totalCost.setText(String.valueOf(model.getPayable() - model.getWallet_balance()));

        paid = model.getPayable() - model.getPaid();

        confirm.setText("تاكبد دفع : " + String.valueOf(model.getPayable() - model.getWallet_balance()));


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommonUtilities.showStaticDialog(c);

                //progressBar.setVisibility(View.VISIBLE);
                ApolloClientUtils.INSTANCE.setupApollo(dataManager.getAccessToken()).mutate(DropSeatsTripUserMutation.builder()
                        .user_id(model.getId())
                        .payable((float) model.getPayable())
                        .paid(Float.parseFloat(String.valueOf(model.getPayable() - model.getWallet_balance())))
                        .booking_id(model.getBooking_id())
                        .log_id(dataManager.getLogId())
                        .trip_id(dataManager.getTripId())
                        .trip_time(trip_time)
                        .build()).enqueue(new Callback<DropSeatsTripUserMutation.Data>() {

                    @Override
                    public void onResponse(@NotNull final Response<DropSeatsTripUserMutation.Data> response) {
                        CommonUtilities.hideDialog();


                        if (!response.hasErrors()) {
                            Logger.d(response.data());
                        } else {
                            Logger.d(response.errors());
                        }
                        dismiss();
                    }

                    public void onFailure(ApolloException apolloException) {
                        Logger.d(apolloException.getMessage());
                        CommonUtilities.hideDialog();

                    }
                });


            }
        });

        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(c);
                View dialogView = c.getLayoutInflater().inflate(R.layout.seat_user_drop_other_dialog, null);
                dialogBuilder.setView(dialogView);


                Button confirm = dialogView.findViewById(R.id.confirm);
                Button cancel = dialogView.findViewById(R.id.cancel);

                TextView name = dialogView.findViewById(R.id.name);
                TextView bookingID = dialogView.findViewById(R.id.bookingID);

                name.setText(model.getName());
                bookingID.setText(model.getBooking_id());


                final AppCompatEditText paidEditText = dialogView.findViewById(R.id.paid);
                final AlertDialog alertDialog = dialogBuilder.create();
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        alertDialog.dismiss();
                        dismiss();
                    }
                });

                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        CommonUtilities.showStaticDialog(c);
                        //progressBar.setVisibility(View.VISIBLE);
                        ApolloClientUtils.INSTANCE.setupApollo(dataManager.getAccessToken()).mutate(DropSeatsTripUserMutation.builder()
                                .user_id(model.getId())
                                .payable((float) model.getPayable())
                                .paid(Float.parseFloat(paidEditText.getText().toString()))
                                .booking_id(model.getBooking_id())
                                .log_id(dataManager.getLogId())
                                .trip_id(dataManager.getTripId())
                                .trip_time(trip_time)
                                .build()).enqueue(new Callback<DropSeatsTripUserMutation.Data>() {

                            @Override
                            public void onResponse(@NotNull final Response<DropSeatsTripUserMutation.Data> response) {
                                CommonUtilities.hideDialog();
                                if (!response.hasErrors()) {
                                    Logger.d(response.data());
                                } else {
                                    Logger.d(response.errors());
                                }


                                alertDialog.dismiss();
                                dismiss();
                             }

                            public void onFailure(ApolloException apolloException) {
                                Logger.d(apolloException.getMessage());
                                CommonUtilities.hideDialog();

                            }
                        });


                    }
                });


                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


                alertDialog.show();


            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }


}
