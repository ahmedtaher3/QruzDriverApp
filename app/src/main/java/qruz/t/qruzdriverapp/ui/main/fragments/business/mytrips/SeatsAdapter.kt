package qruz.t.qruzdriverapp.ui.main.fragments.business.mytrips

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.orhanobut.logger.Logger
import qruz.t.qruzdriverapp.R
import qruz.t.qruzdriverapp.model.DriverTrips
import java.util.*

class BusinessAdapter(
    private var trips: ArrayList<DriverTrips>,

    private val requestManager: RequestManager,
    var fragmentManager: FragmentManager
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var itemsCopy = ArrayList<DriverTrips>()
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        return BusinessViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.my_trips_item, viewGroup, false)
        )
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        (viewHolder as BusinessViewHolder).onBind(trips[i], requestManager, fragmentManager)
    }

    override fun getItemCount(): Int {
        return trips.size
    }

    fun setTrips(trips: ArrayList<DriverTrips>) {
        this.trips = trips
        this.itemsCopy.addAll(trips)

        Logger.d("setTrips" + itemsCopy.size.toString() )
        notifyDataSetChanged()
    }

    fun filter(text: String) {
        Logger.d("filter" + itemsCopy.size.toString() )
        this.trips.clear()
        Logger.d("filter" + itemsCopy.size.toString() )



        if (text.isEmpty()) {
            Logger.d("if" + trips.size.toString() +"\n" + text)
            this.trips.addAll(itemsCopy)
        } else {
            Logger.d("else" + trips.size.toString()+"\n" + text+ "  itemsCopy ="+itemsCopy.size.toString())



            for (model in itemsCopy) {
                Logger.d(model.name+"\n" + text)
                if (model.name.contains(text, ignoreCase = true)) {
                    trips.add(model)
                }

            }

        }
        notifyDataSetChanged()
    }

}