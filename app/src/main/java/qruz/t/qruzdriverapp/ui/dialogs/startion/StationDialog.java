package qruz.t.qruzdriverapp.ui.dialogs.startion;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apollographql.apollo.ApolloCall.Callback;
import com.apollographql.apollo.api.Error;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.orhanobut.logger.Logger;
import com.qruz.BusinessTripSubscribersQuery;
import com.qruz.BusinessTripSubscribersQuery.BusinessTripSubscriber;
import com.qruz.BusinessTripSubscribersQuery.Data;
import com.qruz.DropUsersMutation;
import com.qruz.NearYouMutation;
import com.qruz.PickUsersMutation;
import com.qruz.data.remote.ApolloClientUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import qruz.t.qruzdriverapp.R;
import qruz.t.qruzdriverapp.Utilities.CommonUtilities;
import qruz.t.qruzdriverapp.base.BaseApplication;
import qruz.t.qruzdriverapp.data.local.DataManager;
import qruz.t.qruzdriverapp.model.StationUser;

public class StationDialog extends Dialog {
    Button DropUsersButton;
    int STATUS_INT = 1;
    String STATUS_TEXT = "NOT_PICKED_UP";
    /* access modifiers changed from: private */
    public Activity c;
    public Dialog d;
    DataManager dataManager;
    List<String> dropped;
    Button pickUsersButton;
    List<String> pickedUp;
    ProgressBar progressBar;
    StationUsersAdapter stationAdapter;
    Button stationDone;
    String stationID;
    RecyclerView stationRecyclerView;
    Button stationSendNotification;
    String tripID;

    public StationDialog(Activity activity, String str, String str2) {
        super(activity);
        this.c = activity;
        this.stationID = str;
        this.tripID = str2;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.station_dialog);
        this.dataManager = ((BaseApplication) this.c.getApplication()).getDataManager();
        this.progressBar = (ProgressBar) findViewById(R.id.stationProgressBar);
        this.stationRecyclerView = (RecyclerView) findViewById(R.id.stationRecyclerView);
        this.stationDone = (Button) findViewById(R.id.stationDone);
        this.pickUsersButton = (Button) findViewById(R.id.pickUsersButton);
        this.DropUsersButton = (Button) findViewById(R.id.DropUsersButton);
        this.stationSendNotification = (Button) findViewById(R.id.stationSendNotification);
        this.stationRecyclerView.setLayoutManager(new LinearLayoutManager(this.c));
        this.stationAdapter = new StationUsersAdapter(new ArrayList(), this.c);
        this.stationRecyclerView.setAdapter(this.stationAdapter);
        this.pickedUp = new ArrayList();
        this.dropped = new ArrayList();
        stationSendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StationDialog.this.sendNotification();
            }
        });
        this.pickUsersButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                stationDone.setText(R.string.pick_up);

                StationDialog.this.pickUsersButton.setBackgroundResource(R.drawable.blue_background);
                StationDialog.this.DropUsersButton.setBackgroundResource(R.drawable.white_background);
                StationDialog stationDialog = StationDialog.this;
                stationDialog.STATUS_INT = 1;
                stationDialog.STATUS_TEXT = "NOT_PICKED_UP";
                stationDialog.stationAdapter.setTextStatus(1);
                StationDialog.this.stationAdapter.setUsers(new ArrayList());
                StationDialog stationDialog2 = StationDialog.this;
                stationDialog2.getUsers(stationDialog2.STATUS_TEXT);
            }
        });
        this.DropUsersButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                stationDone.setText(R.string.drop_off);


                StationDialog.this.DropUsersButton.setBackgroundResource(R.drawable.blue_background);
                StationDialog.this.pickUsersButton.setBackgroundResource(R.drawable.white_background);
                StationDialog stationDialog = StationDialog.this;
                stationDialog.STATUS_INT = 2;
                stationDialog.STATUS_TEXT = "PICKED_UP";
                stationDialog.stationAdapter.setTextStatus(2);
                StationDialog.this.stationAdapter.setUsers(new ArrayList());
                StationDialog stationDialog2 = StationDialog.this;
                stationDialog2.getUsers(stationDialog2.STATUS_TEXT);
            }
        });
        this.stationDone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                int i = StationDialog.this.STATUS_INT;
                if (i == 1) {
                    Iterator it = StationDialog.this.stationAdapter.getUsers().iterator();
                    while (it.hasNext()) {
                        StationUser stationUser = (StationUser) it.next();
                        if (stationUser.getIsPickedUp()) {
                            StationDialog.this.pickedUp.add(stationUser.getId());
                        }
                    }
                    if (StationDialog.this.pickedUp.size() != 0) {
                        StationDialog stationDialog = StationDialog.this;
                        stationDialog.setPickedUp(stationDialog.pickedUp);
                        return;
                    }
                    StationDialog.this.dismiss();
                } else if (i == 2) {
                    Iterator it2 = StationDialog.this.stationAdapter.getUsers().iterator();
                    while (it2.hasNext()) {
                        StationUser stationUser2 = (StationUser) it2.next();
                        if (stationUser2.getIsPickedUp()) {
                            StationDialog.this.dropped.add(stationUser2.getId());
                        }
                    }
                    if (StationDialog.this.dropped.size() != 0) {
                        StationDialog stationDialog2 = StationDialog.this;
                        stationDialog2.setDroppedOff(stationDialog2.dropped);
                        return;
                    }
                    StationDialog.this.dismiss();
                }
            }
        });
        getUsers(this.STATUS_TEXT);
    }

    /* access modifiers changed from: 0000 */
    public void getUsers(String str) {
        this.progressBar.setVisibility(View.VISIBLE);
        ApolloClientUtils.INSTANCE.setupApollo(this.dataManager.getAccessToken()).query(BusinessTripSubscribersQuery.builder().station_id(stationID).trip_id(this.tripID).status(str).build()).enqueue(new Callback<Data>() {
            public void onResponse(final Response<Data> response) {
                Logger.d(response.data());
                if (!response.hasErrors()) {
                    StationDialog.this.c.runOnUiThread(new Runnable() {
                        public void run() {
                            StationDialog.this.progressBar.setVisibility(View.GONE);

                            ArrayList arrayList = new ArrayList();
                            for (BusinessTripSubscriber businessTripSubscriber : ((Data) response.data()).businessTripSubscribers()) {
                                StationUser stationUser = new StationUser(businessTripSubscriber.id(), businessTripSubscriber.name(), businessTripSubscriber.email(), businessTripSubscriber.phone(), businessTripSubscriber.avatar(), false);
                                arrayList.add(stationUser);
                            }
                             StationDialog.this.stationAdapter.setUsers(arrayList);
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

    /* access modifiers changed from: 0000 */
    public void setPickedUp(List<String> list) {
        CommonUtilities.showStaticDialog(this.c);
        ApolloClientUtils.INSTANCE.setupApollo(this.dataManager.getAccessToken()).mutate(PickUsersMutation.builder().trip_id(this.dataManager.getTripId()).log_id(this.dataManager.getLogId()).latitude("31.0").longitude("31.2").users(list).build()).enqueue(new Callback<PickUsersMutation.Data>() {
            public void onResponse(Response<PickUsersMutation.Data> response) {
                Logger.d(response.data());
                if (!response.hasErrors()) {
                    CommonUtilities.hideDialog();
                    StationDialog.this.dismiss();
                    return;
                }
                Logger.d(((Error) response.errors().get(0)).message());
            }

            public void onFailure(ApolloException apolloException) {
                Logger.d(apolloException.getMessage());
            }
        });
    }

    /* access modifiers changed from: 0000 */
    public void setDroppedOff(List<String> list) {
        CommonUtilities.showStaticDialog(this.c);
        ApolloClientUtils.INSTANCE.setupApollo(this.dataManager.getAccessToken()).mutate(DropUsersMutation.builder().trip_id(this.dataManager.getTripId()).log_id(this.dataManager.getLogId()).latitude("31.0").longitude("31.2").users(list).build()).enqueue(new Callback<DropUsersMutation.Data>() {
            public void onResponse(Response<DropUsersMutation.Data> response) {
                Logger.d(response.data());
                if (!response.hasErrors()) {
                    CommonUtilities.hideDialog();
                    StationDialog.this.dismiss();
                    return;
                }
                Logger.d(((Error) response.errors().get(0)).message());
            }

            public void onFailure(ApolloException apolloException) {
                Logger.d(apolloException.getMessage());
            }
        });
    }

    /* access modifiers changed from: 0000 */
    public void sendNotification() {
        ApolloClientUtils.INSTANCE.setupApollo(this.dataManager.getAccessToken()).mutate(NearYouMutation.builder().station_id(this.stationID).build()).enqueue(new Callback<NearYouMutation.Data>() {
            public void onResponse(Response<NearYouMutation.Data> response) {
                Logger.d(response.data());
                if (response.hasErrors()) {
                    Logger.d(((Error) response.errors().get(0)).message());
                }
            }

            public void onFailure(ApolloException apolloException) {
                Logger.d(apolloException.getMessage());
            }
        });
    }


}
