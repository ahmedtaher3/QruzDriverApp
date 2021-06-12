package qruz.qruzdriverapp.ui.dialogs.startion

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.logger.Logger
import qruz.qruzdriverapp.R
import qruz.qruzdriverapp.model.StationUser
import java.util.*


class StationUsersAdapter(
    private var users: ArrayList<StationUser>,
    private var context: Activity
) :
    RecyclerView.Adapter<StationUsersAdapter.StationViewHolder>() {

    private var textStatus = 1



    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): StationUsersAdapter.StationViewHolder {
        return StationViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.station_user_item, viewGroup, false)
        )
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(viewHolder: StationViewHolder, i: Int) {

        val model = users[i]

        viewHolder.isPickedUp.tag = this
        viewHolder.name.text = model.name


        viewHolder.isPickedUp.setOnClickListener(View.OnClickListener {

            users[i].isPickedUp = viewHolder.isPickedUp.isChecked
        })


        viewHolder.station_item_call.setOnClickListener(View.OnClickListener {


            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (context as Activity),
                        Manifest.permission.CALL_PHONE
                    )
                ) {

                } else {
                    ActivityCompat.requestPermissions(
                        context, arrayOf(Manifest.permission.CALL_PHONE),
                        1
                    )
                }
            } else {
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + users[i].phone))
                context.startActivity(intent)

            }

        })
    }

    override fun getItemCount(): Int {
        return users.size
    }

    fun setUsers(users: ArrayList<StationUser>) {
        this.users = users

        context.runOnUiThread(Runnable {
            notifyDataSetChanged()
        })


        Logger.d(users.size)
    }


    fun getUsers(): ArrayList<StationUser> {
        return this.users

    }


    public class StationViewHolder(private val parent: View) : RecyclerView.ViewHolder(parent) {

        public val name: TextView
        public val isPickedUp: CheckBox
        public val station_item_call: RelativeLayout


        init {
            name = parent.findViewById(R.id.station_item_name)
            isPickedUp = parent.findViewById(R.id.station_item_picked_up_check_box)
            station_item_call = parent.findViewById(R.id.station_item_call)
        }


    }


    fun setTextStatus(textStatus: Int) {
        this.textStatus = textStatus
        notifyDataSetChanged()
    }

    fun getTextStatus(): Int {
        return this.textStatus
    }
}