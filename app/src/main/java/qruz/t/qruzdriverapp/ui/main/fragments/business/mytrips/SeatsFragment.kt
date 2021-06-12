package qruz.t.qruzdriverapp.ui.main.fragments.business.mytrips


import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.orhanobut.logger.Logger
import qruz.t.qruzdriverapp.R
import qruz.t.qruzdriverapp.Utilities.CommonUtilities
import qruz.t.qruzdriverapp.base.BaseFragment
import qruz.t.qruzdriverapp.databinding.FragmentBusinessBinding
import qruz.t.qruzdriverapp.model.DayTrips
import qruz.t.qruzdriverapp.model.DriverTrips
import qruz.t.qruzdriverapp.model.Partner
import qruz.t.qruzdriverapp.model.SeatsTrips
import qruz.t.qruzdriverapp.ui.main.fragments.business.mytrips.days.DaysAdapter
import qruz.t.qruzdriverapp.ui.main.fragments.business.mytrips.days.TripsInterFace
import qruz.t.qruzdriverapp.ui.main.fragments.business.seats.SeatsMapFragment
import java.util.*
import kotlin.collections.ArrayList
import kotlin.jvm.internal.Intrinsics


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BusinessFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SeatsFragment : BaseFragment<FragmentBusinessBinding>(), TripsInterFace ,
    SeatsAdapter.OnStationClick {

    private var param1: String? = null
    private var param2: String? = null

    public val TAG = "BusinessFragment"

    lateinit var binding: FragmentBusinessBinding
    private lateinit var viewModel: BusinessViewModel
    private lateinit var adapter: SeatsAdapter
    private lateinit var daysAdapter: DaysAdapter
    var fullDayList = ArrayList<SeatsTrips>()
    var upcomingList = ArrayList<SeatsTrips>()
    var pastlist = ArrayList<SeatsTrips>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Mapbox.getInstance(getApplicationContext(), getString(R.string.mapbox_access_token));

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BusinessFragment()
                .apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_business
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = viewDataBinding
        viewModel = ViewModelProviders.of(this).get(BusinessViewModel::class.java)

        initRecyclerView()
        initDaysRecyclerView()
        subscribeObservers()


        viewDataBinding.PastLayout.setOnClickListener {

            if (pastlist.isNullOrEmpty())
                binding.emptyLayout.visibility = View.VISIBLE
            else
                binding.emptyLayout.visibility = View.GONE



            adapter.setTrips(pastlist)


            viewDataBinding.upcomingLayout.background =
                ContextCompat.getDrawable(baseActivity, R.drawable.white_background)
            viewDataBinding.PastLayout.background =
                ContextCompat.getDrawable(baseActivity, R.drawable.blue_background)


        }

        viewDataBinding.upcomingLayout.setOnClickListener {

            if (upcomingList.isNullOrEmpty())
                binding.emptyLayout.visibility = View.VISIBLE
            else
                binding.emptyLayout.visibility = View.GONE



            adapter.setTrips(upcomingList)


            viewDataBinding.PastLayout.background =
                ContextCompat.getDrawable(baseActivity, R.drawable.white_background)
            viewDataBinding.upcomingLayout.background =
                ContextCompat.getDrawable(baseActivity, R.drawable.blue_background)


        }



        viewModel.getDriverSeatsTrips(getTodayName()!!);
    }


    private fun subscribeObservers() {

        viewModel?.responseSeatsLive?.observe(viewLifecycleOwner, androidx.lifecycle.Observer { t ->


            fullDayList.clear()
            upcomingList.clear()
            pastlist.clear()

            if (!t.hasErrors()) {


                for (model in t.data()?.driverSeatsTrips()!!) {


                    var count = 0

                    Logger.d(model.name())
                    var driverTrips = SeatsTrips(
                        model.id(),
                        model.name(),
                        model.starts_at()
                    )
                    fullDayList.add(driverTrips)
                }


                for (trip in fullDayList) {


                    if (CommonUtilities.convertToMillis(trip.startsAt) + 1800000 > System.currentTimeMillis()) {

                        upcomingList.add(trip)

                    } else {

                        pastlist.add(trip)
                    }
                }

                if (upcomingList.isNullOrEmpty())
                    binding.emptyLayout.visibility = View.VISIBLE
                else
                    binding.emptyLayout.visibility = View.GONE

                adapter.setTrips(upcomingList)


            } else {
                Logger.d(t.errors()[0].message())

            }

        })

        viewModel?.responseSeatsLiveTrips?.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { t ->
                fullDayList.clear()
                upcomingList.clear()
                pastlist.clear()

                if (!t.hasErrors()) {

                    if (t.data()?.driverLiveSeatsTrips()!!.size>0)
                    {


                        viewModel.dataManager.saveIsTripLive(true)
                        viewModel.dataManager.saveTripId(t.data()?.driverLiveSeatsTrips()?.get(0)?.id())
                        viewModel.dataManager.saveLogId(t.data()?.driverLiveSeatsTrips()?.get(0)?.log_id())

                    }

                    for (model in t.data()?.driverLiveSeatsTrips()!!) {


                        Logger.d(model.name())
                        var driverTrips = SeatsTrips(
                            model.id(),
                            model.name(),
                            model.starts_at()
                        )



                        fullDayList.add(driverTrips)
                    }


                    if (fullDayList.isNullOrEmpty())
                        binding.emptyLayout.visibility = View.VISIBLE
                    else
                        binding.emptyLayout.visibility = View.GONE

                    adapter.setTrips(fullDayList)


                } else {
                    Logger.d(t.errors()[0].message())

                }

            })




        viewModel?.progress?.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            when (it) {
                0 -> {
                    binding.tripsProgressBar.visibility = View.GONE
                }
                1 -> {
                    binding.tripsProgressBar.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun initRecyclerView() {
        binding.myTripsRecycler.layoutManager = LinearLayoutManager(baseActivity)
        adapter = SeatsAdapter(ArrayList(),baseActivity , this)
        binding.myTripsRecycler.adapter = adapter


    }


    private fun initDaysRecyclerView() {
        daysAdapter = DaysAdapter(ArrayList(), this)
        binding.daysRecycler.layoutManager =
            LinearLayoutManager(baseActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.daysRecycler.adapter = daysAdapter

        val arrayList = ArrayList<DayTrips>()
        val str10 = "Saturday"
        arrayList.add(DayTrips("Sat", str10, isToday(str10), isToday(str10), true))
        val str4 = "Sunday"
        arrayList.add(DayTrips("Sun", str4, isToday(str4), isToday(str4), true))
        val str5 = "Monday"
        arrayList.add(DayTrips("Mon", str5, isToday(str5), isToday(str5), true))
        val str6 = "Tuesday"
        arrayList.add(DayTrips("Tus", str6, isToday(str6), isToday(str6), true))
        val str7 = "Wednesday"
        arrayList.add(DayTrips("Wed", str7, isToday(str7), isToday(str7), true))
        val str8 = "Thursday"
        arrayList.add(DayTrips("Thu", str8, isToday(str8), isToday(str8), true))
        val str9 = "Friday"
        arrayList.add(DayTrips("Fri", str9, isToday(str9), isToday(str9), true))


        val newList = ArrayList<DayTrips>()

        for (model in arrayList) {

            Logger.d(model.isToday().toString())
            if (model.isToday()) {

                break
            } else {

                model.isVisible = false
            }

        }


        newList.add(DayTrips(getString(R.string.live_now), getString(R.string.live_now), false, false, true))


        for (model in arrayList) {
            Logger.d(model.isVisible().toString())


            if (model.isVisible()) {

                newList.add(model)
            }

        }

        daysAdapter.setDays(newList)

    }


    private fun initGlide(): RequestManager {
        val options = RequestOptions()

        return Glide.with(this)
            .setDefaultRequestOptions(options)
    }


    private fun getTodayName(): String? {
        val instance = Calendar.getInstance()
        Intrinsics.checkExpressionValueIsNotNull(instance, "Calendar.getInstance()")
        val displayName = instance.getDisplayName(7, 2, Locale.ENGLISH)
        if (displayName == null) {
            Intrinsics.throwNpe()
        }
        return displayName
    }

    private fun isToday(str: String): Boolean {
        val instance = Calendar.getInstance()
        Intrinsics.checkExpressionValueIsNotNull(instance, "Calendar.getInstance()")
        return Intrinsics.areEqual(
            instance.getDisplayName(7, 2, Locale.ENGLISH) as Any,
            str as Any
        )
    }

    override fun getTrips(str: String) {
        binding.emptyLayout.visibility = View.GONE



        if (str == getString(R.string.live_now)) {
            binding.upcomingPastLayout.visibility = View.GONE
            viewModel.getDriverSeatsLiveTrips();
        } else {
            if (isToday(str))
                binding.upcomingPastLayout.visibility = View.VISIBLE
            else {
                binding.upcomingPastLayout.visibility = View.GONE


                viewDataBinding.PastLayout.background =
                    ContextCompat.getDrawable(baseActivity, R.drawable.white_background)
                viewDataBinding.upcomingLayout.background =
                    ContextCompat.getDrawable(baseActivity, R.drawable.blue_background)

            }
            viewModel.getDriverSeatsTrips(str);
        }

        adapter.setTrips(ArrayList())


    }

    override fun setOnStationClick(model: SeatsTrips) {



        baseActivity.supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_left,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_left
                )
                .add(
                    R.id.Main_Container,
                    SeatsMapFragment.newInstance(model.id.toString(),model.startsAt!!, CommonUtilities.convertToMillis(model.startsAt))
                )
                .addToBackStack(tag)
                .commit()
        }

}
