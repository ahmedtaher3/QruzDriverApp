package qruz.t.qruzdriverapp

import android.app.Notification
import android.app.Notification.DEFAULT_SOUND
import android.app.Notification.DEFAULT_VIBRATE
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.orhanobut.logger.Logger
import com.qruz.UpdateDriverMutation

import com.qruz.data.remote.ApolloClientUtils
import qruz.t.qruzdriverapp.base.BaseApplication
import qruz.t.qruzdriverapp.data.local.DataManager
import qruz.t.qruzdriverapp.ui.auth.splach.SplashActivity
import qruz.t.qruzdriverapp.ui.dialogs.chat.ChatActivity
import qruz.t.qruzdriverapp.ui.dialogs.chat.DirectChatActivity


class MyFirebaseMessagingService : FirebaseMessagingService() {
    var dataManager: DataManager? = null

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        dataManager = (getApplication() as BaseApplication).dataManager!!

        Log.d(TAG, "From: ${remoteMessage.from}")
        Log.d(TAG, "Message data payload: ${remoteMessage.data}")

        if (dataManager?.loggingMode!!) {
            if (remoteMessage.data["view"]!! == "BusinessTripGroupChat") {
                sendChatNotification(remoteMessage.data["title"]!!, remoteMessage.data["body"]!!)

            } else if (remoteMessage.data["view"]!! == "BusinessTripDirectMessage") {
                sendDirectChatNotification(
                    remoteMessage.data["title"]!!,
                    remoteMessage.data["body"]!!,
                    remoteMessage.data["sender_id"]!!
                )

            } else {
                sendNotification(remoteMessage.data["title"]!!, remoteMessage.data["body"]!!)

            }
        }

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob()
            } else {
                // Handle message within 10 seconds
                handleNow()
            }
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    // [START on_new_token]
    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token)
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
    private fun scheduleJob() {
        /*    // [START dispatch_job]
            val work = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
            WorkManager.getInstance().beginWith(work).enqueue()
            // [END dispatch_job]*/
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {

        dataManager = (getApplication() as BaseApplication).dataManager!!


        if (dataManager?.user != null) {
            ApolloClientUtils.setupApollo(dataManager?.accessToken)
                ?.mutate(
                    UpdateDriverMutation.builder().id(dataManager?.user?.id!!).device_id(token).build()
                )
                ?.enqueue(object : ApolloCall.Callback<UpdateDriverMutation.Data>() {
                    override fun onFailure(e: ApolloException) {
                        Logger.d(e.message)
                    }

                    override fun onResponse(response: Response<UpdateDriverMutation.Data>) {
                        Logger.d(response.data())
                    }
                })
        }


    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(messageTitle: String, messageBody: String) {
        val intent = Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,  System.currentTimeMillis().toInt()/* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val DEFAULT_VIBRATE_PATTERN = longArrayOf(0, 250, 250, 250)

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)


        val mBuilder =
            NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.pp)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setDefaults(DEFAULT_VIBRATE) //Important for heads-up notification
                .setPriority(Notification.PRIORITY_MAX)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "channel_name"
            val description = "channel_description"
            val importance =
                NotificationManager.IMPORTANCE_HIGH //Important for heads-up notification

            val channel = NotificationChannel("1", name, importance)
            channel.description = description
            channel.setShowBadge(true)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }


        val buildNotification = mBuilder.build()
        val mNotifyMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotifyMgr.notify(System.currentTimeMillis().toInt(), buildNotification)
    }


    private fun sendDirectChatNotification(messageTitle: String, messageBody: String, id: String) {
        val intent = Intent(this, DirectChatActivity::class.java)
        intent.putExtra("ID", id)
        intent.putExtra("NAME", messageTitle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,  System.currentTimeMillis().toInt() /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val DEFAULT_VIBRATE_PATTERN = longArrayOf(0, 250, 250, 250)

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)


        val mBuilder =
            NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.pp)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setDefaults(DEFAULT_VIBRATE) //Important for heads-up notification
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_MAX)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "channel_name"
            val description = "channel_description"
            val importance =
                NotificationManager.IMPORTANCE_HIGH //Important for heads-up notification

            val channel = NotificationChannel("1", name, importance)
            channel.description = description
            channel.setShowBadge(true)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }


        val buildNotification = mBuilder.build()
        val mNotifyMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotifyMgr.notify(System.currentTimeMillis().toInt(), buildNotification)
    }

    private fun sendChatNotification(messageTitle: String, messageBody: String) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("NAME", messageTitle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, System.currentTimeMillis().toInt() /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val DEFAULT_VIBRATE_PATTERN = longArrayOf(0, 250, 250, 250)

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)


        val mBuilder =
            NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.pp)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setDefaults(DEFAULT_VIBRATE) //Important for heads-up notification
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_MAX)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "channel_name"
            val description = "channel_description"
            val importance =
                NotificationManager.IMPORTANCE_HIGH //Important for heads-up notification

            val channel = NotificationChannel("1", name, importance)
            channel.description = description
            channel.setShowBadge(true)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }


        val buildNotification = mBuilder.build()
        val mNotifyMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotifyMgr.notify(System.currentTimeMillis().toInt(), buildNotification)
    }


    companion object {

        private const val TAG = "MyFirebaseMsgService"
    }


}