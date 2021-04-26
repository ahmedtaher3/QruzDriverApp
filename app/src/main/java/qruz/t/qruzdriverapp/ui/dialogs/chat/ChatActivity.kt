package qruz.t.qruzdriverapp.ui.dialogs.chat

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.orhanobut.logger.Logger
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.PrivateChannel
import com.pusher.client.channel.PrivateChannelEventListener
import com.pusher.client.channel.PusherEvent
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import com.pusher.client.util.HttpAuthorizer
import com.qruz.BusinessTripChatMessagesQuery
import com.qruz.BusinessTripChatMessagesQuery.BusinessTripChatMessage
import com.qruz.BusinessTripChatMessagesQuery.Sender
import com.qruz.SendMesageMutation
import com.qruz.data.remote.ApolloClientUtils.setupApollo
import qruz.t.qruzdriverapp.R
import qruz.t.qruzdriverapp.Utilities.CommonUtilities
import qruz.t.qruzdriverapp.base.BaseActivity
import qruz.t.qruzdriverapp.base.BaseApplication
import qruz.t.qruzdriverapp.data.local.DataManager
import qruz.t.qruzdriverapp.databinding.ActivityChatBinding
import java.util.*

class ChatActivity : BaseActivity<ActivityChatBinding>() {
    lateinit var binding: ActivityChatBinding
    private var ch: PrivateChannel? = null
    var dataManager: DataManager? = null
    var progressBar: ProgressBar? = null
    var recyclerView: RecyclerView? = null
    var sendMsg: ImageView? = null
    var sendMsgProgress: ProgressBar? = null
    var adapter: ChatAdapter? = null
    var chat_msg: EditText? = null
    var NAME = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = viewDataBinding

        NAME = intent.getStringExtra("NAME")!!
        binding.name.text = NAME
        dataManager = (getApplication() as BaseApplication).dataManager
        progressBar = findViewById(R.id.chatProgressBar)
        sendMsgProgress = findViewById(R.id.sendMsgProgress)
        recyclerView = findViewById(R.id.chatRecyclerView)
        chat_msg = findViewById(R.id.chat_msg)
        sendMsg = findViewById(R.id.sendMsg)

        recyclerView!!.layoutManager = LinearLayoutManager(this)
        adapter = ChatAdapter(this, dataManager!!)

        binding.close.setOnClickListener {
            finish()
        }

        sendMsg!!.setOnClickListener {
            if (!TextUtils.isEmpty(chat_msg?.getText().toString())) {
                sendMsg(chat_msg?.getText().toString())
            }
        }

        pusher()
        getPreMsgs()

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_chat
    }

    /* access modifiers changed from: 0000 */
    fun sendMsg(str: String?) {
        adapter!!.addItem(
            BusinessTripChatMessage(
                "",
                "",
                str,
                CommonUtilities.convertToTime(System.currentTimeMillis()),
                CommonUtilities.convertToTime(System.currentTimeMillis()),
                Sender(
                    "",
                    dataManager!!.user.id,
                    dataManager!!.user.name
                ),
                "App\\Driver"
            )
        )
        adapter!!.notifyDataSetChanged()
        recyclerView!!.scrollToPosition(adapter!!.itemCount - 1)
        chat_msg!!.setText("")
        setupApollo(this.dataManager!!.accessToken)
            ?.mutate(
                SendMesageMutation.builder().user_id(dataManager!!.user.id).message(str!!)
                    .trip_id(dataManager!!.tripId).log_id(dataManager!!.logId).build()
            )?.enqueue(object : ApolloCall.Callback<SendMesageMutation.Data?>() {
                override fun onResponse(response: Response<SendMesageMutation.Data?>) {
                    Logger.d(response.errors())
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

                override fun onFailure(apolloException: ApolloException) {
                    /*   c.runOnUiThread(new Runnable() {
                    public void run() {
                        sendMsgProgress.setVisibility(View.GONE);
                        sendMsg.setVisibility(View.VISIBLE);
                        Toast.makeText(c, " sendMsg" + R.string.something_wrong, Toast.LENGTH_SHORT).show();
                    }
                });*/
                    Logger.d(apolloException.localizedMessage)
                }
            })
    }

    /* access modifiers changed from: 0000 */
    fun getPreMsgs() {
        progressBar!!.visibility = View.VISIBLE
        setupApollo(dataManager!!.accessToken)
            ?.query(
                BusinessTripChatMessagesQuery.builder().log_id(dataManager!!.logId).user_id("0")
                    .is_direct(false).build()
            )?.enqueue(object : ApolloCall.Callback<BusinessTripChatMessagesQuery.Data>() {
                override fun onResponse(response: Response<BusinessTripChatMessagesQuery.Data>) {
                    if (!response.hasErrors()) {
                        adapter!!.setMessages(response.data()!!.businessTripChatMessages()!!)
                        runOnUiThread {
                            recyclerView!!.adapter = adapter
                            progressBar!!.visibility = View.GONE
                            recyclerView!!.scrollToPosition(adapter!!.itemCount - 1)
                        }
                    } else {
                       runOnUiThread {
                           progressBar!!.visibility = View.GONE
                       }
                    }
                }

                override fun onFailure(apolloException: ApolloException) {
                    runOnUiThread {
                        progressBar!!.visibility = View.GONE
                        Toast.makeText(this@ChatActivity, R.string.something_wrong, Toast.LENGTH_SHORT).show()
                    }
                    Toast.makeText(this@ChatActivity, R.string.something_wrong, Toast.LENGTH_SHORT).show()

                }
            })
    }

    fun pusher() {
        val httpAuthorizer = HttpAuthorizer("https://qruz.xyz/broadcasting/auth")
        val hashMap: HashMap<String, String> = HashMap<String, String>()
        hashMap.put("Accept", "application/json")
        val sb = StringBuilder()
        sb.append("Bearer ")
        sb.append(dataManager?.accessToken)
        hashMap["Authorization"] = sb.toString()
        httpAuthorizer.setHeaders(hashMap)
        val wssPort =
            PusherOptions().setCluster("eu").setAuthorizer(httpAuthorizer).setHost("qruz.xyz")
                .setWsPort(6002).setWssPort(6002)
        wssPort.isEncrypted = true
        val pusher = Pusher("48477c9c2419bd65e74f", wssPort)
        pusher.connect(object : ConnectionEventListener {
            override fun onConnectionStateChange(connectionStateChange: ConnectionStateChange) {
                val printStream = System.out
                val sb = StringBuilder()
                sb.append("State changed to ")
                sb.append(connectionStateChange.currentState)
                sb.append(" from ")
                sb.append(connectionStateChange.previousState)
                printStream.println(sb.toString())
            }

            override fun onError(
                str: String,
                str2: String,
                exc: Exception
            ) {
                println("There was a problem connecting!")
            }
        }, ConnectionState.ALL)
        val sb2 = StringBuilder()
        sb2.append("private-App.BusinessTrip.")
        sb2.append(dataManager?.logId)
        ch = pusher.subscribePrivate(sb2.toString())
        ch?.bind("client-chat.message", object : PrivateChannelEventListener {
            override fun onSubscriptionSucceeded(str: String) {
                val str2 = "onSubscriptionSucceeded"
                Log.e(str2, str2)
            }

            override fun onAuthenticationFailure(
                str: String,
                exc: Exception
            ) {
                Log.e("failed 1", str)
            }

            override fun onEvent(pusherEvent: PusherEvent) {
                val msg = Gson().fromJson(
                    JsonParser().parse(pusherEvent.data),
                    BusinessTripChatMessage::class.java
                ) as BusinessTripChatMessage
                Logger.d(
                    msg.sender().id() + "   =   " + dataManager?.user?.id
                )
                if (msg.sender().id() == dataManager?.user?.id) return
                val printStream = System.out
                val sb = StringBuilder()
                sb.append(" PusherEvent")
                sb.append(pusherEvent)
                printStream.println(sb.toString())
                runOnUiThread {
                    adapter?.addItem(
                        Gson().fromJson(
                            JsonParser().parse(pusherEvent.data),
                            BusinessTripChatMessage::class.java
                        ) as BusinessTripChatMessage
                    )
                    adapter?.notifyDataSetChanged()
                    recyclerView?.scrollToPosition(adapter?.getItemCount()!! - 1)
                }
            }
        })
        pusher.connect()
    }
}