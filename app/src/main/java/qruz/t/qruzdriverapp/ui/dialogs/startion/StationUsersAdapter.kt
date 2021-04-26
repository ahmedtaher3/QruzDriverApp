package qruz.t.qruzdriverapp.ui.dialogs.startion

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
import qruz.t.qruzdriverapp.R
import qruz.t.qruzdriverapp.model.DriverTrips
import qruz.t.qruzdriverapp.model.StationUser
import java.util.*


class StationUsersAdapter(
    private var users: ArrayList<StationUser>,
    private var context: Activity,
    private var onPhonesClick: OnPhonesClick
) :
    RecyclerView.Adapter<StationUsersAdapter.StationViewHolder>() {

    private var textStatus = 1
    private var itemsCopy = ArrayList<StationUser>()



    interface OnPhonesClick{
        fun setOnPhonesClick(model:StationUser)
        fun setOnChatClick(model:StationUser)
    }

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
        viewHolder.station_type.text = model.station_type

        if (model.secondary_no.isNullOrEmpty())
        {
            viewHolder.additional_numbers.visibility = View.GONE
        }
        else
        {
            viewHolder.additional_numbers.visibility = View.VISIBLE

        }

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

        viewHolder.additional_numbers.setOnClickListener(View.OnClickListener {

            onPhonesClick.setOnPhonesClick(model)

        })

        viewHolder.chat.setOnClickListener(View.OnClickListener {

            onPhonesClick.setOnChatClick(model)

        })
    }

    override fun getItemCount(): Int {
        return users.size
    }

    fun setUsers(users: ArrayList<StationUser>) {
        this.users = users
        this.itemsCopy.addAll(users)

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
        public val station_type: TextView
        public val isPickedUp: CheckBox
        public val station_item_call: RelativeLayout
        public val additional_numbers: RelativeLayout
        public val chat: RelativeLayout


        init {
            name = parent.findViewById(R.id.station_item_name)
            station_type = parent.findViewById(R.id.station_type)
            isPickedUp = parent.findViewById(R.id.station_item_picked_up_check_box)
            station_item_call = parent.findViewById(R.id.station_item_call)
            additional_numbers = parent.findViewById(R.id.additional_numbers)
            chat = parent.findViewById(R.id.chat)
        }


    }


    fun setTextStatus(textStatus: Int) {
        this.textStatus = textStatus
        notifyDataSetChanged()
    }

    fun getTextStatus(): Int {
        return this.textStatus
    }


    fun filter(text: String) {
         this.users.clear()



        if (text.isEmpty()) {
             this.users.addAll(itemsCopy)
        } else {



            for (model in itemsCopy) {
                Logger.d(model.name+"\n" + text)
                if (model.name.contains(text, ignoreCase = true)) {
                    users.add(model)
                }

            }

        }
        notifyDataSetChanged()
    }

}