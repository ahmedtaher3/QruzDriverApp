package qruz.t.qruzdriverapp.ui.main.fragments.business.map

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.orhanobut.logger.Logger
import qruz.t.qruzdriverapp.R
import qruz.t.qruzdriverapp.Utilities.CommonUtilities
import qruz.t.qruzdriverapp.model.DriverTrips
import qruz.t.qruzdriverapp.model.Station
import qruz.t.qruzdriverapp.ui.dialogs.startion.StationDialog
import java.util.*

class StationsAdapter(
    private var stations: ArrayList<Station>,
    private var onStationClick: OnStationClick
) : RecyclerView.Adapter<StationsAdapter.StationViewHolder>() {

    private var itemsCopy = ArrayList<Station>()


    interface OnStationClick {
        fun setOnStationClick(station: Station)

    }


    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): StationsAdapter.StationViewHolder {
        return StationViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.station_item, viewGroup, false)
        )
    }

    override fun onBindViewHolder(viewHolder: StationViewHolder, i: Int) {

        val model = stations[i]



        viewHolder.name.text = model.name
        if (model.date != null) {
            viewHolder.stationStartAt.text = CommonUtilities.convertToTime(model.date.toLong())

        }

        if (model.users == 0)
        {
            viewHolder.seats.text =  " No Seats"

        }

        else if (model.users == 0){
            viewHolder.seats.text = model.users.toString() + " " + "Seats"

        }
        else
        {
            viewHolder.seats.text = model.users.toString() + " " + "Seat"

        }

        viewHolder.itemView.setOnClickListener(View.OnClickListener {
            onStationClick.setOnStationClick(model)


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

        public val name: TextView
        public val seats: TextView
        public val stationStartAt: TextView


        init {
            name = parent.findViewById(R.id.stationName)
            seats = parent.findViewById(R.id.stationSeatsN)
            stationStartAt = parent.findViewById(R.id.stationStartAt)

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