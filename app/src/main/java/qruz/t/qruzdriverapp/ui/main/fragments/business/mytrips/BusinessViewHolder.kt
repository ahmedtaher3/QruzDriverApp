package qruz.t.qruzdriverapp.ui.main.fragments.business.mytrips

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.qruz.model.Vehicle
import de.hdodenhof.circleimageview.CircleImageView
import qruz.t.qruzdriverapp.R
import qruz.t.qruzdriverapp.model.DriverTrips
import qruz.t.qruzdriverapp.model.Trip
import qruz.t.qruzdriverapp.ui.main.fragments.business.map.MapFragment

class BusinessViewHolder(private val parent: View) : RecyclerView.ViewHolder(parent) {
    private var requestManager: RequestManager? = null
    private var fragmentManager: FragmentManager? = null

    private val name: TextView
    private val startsAt: TextView
    private val tripType: TextView
    private val logo: CircleImageView

    fun onBind(
        trip: DriverTrips,
        requestManager: RequestManager?,
        fragmentManager: FragmentManager
    ) {
        this.requestManager = requestManager
        this.fragmentManager = fragmentManager
        parent.tag = this
        name.text = trip.name
        startsAt.text = trip.startsAt

        if (trip.isReturn == null)
            trip.isReturn = false

        if (trip.isReturn) {
            tripType.text = "Return"
        } else {
            tripType.text = "Go"

        }

        this.requestManager?.load(trip.partner.logo)?.placeholder(logo.drawable)?.into(logo)



        if (trip.flag == null)
            trip.flag = false

        if (trip.startsAt == null)
            trip.startsAt = ""

        parent.setOnClickListener(View.OnClickListener {
            replace_fragment(
                fragmentManager,
                MapFragment.newInstance(trip.flag, trip.id, trip.startsAt),
                "MapFragment"
            )

        })


    }

    init {
        name = parent.findViewById(R.id.partner_name)
        logo = parent.findViewById(R.id.partner_logo)
        startsAt = parent.findViewById(R.id.partner_startsAt)
        tripType = parent.findViewById(R.id.tripType)

    }

    private fun replace_fragment(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        tag: String
    ) {

        fragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_left,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_left
            )
            .add(
                R.id.Main_Container,
                fragment
            )
            .addToBackStack(tag)
            .commit()
    }
}