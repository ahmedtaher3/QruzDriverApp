package qruz.qruzdriverapp.ui.main.fragments.business.mytrips

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.qruz.model.Vehicle
import qruz.qruzdriverapp.R
import qruz.qruzdriverapp.model.DriverTrips
import qruz.qruzdriverapp.model.Trip
import java.util.ArrayList

class BusinessAdapter (private var trips: ArrayList<DriverTrips>, private val requestManager: RequestManager, var fragmentManager: FragmentManager) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        return BusinessViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.my_trips_item, viewGroup, false))
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        (viewHolder as BusinessViewHolder).onBind(trips[i], requestManager , fragmentManager)
    }

    override fun getItemCount(): Int {
        return trips.size
    }

    fun setTrips(trips: ArrayList<DriverTrips>) {
        this.trips = trips
        notifyDataSetChanged()
    }



}