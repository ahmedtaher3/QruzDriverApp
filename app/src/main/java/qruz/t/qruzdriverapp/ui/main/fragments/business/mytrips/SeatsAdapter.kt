package qruz.t.qruzdriverapp.ui.main.fragments.business.mytrips

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
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.orhanobut.logger.Logger
import qruz.t.qruzdriverapp.R
import qruz.t.qruzdriverapp.Utilities.CommonUtilities
import qruz.t.qruzdriverapp.model.SeatsTrips
import qruz.t.qruzdriverapp.model.StationUser
import java.util.*




class SeatsAdapter(
    private var trips: List<SeatsTrips>,
    private var context: Activity,
    private val onStationClick: OnStationClick
) :
    RecyclerView.Adapter<SeatsAdapter.ViewHolder>() {

 


    interface OnStationClick{
        fun setOnStationClick(model:SeatsTrips)
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.seats_trips_item, viewGroup, false)
        )
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {

        val model = trips[i]

        viewHolder.name.text = model.name
        viewHolder.startsAt.text = CommonUtilities.convertToTime(CommonUtilities.convertToMillis(model.startsAt))



        viewHolder.itemView.setOnClickListener {
            onStationClick.setOnStationClick(model)
        }


    }

    override fun getItemCount(): Int {
        return trips.size
    }

    fun setTrips(trips: ArrayList<SeatsTrips>) {
        this.trips = trips

        context.runOnUiThread(Runnable {
            notifyDataSetChanged()
        })

        Logger.d(trips.size)
    }


    fun getTrips(): List<SeatsTrips> {
        return this.trips

    }


    public class ViewHolder(private val parent: View) : RecyclerView.ViewHolder(parent) {

        val name: TextView
        val startsAt: TextView


        init {
            name = parent.findViewById(R.id.partner_name)
            startsAt = parent.findViewById(R.id.partner_startsAt)

        }

    }


 


}



 
 
 