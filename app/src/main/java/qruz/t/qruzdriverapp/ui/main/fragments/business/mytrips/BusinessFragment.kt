package qruz.t.qruzdriverapp.ui.main.fragments.business.mytrips


import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.orhanobut.logger.Logger
import qruz.t.qruzdriverapp.R
import qruz.t.qruzdriverapp.base.BaseFragment
import qruz.t.qruzdriverapp.databinding.FragmentBusinessBinding
import qruz.t.qruzdriverapp.model.DayTrips
import qruz.t.qruzdriverapp.model.DriverTrips
import qruz.t.qruzdriverapp.model.Partner
import qruz.t.qruzdriverapp.ui.main.fragments.business.mytrips.days.DaysAdapter
import qruz.t.qruzdriverapp.ui.main.fragments.business.mytrips.days.TripsInterFace
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
class BusinessFragment : BaseFragment<FragmentBusinessBinding>(), TripsInterFace {

    private var param1: String? = null
    private var param2: String? = null

    public val TAG = "BusinessFragment"

    lateinit var binding: FragmentBusinessBinding
    private lateinit var viewModel: BusinessViewModel
    private lateinit var businessAdapter: BusinessAdapter
    private lateinit var daysAdapter: DaysAdapter
    var fullDayList = ArrayList<DriverTrips>()
    var upcomingList = ArrayList<DriverTrips>()
    var pastlist = ArrayList<DriverTrips>()
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
            BusinessFragment().apply {
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



            businessAdapter.setTrips(pastlist)


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



            businessAdapter.setTrips(upcomingList)


            viewDataBinding.PastLayout.background =
                ContextCompat.getDrawable(baseActivity, R.drawable.white_background)
            viewDataBinding.upcomingLayout.background =
                ContextCompat.getDrawable(baseActivity, R.drawable.blue_background)


        }



        viewModel.getDriverTrips(getTodayName()!!);
    }


    private fun subscribeObservers() {

        viewModel?.responseLive?.observe(viewLifecycleOwner, androidx.lifecycle.Observer { t ->


            fullDayList.clear()
            upcomingList.clear()
            pastlist.clear()

            if (!t.hasErrors()) {


                for (model in t.data()?.driverTrips()!!) {


                    var count = 0




                    Logger.d(model.name())
                    var driverTrips = DriverTrips(
                        model.id(),
                        model.name(),
                        model.dayName(),
                        model.date().toString(),
                        model.startsAt(),
                        model.flag(),
                        Partner(
                            model.partner()?.id(),
                            model.partner()?.name(),
                            model.partner()?.logo()
                        ),
                        count.toString(),
                        model.isReturn
                    )
                    fullDayList.add(driverTrips)
                }


                for (trip in fullDayList) {


                    if (trip.date.toLong() + 1800000 > System.currentTimeMillis()) {

                        upcomingList.add(trip)

                    } else {

                        pastlist.add(trip)
                    }
                }

                if (upcomingList.isNullOrEmpty())
                    binding.emptyLayout.visibility = View.VISIBLE
                else
                    binding.emptyLayout.visibility = View.GONE

                businessAdapter.setTrips(upcomingList)


            } else {
                Logger.d(t.errors()[0].message())

            }

        })

        viewModel?.responseLiveTrips?.observe(viewLifecycleOwner, androidx.lifecycle.Observer { t ->
            fullDayList.clear()
            upcomingList.clear()
            pastlist.clear()

            if (!t.hasErrors()) {


                for (model in t.data()?.driverLiveBusinessTrips()!!) {


                    var count = 0

                    


                    Logger.d(model.name())
                    var driverTrips = DriverTrips(
                        model.id(),
                        model.name(),
                        model.dayName(),
                        model.date().toString(),
                        model.startsAt(),
                        model.flag(),
                        Partner(
                            model.partner()?.id(),
                            model.partner()?.name(),
                            model.partner()?.logo()
                        ),
                        count.toString(),
                        model.isReturn
                    )



                    fullDayList.add(driverTrips)
                }


                if (fullDayList.isNullOrEmpty())
                    binding.emptyLayout.visibility = View.VISIBLE
                else
                    binding.emptyLayout.visibility = View.GONE

                businessAdapter.setTrips(fullDayList)


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
        businessAdapter =
            BusinessAdapter(ArrayList(),  initGlide(), baseActivity.supportFragmentManager)
        binding.myTripsRecycler.adapter = businessAdapter


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


        newList.add(DayTrips("Live Now", "Live Now", false, false, true))


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



        if (str == "Live Now") {
            binding.upcomingPastLayout.visibility = View.GONE
            viewModel.getDriverLiveTrips();
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
            viewModel.getDriverTrips(str);
        }

        businessAdapter.setTrips(ArrayList())


    }
}
