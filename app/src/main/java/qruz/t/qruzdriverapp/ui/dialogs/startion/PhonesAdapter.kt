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


class PhonesAdapter(
    private var phones: List<String>,
    private var context: Activity
 ) :
    RecyclerView.Adapter<PhonesAdapter.StationViewHolder>() {

    private var textStatus = 1



    interface OnPhonesClick{
        fun setOnPhonesClick(model:StationUser)
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): PhonesAdapter.StationViewHolder {
        return StationViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.phones_item, viewGroup, false)
        )
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(viewHolder: StationViewHolder, i: Int) {

        val model = phones[i]


        viewHolder.number.text = model



        viewHolder.call.setOnClickListener(View.OnClickListener {


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


    }

    override fun getItemCount(): Int {
        return phones.size
    }

    fun setUsers(users: ArrayList<String>) {
        this.phones = users


        context.runOnUiThread(Runnable {
            notifyDataSetChanged()
        })


        Logger.d(users.size)
    }


    fun getUsers(): List<String> {
        return this.phones

    }


    public class StationViewHolder(private val parent: View) : RecyclerView.ViewHolder(parent) {

        public val number: TextView
        public val call: RelativeLayout




        init {
            number = parent.findViewById(R.id.number)
            call = parent.findViewById(R.id.call)
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