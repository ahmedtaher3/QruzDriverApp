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
import qruz.t.qruzdriverapp.databinding.StationSeatUserPickItemBinding
import qruz.t.qruzdriverapp.databinding.StationUserSeatsDropItemBinding
import qruz.t.qruzdriverapp.model.DriverTrips
import qruz.t.qruzdriverapp.model.StationSeatUser
import qruz.t.qruzdriverapp.model.StationUser
import java.util.*


class StationUsersSeatsDropAdapter(
    private var users: ArrayList<StationSeatUser>,
    private var context: Activity,
    private var onStationUsersClick: OnStationUsersClick
) :
    RecyclerView.Adapter<StationUsersSeatsDropAdapter.StationViewHolder>() {

    private var textStatus = 1
    private var itemsCopy = ArrayList<StationSeatUser>()



    interface OnStationUsersClick{
        fun setOnDropPhonesClick(model:StationSeatUser)
        fun setOnDropConfirmClick(model:StationSeatUser)

    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): StationUsersSeatsDropAdapter.StationViewHolder {




        return StationViewHolder(
            StationUserSeatsDropItemBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup, false
            )
        )
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(viewHolder: StationViewHolder, i: Int) {

        val model = users[i]


        viewHolder.mBinding?.name?.text = model.name
        viewHolder.mBinding?.bookingID?.text = model.booking_id.toString()


        viewHolder.mBinding?.call?.setOnClickListener(View.OnClickListener {


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
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + model))
                context.startActivity(intent)

            }

        })

        viewHolder.mBinding?.drop?.setOnClickListener(View.OnClickListener {
            onStationUsersClick.setOnDropConfirmClick(model)
        })



    }

    override fun getItemCount(): Int {
        return users.size
    }

    fun setUsers(users: ArrayList<StationSeatUser>) {
        this.users = users
        this.itemsCopy.addAll(users)

        context.runOnUiThread(Runnable {
            notifyDataSetChanged()
        })


        Logger.d(users.size)
    }


    fun getUsers(): ArrayList<StationSeatUser> {
        return this.users

    }


    inner class StationViewHolder(var binding: StationUserSeatsDropItemBinding) :
        RecyclerView.ViewHolder(binding.root) {


        var mBinding: StationUserSeatsDropItemBinding? = null

        init {
            this.mBinding = binding;


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
                if (model.name?.contains(text, ignoreCase = true)!!) {
                    users.add(model)
                }

            }

        }
        notifyDataSetChanged()
    }

}