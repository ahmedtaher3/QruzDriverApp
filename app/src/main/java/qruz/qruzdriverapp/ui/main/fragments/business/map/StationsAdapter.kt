package qruz.qruzdriverapp.ui.main.fragments.business.map

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import qruz.qruzdriverapp.R
import qruz.qruzdriverapp.Utilities.CommonUtilities
import qruz.qruzdriverapp.model.Station
import qruz.qruzdriverapp.ui.dialogs.startion.StationDialog
import java.util.*

class StationsAdapter(
    private var stations: ArrayList<Station>
    , private var context: Activity
) :
    RecyclerView.Adapter<StationsAdapter.StationViewHolder>() {


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
        viewHolder.seats.text = model.users.toString() + " " + "Seats"

        viewHolder.itemView.setOnClickListener(View.OnClickListener {

            val cdd = StationDialog(context, model.id, null)
            com.orhanobut.logger.Logger.d(model.id)
            cdd.setCanceledOnTouchOutside(true)
            cdd.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            cdd.getWindow()?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            cdd.show()

        })
    }

    override fun getItemCount(): Int {
        return stations.size
    }

    fun setTrips(users: ArrayList<Station>) {
        this.stations = users
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


}