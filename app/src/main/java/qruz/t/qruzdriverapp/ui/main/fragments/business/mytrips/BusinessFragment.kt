package qruz.t.qruzdriverapp.ui.main.fragments.business.mytrips


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
class BusinessFragment : BaseFragment<FragmentBusinessBinding>()  , TripsInterFace {

    private var param1: String? = null
    private var param2: String? = null

    public val TAG = "BusinessFragment"

    lateinit var fragmentBusinessBinding: FragmentBusinessBinding
    private lateinit var viewModel: BusinessViewModel
    private lateinit var businessAdapter: BusinessAdapter
    private lateinit var daysAdapter: DaysAdapter

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
        fragmentBusinessBinding = viewDataBinding
        viewModel = ViewModelProviders.of(this).get(BusinessViewModel::class.java)

        initRecyclerView()
        initDaysRecyclerView()
        subscribeObservers()


        viewModel.getDriverTrips(getTodayName()!!);
    }


    private fun subscribeObservers() {

        viewModel?.responseLive?.observe(viewLifecycleOwner, androidx.lifecycle.Observer { t ->


            if (!t.hasErrors()) {
                var driverTripsList = ArrayList<DriverTrips>()
                for (model in t.data()?.driverTrips()!!) {



                    var count = 0

                    for (model2 in model.stations()!!)
                    {
                        count += model2.users()?.size!!
                    }


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
                    driverTripsList.add(driverTrips)
                    businessAdapter.setTrips(driverTripsList)
                }
            } else {
                Logger.d(t.errors()[0].message())

            }

        })

        viewModel?.progress?.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            when (it) {
                0 -> {
                    fragmentBusinessBinding.tripsProgressBar.visibility = View.GONE
                }
                1 -> {
                    fragmentBusinessBinding.tripsProgressBar.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun initRecyclerView() {
        fragmentBusinessBinding.myTripsRecycler.layoutManager = LinearLayoutManager(baseActivity)
        businessAdapter =
            BusinessAdapter(ArrayList(), initGlide(), baseActivity.supportFragmentManager)
        fragmentBusinessBinding.myTripsRecycler.adapter = businessAdapter


    }


    private fun initDaysRecyclerView() {
        daysAdapter = DaysAdapter(ArrayList(), this)
        fragmentBusinessBinding.daysRecycler.layoutManager =  LinearLayoutManager(baseActivity, LinearLayoutManager.HORIZONTAL, false)
        fragmentBusinessBinding.daysRecycler.adapter = daysAdapter

        val arrayList = ArrayList<DayTrips>()
        val str4 = "Sunday"
        arrayList.add(DayTrips("Sun", str4, isToday(str4), isToday(str4)))
        val str5 = "Monday"
        arrayList.add(DayTrips("Mon", str5, isToday(str5), isToday(str5)))
        val str6 = "Tuesday"
        arrayList.add(DayTrips("Tus", str6, isToday(str6), isToday(str6)))
        val str7 = "Wednesday"
        arrayList.add(DayTrips("Wed", str7, isToday(str7), isToday(str7)))
        val str8 = "Thursday"
        arrayList.add(DayTrips("Thu", str8, isToday(str8), isToday(str8)))
        val str9 = "Friday"
        arrayList.add(DayTrips("Fri", str9, isToday(str9), isToday(str9)))
        val str10 = "Saturday"
        arrayList.add(DayTrips("Sat", str10, isToday(str10), isToday(str10)))


        for (model in arrayList)
        {
            if (model.isSelected() && model.isToday())
            {
                arrayList.remove(model)
                arrayList.add(0, model)
                break
            }
        }
        daysAdapter.setDays(arrayList)

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


        viewModel.getDriverTrips(str);

     }
}
