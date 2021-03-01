package qruz.t.qruzdriverapp.ui.dialogs.chat;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apollographql.apollo.ApolloCall.Callback;
import com.apollographql.apollo.api.Error;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.firebase.crashlytics.internal.common.AbstractSpiCall;
import com.google.firebase.crashlytics.internal.common.CommonUtils;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.orhanobut.logger.Logger;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.PrivateChannel;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.util.HttpAuthorizer;


import com.qruz.BusinessTripChatMessagesQuery;
import com.qruz.SendMesageMutation;
import com.qruz.data.remote.ApolloClientUtils;

import java.io.PrintStream;
import java.util.HashMap;

import qruz.t.qruzdriverapp.R;
import qruz.t.qruzdriverapp.Utilities.CommonUtilities;
import qruz.t.qruzdriverapp.base.BaseApplication;
import qruz.t.qruzdriverapp.data.local.DataManager;

public class ChatDialog extends Dialog {
    ChatAdapter adapter;
    /* access modifiers changed from: private */
    public Activity c;
    private PrivateChannel ch;
    EditText chat_msg;
    public Dialog d;
    DataManager dataManager;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    ImageView sendMsg;
    ProgressBar sendMsgProgress;

    public ChatDialog(Activity activity) {
        super(activity);
        this.c = activity;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        setContentView(R.layout.chat_dialog);

        dataManager = ((BaseApplication) this.c.getApplication()).getDataManager();
        progressBar = (ProgressBar) findViewById(R.id.chatProgressBar);
        sendMsgProgress = (ProgressBar) findViewById(R.id.sendMsgProgress);
        recyclerView = (RecyclerView) findViewById(R.id.chatRecyclerView);
        chat_msg = (EditText) findViewById(R.id.chat_msg);
        sendMsg = (ImageView) findViewById(R.id.sendMsg);

        recyclerView.setLayoutManager(new LinearLayoutManager(c));
        adapter = new ChatAdapter(c, dataManager);


        sendMsg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!TextUtils.isEmpty(chat_msg.getText().toString())) {
                    sendMsg(chat_msg.getText().toString());
                }
            }
        });

        pusher();
        getPreMsgs();
    }

    /* access modifiers changed from: 0000 */
    public void sendMsg(String str) {

        adapter.addItem(new BusinessTripChatMessagesQuery.BusinessTripChatMessage(
                "",
                "",
                str,
                CommonUtilities.convertToTime(System.currentTimeMillis()),
                CommonUtilities.convertToTime(System.currentTimeMillis()),
                new BusinessTripChatMessagesQuery.Sender(
                        "",
                        dataManager.getUser().getId(),
                        dataManager.getUser().getName()
                ),
                "App\\Driver"));
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        chat_msg.setText("");

        ApolloClientUtils.INSTANCE.setupApollo(this.dataManager.getAccessToken()).mutate(SendMesageMutation.builder().user_id(dataManager.getUser().getId()).message(str).trip_id(dataManager.getTripId()).log_id(dataManager.getLogId()).build()).enqueue(new Callback<SendMesageMutation.Data>() {
            public void onResponse(Response<SendMesageMutation.Data> response) {
                Logger.d(response.errors());
           /*     if (!response.hasErrors()) {
                    c.runOnUiThread(new Runnable() {
                        public void run() {
                            sendMsgProgress.setVisibility(View.GONE);
                            sendMsg.setVisibility(View.VISIBLE);
                            chat_msg.setText("");
                        }
                    });
                    return;
                }
                Logger.d("sendMsggg" + response.errors().get(0).message());
                c.runOnUiThread(new Runnable() {
                    public void run() {
                        sendMsgProgress.setVisibility(View.GONE);
                        sendMsg.setVisibility(View.VISIBLE);
                        Toast.makeText(c, R.string.something_wrong, Toast.LENGTH_SHORT).show();
                    }
                });*/
            }

            public void onFailure(ApolloException apolloException) {
             /*   c.runOnUiThread(new Runnable() {
                    public void run() {
                        sendMsgProgress.setVisibility(View.GONE);
                        sendMsg.setVisibility(View.VISIBLE);
                        Toast.makeText(c, " sendMsg" + R.string.something_wrong, Toast.LENGTH_SHORT).show();
                    }
                });*/
                Logger.d(apolloException.getLocalizedMessage());
            }
        });
    }

    /* access modifiers changed from: 0000 */
    public void getPreMsgs() {
        this.progressBar.setVisibility(View.VISIBLE);


        ApolloClientUtils.INSTANCE.setupApollo(dataManager.getAccessToken()).query(BusinessTripChatMessagesQuery.builder().log_id(dataManager.getLogId()).user_id("0").is_direct(false).build()).enqueue(new Callback<BusinessTripChatMessagesQuery.Data>() {
            public void onResponse(Response<BusinessTripChatMessagesQuery.Data> response) {

                if (!response.hasErrors()) {
                    adapter.setMessages(response.data().businessTripChatMessages());


                    c.runOnUiThread(new Runnable() {
                        public void run() {

                            recyclerView.setAdapter(adapter);
                            progressBar.setVisibility(View.GONE);
                            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                        }
                    });
                } else {
                    c.runOnUiThread(new Runnable() {
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                }

            }

            public void onFailure(ApolloException apolloException) {
                Toast.makeText(c, R.string.something_wrong, Toast.LENGTH_SHORT).show();

                c.runOnUiThread(new Runnable() {
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(c, R.string.something_wrong, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void pusher() {
        HttpAuthorizer httpAuthorizer = new HttpAuthorizer("https://qruz.xyz/broadcasting/auth");
        HashMap hashMap = new HashMap();
        hashMap.put(AbstractSpiCall.HEADER_ACCEPT, "application/json");
        StringBuilder sb = new StringBuilder();
        sb.append("Bearer ");
        sb.append(this.dataManager.getAccessToken());
        hashMap.put("Authorization", sb.toString());
        httpAuthorizer.setHeaders(hashMap);
        PusherOptions wssPort = new PusherOptions().setCluster("eu").setAuthorizer(httpAuthorizer).setHost("qruz.xyz").setWsPort(6002).setWssPort(6002);
        wssPort.setEncrypted(true);
        Pusher pusher = new Pusher("48477c9c2419bd65e74f", wssPort);
        pusher.connect(new ConnectionEventListener() {
            public void onConnectionStateChange(ConnectionStateChange connectionStateChange) {
                PrintStream printStream = System.out;
                StringBuilder sb = new StringBuilder();
                sb.append("State changed to ");
                sb.append(connectionStateChange.getCurrentState());
                sb.append(" from ");
                sb.append(connectionStateChange.getPreviousState());
                printStream.println(sb.toString());
            }

            public void onError(String str, String str2, Exception exc) {
                System.out.println("There was a problem connecting!");
            }
        }, ConnectionState.ALL);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("private-App.BusinessTrip.");
        sb2.append(this.dataManager.getLogId());
        this.ch = pusher.subscribePrivate(sb2.toString());
        this.ch.bind("client-chat.message", new PrivateChannelEventListener() {
            public void onSubscriptionSucceeded(String str) {
                String str2 = "onSubscriptionSucceeded";
                Log.e(str2, str2);
            }

            public void onAuthenticationFailure(String str, Exception exc) {
                Log.e("failed 1", str.toString());
            }

            public void onEvent(final PusherEvent pusherEvent) {



                BusinessTripChatMessagesQuery.BusinessTripChatMessage msg = ( BusinessTripChatMessagesQuery.BusinessTripChatMessage) new Gson().fromJson(new JsonParser().parse(pusherEvent.getData()),  BusinessTripChatMessagesQuery.BusinessTripChatMessage.class);
                Logger.d(msg.sender().id() + "   =   " +dataManager.getUser().getId() );
                if (msg.sender().id().equals(dataManager.getUser().getId()))
                    return;


                PrintStream printStream = System.out;
                StringBuilder sb = new StringBuilder();
                sb.append(" PusherEvent");
                sb.append(pusherEvent);
                printStream.println(sb.toString());
                c.runOnUiThread(new Runnable() {
                    public void run() {
                        adapter.addItem(( BusinessTripChatMessagesQuery.BusinessTripChatMessage) new Gson().fromJson(new JsonParser().parse(pusherEvent.getData()),  BusinessTripChatMessagesQuery.BusinessTripChatMessage.class));
                        adapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    }
                });
            }
        });
        pusher.connect();
    }
}
