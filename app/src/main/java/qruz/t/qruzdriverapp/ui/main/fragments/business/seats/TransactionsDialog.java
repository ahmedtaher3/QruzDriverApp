package qruz.t.qruzdriverapp.ui.main.fragments.business.seats;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyboardShortcutGroup;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apollographql.apollo.ApolloCall.Callback;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.android.gms.maps.model.LatLng;
import com.orhanobut.logger.Logger;
import com.qruz.PickSeatsTripUserMutation;
import com.qruz.SeatsTripUsersQuery;
import com.qruz.SingleSeatsTripTransactionsQuery;
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
import qruz.t.qruzdriverapp.ui.dialogs.startion.SeatsUserDropDialog;
import qruz.t.qruzdriverapp.ui.dialogs.startion.StationInterface;
import qruz.t.qruzdriverapp.ui.dialogs.startion.StationUsersSeatsAdapter;
import qruz.t.qruzdriverapp.ui.dialogs.startion.StationUsersSeatsDropAdapter;

public class TransactionsDialog extends Dialog {


    public Activity c;
    public Dialog d;
    DataManager dataManager;

    TransactionsAdapter adapter;
    Button paid;
    RecyclerView recyclerView;

    SearchView searchView;

    String startAT;


    public TransactionsDialog(Activity activity, String startAT) {
        super(activity);
        this.c = activity;
        this.startAT = startAT;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.transaction_dialog);
        dataManager = ((BaseApplication) c.getApplication()).getDataManager();
        paid = (Button) findViewById(R.id.paid);
        searchView = (SearchView) findViewById(R.id.searchView);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(c));
        adapter = new TransactionsAdapter(new ArrayList(), c);


        recyclerView.setAdapter(adapter);


        getTransactions(startAT);
    }

    public void getTransactions(String str) {

        CommonUtilities.showStaticDialog(c);
        ApolloClientUtils.INSTANCE.setupApollo(this.dataManager.getAccessToken()).query(SingleSeatsTripTransactionsQuery.builder()
                .trip_id(dataManager.getTripId())
                .trip_time(str)
                .build()).enqueue(new Callback<SingleSeatsTripTransactionsQuery.Data>() {


            @Override
            public void onResponse(@NotNull final Response<SingleSeatsTripTransactionsQuery.Data> response) {

                if (!response.hasErrors()) {

                    Double totalPaid = 0.0;
                    Logger.d(response.data());
                    CommonUtilities.hideDialog();
                    ArrayList list = new ArrayList<SingleSeatsTripTransactionsQuery.SingleSeatsTripTransaction>(response.data().singleSeatsTripTransactions());

                   if(list.size() > 0)
                   {
                       for (SingleSeatsTripTransactionsQuery.SingleSeatsTripTransaction model : response.data().singleSeatsTripTransactions())
                       {
                           totalPaid = totalPaid +model.paid();
                       }

                       final Double finalTotalPaid = totalPaid;
                       c.runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               paid.setVisibility(View.VISIBLE);
                               paid.setText("إجمالى الدفع : " + "\n" +  String.valueOf(finalTotalPaid));

                           }
                       });



                   }

                    adapter.setUsers(list);
                } else {
                    CommonUtilities.hideDialog();
                    Logger.d(response.errors());
                }

            }

            public void onFailure(ApolloException apolloException) {
                Logger.d(apolloException.getMessage());
                CommonUtilities.hideDialog();

            }
        });
    }


}
