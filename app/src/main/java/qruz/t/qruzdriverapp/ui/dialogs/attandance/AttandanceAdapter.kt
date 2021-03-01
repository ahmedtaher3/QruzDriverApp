package qruz.t.qruzdriverapp.ui.dialogs.attandance

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
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.logger.Logger
import com.qruz.BusinessTripAttendanceQuery
import qruz.t.qruzdriverapp.R
import java.util.*


class AttandanceAdapter(
    private var users: List<BusinessTripAttendanceQuery.BusinessTripAttendance>,
    private var context: Activity,
    private var onChangeStatusClick: OnChangeStatusClick
) :
    RecyclerView.Adapter<AttandanceAdapter.StationViewHolder>() {

    private var textStatus = 1


    interface OnChangeStatusClick {

        fun setOnChangeStatusClick(userId: String, userName: String,status: Boolean)
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): AttandanceAdapter.StationViewHolder {
        return StationViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.attandance_user_item, viewGroup, false)
        )
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(viewHolder: StationViewHolder, i: Int) {

        val model = users[i]

        viewHolder.name.text = model.name()
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
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + users[i].phone()))
                context.startActivity(intent)

            }

        })



        if (model.is_absent()!!)
        {
            viewHolder.absent.isChecked = true
            viewHolder.present.isChecked = false
        }
        else
        {
            viewHolder.absent.isChecked = false
            viewHolder.present.isChecked = true
        }
        viewHolder.absent.setOnClickListener(View.OnClickListener {

            onChangeStatusClick.setOnChangeStatusClick(model.id().toString(), model.name().toString(),true)
        })


        viewHolder.present.setOnClickListener(View.OnClickListener {
            onChangeStatusClick.setOnChangeStatusClick(model.id().toString(),model.name().toString(), false)
        })
    }

    override fun getItemCount(): Int {
        return users.size
    }

    fun setUsers(users: List<BusinessTripAttendanceQuery.BusinessTripAttendance>) {
        this.users = users

        context.runOnUiThread(Runnable {
            notifyDataSetChanged()
        })


        Logger.d(users.size)
    }


    fun getUsers(): List<BusinessTripAttendanceQuery.BusinessTripAttendance> {
        return this.users

    }


    public class StationViewHolder(private val parent: View) : RecyclerView.ViewHolder(parent) {

        public val name: TextView
        public val station_item_call: RelativeLayout
        public val present: RadioButton
        public val absent: RadioButton


        init {
            name = parent.findViewById(R.id.station_item_name)
            station_item_call = parent.findViewById(R.id.station_item_call)
            present = parent.findViewById(R.id.present)
            absent = parent.findViewById(R.id.absent)
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