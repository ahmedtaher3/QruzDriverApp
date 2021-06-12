package qruz.t.qruzdriverapp.ui.main.fragments.business.seats

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.orhanobut.logger.Logger
import qruz.t.qruzdriverapp.R
import qruz.t.qruzdriverapp.Utilities.CommonUtilities
import qruz.t.qruzdriverapp.model.DriverTrips
import qruz.t.qruzdriverapp.model.Station
import qruz.t.qruzdriverapp.ui.dialogs.startion.StationDialog
import java.util.*

class StationsSeatsAdapter(
    private var activity: AppCompatActivity,
    private var stations: ArrayList<Station>,
    private var onStationClick: OnStationClick
) : RecyclerView.Adapter<StationsSeatsAdapter.StationViewHolder>() {

    private var itemsCopy = ArrayList<Station>()


    interface OnStationClick {
        fun setOnArrivedClick(station: Station)
        fun setOnPickClick(station: Station)
        fun setOnDropClick(station: Station)

    }


    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): StationsSeatsAdapter.StationViewHolder {
        return StationViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.station_seat_item, viewGroup, false)
        )
    }

    override fun onBindViewHolder(viewHolder: StationViewHolder, i: Int) {

        val model = stations[i]



        viewHolder.name.text = model.name
        if (model.date != null) {
            viewHolder.stationStartAt.text = CommonUtilities.convertToTime(model.date.toLong())

        }


        viewHolder.pick.setOnClickListener(View.OnClickListener {
            onStationClick.setOnPickClick(model)
        })

        viewHolder.drop.setOnClickListener(View.OnClickListener {
            onStationClick.setOnDropClick(model)
        })

        viewHolder.arrived.setOnClickListener(View.OnClickListener {
            onStationClick.setOnArrivedClick(model)
        })
    }

    override fun getItemCount(): Int {
        return stations.size
    }

    fun setTrips(users: ArrayList<Station>) {
        this.stations = users
        this.itemsCopy.addAll(users)

        try {
            notifyDataSetChanged()

        } catch (e: Exception) {

        }
    }


    fun getTrips(): ArrayList<Station> {
        return this.stations
    }


    public class StationViewHolder(private val parent: View) : RecyclerView.ViewHolder(parent) {

        val name: TextView
        val seats: TextView
        val stationStartAt: TextView
        val pick: Button
        val drop: Button
        val arrived: Button


        init {
            name = parent.findViewById(R.id.stationName)
            seats = parent.findViewById(R.id.stationSeatsN)
            stationStartAt = parent.findViewById(R.id.stationStartAt)
            pick = parent.findViewById(R.id.pick)
            arrived = parent.findViewById(R.id.arrived)
            drop = parent.findViewById(R.id.drop)

        }

    }


    fun filter(text: String) {
        this.stations.clear()



        if (text.isEmpty()) {
            this.stations.addAll(itemsCopy)
        } else {


            for (model in itemsCopy) {
                Logger.d(model.name + "\n" + text)
                if (model.name.contains(text, ignoreCase = true)) {
                    stations.add(model)
                }

            }

        }
        notifyDataSetChanged()
    }


}