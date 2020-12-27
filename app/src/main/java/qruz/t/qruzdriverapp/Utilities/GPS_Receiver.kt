package qruz.t.qruzdriverapp.Utilities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.orhanobut.logger.Logger

class GPS_Receiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Logger.i(
            GPS_Receiver::class.java.getSimpleName(),
            "Service Stops! Oooooooooooooppppssssss!!!!"
        )

        context.startService(Intent(context, GPS_Receiver::class.java))

    }
}
