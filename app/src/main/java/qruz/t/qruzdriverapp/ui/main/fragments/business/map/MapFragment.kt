package qruz.t.qruzdriverapp.ui.main.fragments.business.map


import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.orhanobut.logger.Logger
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.PrivateChannel
import com.pusher.client.channel.PrivateChannelEventListener
import com.pusher.client.channel.PusherEvent
import com.pusher.client.util.HttpAuthorizer
import com.qruz.TripQuery
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import mumayank.com.airlocationlibrary.AirLocation
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import qruz.t.qruzdriverapp.Helper.DataParser
import qruz.t.qruzdriverapp.R
import qruz.t.qruzdriverapp.Utilities.CommonUtilities
import qruz.t.qruzdriverapp.Utilities.CommonUtilities.bitmapDescriptorFromVector
import qruz.t.qruzdriverapp.base.BaseFragment
import qruz.t.qruzdriverapp.databinding.FragmentMapBinding
import qruz.t.qruzdriverapp.model.Station
import qruz.t.qruzdriverapp.model.StationUser
import qruz.t.qruzdriverapp.ui.auth.splach.SplashActivity
import qruz.t.qruzdriverapp.ui.dialogs.attandance.AttandanceDialog
import qruz.t.qruzdriverapp.ui.dialogs.chat.ChatDialog
import qruz.t.qruzdriverapp.ui.dialogs.chat.DirectChatDialog
import qruz.t.qruzdriverapp.ui.dialogs.startion.PhonesDialog
import qruz.t.qruzdriverapp.ui.dialogs.startion.StationDialog
import qruz.t.qruzdriverapp.ui.dialogs.startion.StationInterface
import qruz.t.qruzdriverapp.ui.main.MainActivity
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


private const val ARG_START_AT = "startAt"
private const val ARG_TRIP_ID = "tripId"
private const val ARG_DATE = "date"


/**
 * A simple [Fragment] subclass.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


class MapFragment : BaseFragment<FragmentMapBinding>(), View.OnClickListener, OnMapReadyCallback,
    StationsAdapter.OnStationClick, StationInterface {

    var startAt = ""
    var tripId = "0"
    var date: Long = 0
    var counter = 0
    var startAtTime = 0
    private lateinit var airLocation: AirLocation


    var disposable: Disposable? = null


    lateinit var binding: FragmentMapBinding
    private lateinit var viewModel: MapViewModel

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private val INTERVAL: Long = 7000
    private val FASTEST_INTERVAL: Long = 7000
    private var mLastLocation: Location? = null
    internal lateinit var mLocationRequest: LocationRequest
    private val REQUEST_PERMISSION_LOCATION = 10
    private var ch: PrivateChannel? = null
    var points: LatLngBounds.Builder? = null
    var mMap: GoogleMap? = null
    var mapFragment: SupportMapFragment? = null
    var latLngs: ArrayList<LatLng>? = null
    var origin: LatLng? = null
    var destination: LatLng? = null
    var latLngsWayPoints: ArrayList<LatLng>? = null


    var adapter: StationsAdapter? = null


    var group_chat = false

    var model: TripQuery.Trip? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        arguments?.let {
            startAt = it.getString(ARG_START_AT).toString()
            tripId = it.getString(ARG_TRIP_ID).toString()
            date = it.getLong(ARG_DATE)


        }
        latLngs = ArrayList()
        latLngsWayPoints = ArrayList()
        viewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)
        mLocationRequest = LocationRequest()

        val locationManager =
            baseActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        }


    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = viewDataBinding


        Observable.interval(0, 1, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : io.reactivex.Observer<Long> {
                override fun onSubscribe(d: Disposable) {
                    disposable = d
                }

                override fun onNext(aLong: Long) {


                    counter = counter + 1

                }

                override fun onError(e: Throwable) {}
                override fun onComplete() {}
            })



        if (mMap == null) {
            val fm = childFragmentManager
            mapFragment = fm.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(this)
        }





        if (viewModel.dataManager.isTripLive) {

            binding.startTripCardView.visibility = View.GONE
            binding.bottomSheet.visibility = View.VISIBLE

            viewModel.tripLiveTrip(viewModel.dataManager.tripId)

        } else {

            binding.startTripCardView.visibility = View.VISIBLE
            binding.bottomSheet.visibility = View.GONE

            viewModel.tripLiveTrip(tripId.toString().toString())
        }


        adapter = StationsAdapter(baseActivity, ArrayList(), this)
        binding.stations.adapter = adapter



        setLiseners()

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        googleMap.getUiSettings().setMyLocationButtonEnabled(false)
        googleMap.getUiSettings().setMapToolbarEnabled(false)
        googleMap.getUiSettings().setZoomControlsEnabled(false)
        googleMap.getUiSettings().setCompassEnabled(false)
        if (ActivityCompat.checkSelfPermission(
                baseActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                baseActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMarkerClickListener(GoogleMap.OnMarkerClickListener {

            onMarkerClick(it)
        })
        if (viewModel.dataManager.isTripLive) {
            Handler().post(Runnable {
                mMap?.setPadding(
                    0,
                    0,
                    0,
                    150
                )
            })
        } else {


            Handler().post(Runnable {
                mMap?.setPadding(
                    0,
                    binding.startTripCardView.getHeight(),
                    0,
                    0
                )
            })
        }
        subscribeObservers()


    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MapFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(tripId: String, startAt: String, date: Long) =
            MapFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_START_AT, startAt)
                    putString(ARG_TRIP_ID, tripId)
                    putLong(ARG_DATE, date)
                }
            }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_map
    }

    private fun buildAlertMessageNoGps() {

        val builder = AlertDialog.Builder(baseActivity)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                startActivityForResult(
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 11
                )
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.cancel()
                baseActivity.onBackPressed()
            }
        val alert: AlertDialog = builder.create()
        alert.show()


    }

    protected fun startLocationUpdates() {

        // Create the location request to start receiving updates

        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest!!.setInterval(INTERVAL)
        mLocationRequest!!.setSmallestDisplacement(20.0F)
        mLocationRequest!!.setFastestInterval(FASTEST_INTERVAL)

        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        val locationSettingsRequest = builder.build()

        val settingsClient = LocationServices.getSettingsClient(baseActivity)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(baseActivity)
        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                baseActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                baseActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {


            return
        }
        mFusedLocationProviderClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // do work here
            locationResult.lastLocation
            onLocationChanged(locationResult.lastLocation)
        }
    }

    fun onLocationChanged(location: Location) {
        // New location has now been determined

        mLastLocation = location
        if (viewModel.dataManager.isTripLive) {
            try {

                if (ch != null) {
                    val sb = java.lang.StringBuilder()
                    sb.append("{\"latitude\":\"")
                    sb.append(java.lang.String.valueOf(location.latitude))
                    sb.append("\",\"longitude\":\"")
                    sb.append(java.lang.String.valueOf(location.longitude))
                    sb.append("\"}")
                    ch?.trigger("client-driver.location", sb.toString())

                    System.out.println("pusherEvent" + viewModel.dataManager.getLogId())

                    System.out.println("client-driver.location" + sb.toString())
                }


            } catch (e: java.lang.Exception) {
                Logger.d(e.message)
            }

            if (counter > 420) {

                if (viewModel.dataManager.logId != null && viewModel.dataManager.tripId != null) {
                    viewModel.updateBusinessTripDriverLocation(
                        location.latitude.toString(),
                        location.longitude.toString()
                    )
                    counter = 1
                }

            }
        }

    }

    private fun stoplocationUpdates() {

        try {
            mFusedLocationProviderClient!!.removeLocationUpdates(mLocationCallback)

        } catch (e: Exception) {

        }
    }

    private fun setLiseners() {
        binding.startTrip.setOnClickListener(this)
        binding.endTrip.setOnClickListener(this)
        binding.myLocation.setOnClickListener(this)
        binding.zoomRoute.setOnClickListener(this)
        binding.navigation.setOnClickListener(this)
        binding.openChat.setOnClickListener(this)
        binding.passengersButton.setOnClickListener(this)
        binding.attandance.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.startTrip -> {

                Logger.d(System.currentTimeMillis().toString() + "\n" + date.toString())

                if (System.currentTimeMillis() > (date.toLong() + 1800000)) {
                    Toast.makeText(baseActivity, "cant start trip now", Toast.LENGTH_SHORT).show()

                } else if (System.currentTimeMillis() < (date.toLong() - 1800000)) {
                    Toast.makeText(baseActivity, "cant start trip now", Toast.LENGTH_SHORT).show()


                } else {
                    val dialogBuilder = android.app.AlertDialog.Builder(baseActivity)
                    // ...Irrelevant code for customizing the buttons and title
                    val inflater = this.layoutInflater
                    val dialogView = inflater.inflate(R.layout.start_trip_confirm, null)
                    dialogBuilder.setView(dialogView)

                    val cancel = dialogView.findViewById<View>(R.id.cancel) as Button
                    val yes = dialogView.findViewById<View>(R.id.yes) as TextView

                    val alertDialog = dialogBuilder.create()
                    cancel.setOnClickListener {
                        alertDialog.dismiss()
                    }
                    yes.setOnClickListener {
                        alertDialog.dismiss()

                        if (mLastLocation != null) {
                            viewModel.startTrip(
                                tripId.toString(),
                                mLastLocation?.latitude.toString(),
                                mLastLocation?.longitude.toString()
                            )
                        } else {
                            viewModel.startTrip(
                                tripId.toString(),
                                "0.0",
                                "0.0"
                            )
                        }
                    }
                    alertDialog.getWindow()
                        ?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

                    alertDialog.show()


                }

            }

            R.id.endTrip -> {

                if ((System.currentTimeMillis() - viewModel.dataManager.startAtTime) > (startAtTime * 60 * 1000) / 2) {
                    val dialogBuilder = android.app.AlertDialog.Builder(baseActivity)
                    // ...Irrelevant code for customizing the buttons and title
                    val inflater = this.layoutInflater
                    val dialogView = inflater.inflate(R.layout.start_trip_confirm, null)
                    dialogBuilder.setView(dialogView)

                    val cancel = dialogView.findViewById<View>(R.id.cancel) as Button
                    val yes = dialogView.findViewById<View>(R.id.yes) as TextView

                    val alertDialog = dialogBuilder.create()
                    cancel.setOnClickListener {
                        alertDialog.dismiss()
                    }
                    yes.setOnClickListener {
                        alertDialog.dismiss()
                        if (mLastLocation != null) {


                            viewModel.endTrip(
                                viewModel.dataManager?.tripId,
                                viewModel.dataManager?.logId + "",
                                mLastLocation?.latitude.toString(),
                                mLastLocation?.longitude.toString()

                            )


                        } else {
                            viewModel.endTrip(
                                viewModel.dataManager?.tripId,
                                viewModel.dataManager?.logId + "",
                                "0.0",
                                "0.0"
                            )
                        }
                    }
                    alertDialog.getWindow()
                        ?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
                    alertDialog.show()


                } else {
                    Snackbar.make(view!!, getString(R.string.cant_end), Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(resources.getColor(R.color.red))
                        .setAction("Action", null).show();

                }

            }

            R.id.my_location -> {


                if (mLastLocation != null) {
                    val position = CameraPosition.Builder()
                        .target(
                            LatLng(
                                mLastLocation!!.latitude,
                                mLastLocation!!.longitude
                            )
                        ) // Sets the new camera position
                        .zoom(17f) // Sets the zoom
                        .build(); // Creates a CameraPosition from the builder


                    if (mMap != null) {
                        mMap!!.animateCamera(
                            CameraUpdateFactory.newCameraPosition(position)
                        );
                    }
                }


            }

            R.id.zoom_route -> {


                if (points != null) {
                    val bounds = points?.build()
                    val cu: CameraUpdate = CameraUpdateFactory.newLatLngBounds(
                        bounds,
                        100
                    )
                    mMap?.animateCamera(cu, 200, null)
                }


                /* try {   // zero padding
                    val cameraPosition2 =
                        map_boxMap?.getCameraForLatLngBounds(
                            bounds.build(),
                            intArrayOf(100, 100, 100, 100)
                        )
                    map_boxMap?.easeCamera(
                        CameraUpdateFactory.newCameraPosition(cameraPosition2!!),
                        1000
                    )
                } catch (e: Exception) {

                }*/


            }

            R.id.attandance -> {

                open_attandance_dialog()
                /*   val intent = Intent(
                       Intent.ACTION_VIEW,
                       Uri.parse(getNavigationUrl(latLngs!!))
                   )
                   intent.setClassName(
                       "com.google.android.apps.maps",
                       "com.google.android.maps.MapsActivity"
                   )
                   startActivity(intent)

                   *//* if (route != null) {
                    val options = NavigationLauncherOptions.builder()
                        .directionsRoute(route)
                        .build()

                    NavigationLauncher.startNavigation(baseActivity, options)
                }*/


            }

            R.id.navigation -> {

                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(getNavigationUrl(latLngsWayPoints!!, origin!!, destination!!))
                )
                intent.setClassName(
                    "com.google.android.apps.maps",
                    "com.google.android.maps.MapsActivity"
                )
                startActivity(intent)


            }


            R.id.openChat -> {


                if (group_chat)
                    open_chat_dialog()
                else
                    Toast.makeText(
                        baseActivity,
                        "Chat service not available now",
                        Toast.LENGTH_LONG
                    ).show();


            }

            R.id.passengersButton -> {


                open_station_dialog(
                    null,
                    binding.name.text.toString(),
                    viewModel.dataManager.tripId
                )


            }
        }
    }

    private fun runtime_permissions(): Boolean {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(
                baseActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                baseActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 100
            )
            return true
        }
        return false
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onResume() {
        super.onResume()

        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()

    }

    override fun onStop() {
        super.onStop()

        stoplocationUpdates()
    }

    override fun onLowMemory() {
        super.onLowMemory()

    }

    override fun onDestroy() {
        super.onDestroy()

        stoplocationUpdates()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    private fun subscribeObservers() {


        viewModel?.responseLive?.observe(viewLifecycleOwner, Observer { t ->


            if (!t.hasErrors()) {

                Logger.d(t.data().toString())
                model = t.data()?.trip()

                if (t.data()?.trip()?.group_chat() != null)
                    group_chat = t.data()?.trip()?.group_chat()!!


                binding?.name.text = t.data()?.trip()?.name()

                try {
                } catch (e: Exception) {
                }

                try {
                    binding?.startStation.text = t.data()?.trip()?.stations()?.get(0)?.name()
                } catch (e: Exception) {
                }

                binding?.startsAt.text = startAt


                if (viewModel.dataManager.isTripLive)
                    pusher()



                viewModel.dataManager.saveTripId(t.data()?.trip()?.id())
                viewModel.dataManager.saveLogId(t.data()?.trip()?.log_id())
                viewModel.dataManager.saveStartAt(startAt)


                try {
                    //   fragmentMapBinding?.stationStartAt.text = CommonUtilities.convertToTime(t.data()?.trip()?.stations()!![0].shouldBeThereAt()?.toLong()!!)

                } catch (e: java.lang.Exception) {

                }
                Glide.with(baseActivity).load(t.data()?.trip()?.partner()?.logo()).placeholder(
                    binding?.logo.drawable
                )
                    .into(binding?.logo)


                if (!t.data()?.trip()?.stations()?.isEmpty()!!) {

                    points = LatLngBounds.Builder()

                    for (model in t.data()?.trip()?.stations()!!) {

                        if (model.state().toString() == "PICKABLE") {
                            latLngsWayPoints?.add(

                                LatLng(
                                    model.latitude()?.toDouble()!!,
                                    model.longitude()?.toDouble()!!
                                )
                            )
                        } else if (model.state().toString() == "START") {
                            origin = LatLng(
                                model.latitude()?.toDouble()!!,
                                model.longitude()?.toDouble()!!
                            )
                        } else if (model.state().toString() == "END") {
                            destination = LatLng(
                                model.latitude()?.toDouble()!!,
                                model.longitude()?.toDouble()!!
                            )
                        }

                        latLngs?.add(

                            LatLng(
                                model.latitude()?.toDouble()!!,
                                model.longitude()?.toDouble()!!
                            )
                        )


                        if (model.state().toString() == "START") {
                            val markerOptions =
                                MarkerOptions().position(
                                    LatLng(
                                        model.latitude()?.toDouble()!!,
                                        model.longitude()?.toDouble()!!
                                    )
                                )
                                    .title("your_title").icon(
                                        bitmapDescriptorFromVector(
                                            baseActivity,
                                            R.drawable.marker_icon_3
                                        )
                                    )
                                    .snippet(model.id())
                            val addedMarker = mMap!!.addMarker(markerOptions)
                            val obj: String = model.name().toString()
                            addedMarker.tag = obj

                        } else if (model.state().toString() == "END") {
                            val markerOptions =
                                MarkerOptions().position(
                                    LatLng(
                                        model.latitude()?.toDouble()!!,
                                        model.longitude()?.toDouble()!!
                                    )
                                )
                                    .title("your_title").icon(
                                        bitmapDescriptorFromVector(
                                            baseActivity,
                                            R.drawable.marker_icon_2
                                        )
                                    )
                                    .snippet(model.id())
                            val addedMarker = mMap!!.addMarker(markerOptions)
                            val obj: String = model.name().toString()
                            addedMarker.tag = obj

                        } else {
                            val markerOptions =
                                MarkerOptions().position(
                                    LatLng(
                                        model.latitude()?.toDouble()!!,
                                        model.longitude()?.toDouble()!!
                                    )
                                )
                                    .title("your_title").icon(
                                        bitmapDescriptorFromVector(
                                            baseActivity,
                                            R.drawable.marker_icon
                                        )
                                    )
                                    .snippet(model.id())
                            val addedMarker = mMap!!.addMarker(markerOptions)
                            val obj: String = model.name().toString()
                            addedMarker.tag = obj

                        }



                        points?.include(
                            LatLng(
                                model.latitude()?.toDouble()!!,
                                model.longitude()?.toDouble()!!
                            )
                        )

                    }

                    if (points != null) {
                        val bounds = points?.build()
                        val cu: CameraUpdate = CameraUpdateFactory.newLatLngBounds(
                            bounds,
                            100
                        )
                        mMap?.animateCamera(cu, 200, null)
                    }


                    if (latLngsWayPoints == null) {
                        Toast.makeText(
                            baseActivity,
                            "latLngsWayPoints Error",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (origin == null) {
                        Toast.makeText(
                            baseActivity,
                            "origin Error",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (destination == null) {
                        Toast.makeText(
                            baseActivity,
                            "destination null",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        downloadUrl2(getDirectionsUrl(latLngsWayPoints!!, origin!!, destination!!))

                    }




                    binding.stations.layoutManager = LinearLayoutManager(baseActivity)
                    val stations =
                        ArrayList<Station>()
                    for (model in t.data()?.trip()?.stations()!!) {
                        stations.add(
                            Station(
                                model.id(),
                                model.name(),
                                model.latitude(),
                                model.longitude(),
                                "123456852",
                                0
                            )
                        )
                    }



                    adapter?.setTrips(stations)


                }

            } else {
                Logger.d(t.errors()[0].message())

            }

        })

        viewModel?.responseLiveStartTrip?.observe(viewLifecycleOwner, Observer
        { t ->


            if (!t.hasErrors()) {

                viewModel.dataManager.saveIsTripLive(true)
                viewModel.dataManager.saveStartAtTime(System.currentTimeMillis())
                viewModel.dataManager.saveTripId(t.data()?.startTrip()?.id())
                viewModel.dataManager.saveLogId(t.data()?.startTrip()?.log_id())

                viewModel.dataManager?.saveTripType("business")

                binding.startTripCardView.visibility = View.GONE
                binding.bottomSheet.visibility = View.VISIBLE




                Handler().post(Runnable {
                    mMap?.setPadding(
                        0,
                        0,
                        0,
                        150
                    )
                })

                if (points != null) {
                    val bounds = points?.build()
                    val cu: CameraUpdate = CameraUpdateFactory.newLatLngBounds(
                        bounds,
                        100
                    )
                    mMap?.animateCamera(cu, 200, null)
                }


                pusher()

            } else {
                Logger.d(t.errors()[0].message())
            }

        })

        viewModel?.responseLiveEndTrip?.observe(viewLifecycleOwner, Observer
        { t ->

            if (!t.hasErrors()) {

                viewModel.dataManager.saveStartAtTime(0)
                viewModel.dataManager.saveIsTripLive(false)
                viewModel.dataManager.saveTripId(null)
                viewModel.dataManager.saveLogId(null)
                viewModel.dataManager?.saveTripType("CAB")
                binding.startTripCardView.visibility = View.VISIBLE
                binding.bottomSheet.visibility = View.GONE

                Handler().post(Runnable {
                    mMap?.setPadding(
                        0,
                        binding.startTripCardView.getHeight(),
                        0,
                        0
                    )
                })

                if (points != null) {
                    val bounds = points?.build()
                    val cu: CameraUpdate = CameraUpdateFactory.newLatLngBounds(
                        bounds,
                        100
                    )
                    mMap?.animateCamera(cu, 200, null)
                }


                val intent = Intent(baseActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                baseActivity.finish()


            } else {
                Logger.d(t.errors()[0].message())
            }

        })


        viewModel?.progress?.observe(viewLifecycleOwner, Observer
        {

            when (it) {
                0 -> {
                    try {
                        CommonUtilities.hideDialog()

                    } catch (e: Exception) {

                    }
                }
                1 -> {
                    CommonUtilities.showStaticDialog(baseActivity)
                }
            }
        })

    }

    fun onMarkerClick(marker: Marker): Boolean {
        val obj: String? = marker.tag as String?
        Logger.d(marker.snippet + " NAMEEEEE " + obj)

        if (viewModel.dataManager.isTripLive) {
            open_station_dialog(marker.snippet, obj, null)
        } else {
            Toast.makeText(baseActivity, "Trip Not Started Yet", Toast.LENGTH_SHORT).show()
        }

        return true
    }

    fun open_station_dialog(stationID: String?, stationName: String?, tripId: String?) {
        val cdd = StationDialog(
            baseActivity,
            stationID,
            stationName,
            tripId,
            binding.name.text.toString(),
            LatLng(mLastLocation?.latitude!!, mLastLocation?.longitude!!),
            this
        )
        cdd.setCanceledOnTouchOutside(true)
        cdd.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        cdd.getWindow()?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        cdd.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun open_chat_dialog() {
        val cdd = ChatDialog(baseActivity)
        cdd.setCanceledOnTouchOutside(true)
        cdd.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val window = cdd.getWindow();
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        );

        cdd.getWindow()?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        cdd.show()
    }

    fun open_attandance_dialog() {
        val cdd = AttandanceDialog(
            baseActivity,
            binding.name.text.toString(),
            viewModel.dataManager.tripId,
            LatLng(mLastLocation?.latitude!!, mLastLocation?.longitude!!)
        )
        cdd.setCanceledOnTouchOutside(true)
        cdd.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        cdd.getWindow()?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        cdd.show()
    }

    fun pusher() {
        val httpAuthorizer = HttpAuthorizer("https://qruz.xyz/broadcasting/auth")


        val hashMap: HashMap<String, String> = HashMap<String, String>()
        hashMap.put("Accept", "application/json")
        val sb = StringBuilder()
        sb.append("Bearer ")
        sb.append(viewModel.dataManager.getAccessToken())
        hashMap.put("Authorization", sb.toString())
        httpAuthorizer.setHeaders(hashMap)
        val wssPort =
            PusherOptions().setCluster("eu").setAuthorizer(httpAuthorizer).setHost("qruz.xyz")
                .setWsPort(6002).setWssPort(6002)
        wssPort.isEncrypted = true
        val pusher = Pusher("48477c9c2419bd65e74f", wssPort)


        val sb2 = StringBuilder()
        sb2.append("private-App.BusinessTrip.")
        sb2.append(viewModel.dataManager.getLogId())

        this.ch = pusher.subscribePrivate(sb2.toString())
        this.ch!!.bind("client-driver.location", object : PrivateChannelEventListener {
            override fun onSubscriptionSucceeded(str: String) {
            }

            override fun onAuthenticationFailure(str: String, exc: java.lang.Exception) {
            }

            override fun onEvent(pusherEvent: PusherEvent) {

            }
        })
        pusher.connect()
    }


    @Throws(IOException::class)
    private fun downloadUrl2(strUrl: String): String? {
        var data = ""
        Completable.fromAction {
            var iStream: InputStream? = null
            var urlConnection: HttpURLConnection? = null
            try {
                val url = URL(strUrl)

                // Creating an http connection to communicate with url
                urlConnection = url.openConnection() as HttpURLConnection

                // Connecting to url
                urlConnection.connect()

                // Reading data from url
                iStream = urlConnection.inputStream
                val br = BufferedReader(InputStreamReader(iStream))
                val sb = StringBuffer()
                var line: String? = ""
                while (br.readLine().also { line = it } != null) {
                    sb.append(line)
                }
                data = sb.toString()
                Log.d("  downloadUrlaa  ", data)
                br.close()
            } catch (e: java.lang.Exception) {
                Log.d("  Exceptionaa  ", e.toString())
            } finally {
                iStream!!.close()
                urlConnection!!.disconnect()
            }


            val jObject: JSONObject
            var routes: List<List<HashMap<String, String>>>? = null
            try {
                jObject = JSONObject(data)

                parseJson(jObject)

                Log.d("dataParserTask", data)
                val parser = DataParser()
                Log.d("parserParserTask", parser.toString())

                // Starts parsing data
                routes = parser.parse(jObject)
                Log.d("ParserTask", "Executing routes")
                Log.d("routesParserTask", routes.toString())
            } catch (e: java.lang.Exception) {
                Log.d("ParserTask", e.toString())
                e.printStackTrace()
                throw RuntimeException(e.message)
            }


            var points: ArrayList<LatLng?>? = null
            var lineOptions: PolylineOptions? = null
            if (routes != null) {
                // Traversing through all the routes
                for (i in routes.indices) {
                    points = ArrayList()
                    lineOptions = PolylineOptions()

                    // Fetching i-th route
                    val path = routes[i]

                    // Fetching all the points in i-th route
                    for (j in path.indices) {
                        val point = path[j]
                        val lat = point["lat"]!!.toDouble()
                        val lng = point["lng"]!!.toDouble()
                        val position = LatLng(lat, lng)
                        points.add(position)
                    }


                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points)
                    lineOptions.width(10f)
                    lineOptions.color(
                        Color.parseColor(
                            baseActivity.getResources().getString(0 + R.color.colorPrimaryLight)
                        )
                    )
                    Log.d("onPostExecute", "onPostExecute lineoptions decoded")
                }
            }
            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null && points != null) {

                baseActivity.runOnUiThread { mMap?.addPolyline(lineOptions) }

            } else {
                Log.d("onPostExecute", "without Polylines drawn")
            }


        }
            .subscribeOn(Schedulers.io())
            .subscribe()





        return data
    }

    private fun getLineUrl(points: ArrayList<LatLng>): String {


        if (points.size > 2) {
            val str_origin =
                "origin=" + points[0].latitude.toString() + "," + points[0].longitude.toString()

            // Destination of route
            val str_dest =
                "destination=" + points.get(points.size - 1).latitude.toString() + "," + points.get(
                    points.size - 1
                ).longitude.toString()

            val waypoints = StringBuffer()
            var flag = 0
            for (model in points) {
                if (flag == 0)
                    waypoints.append("waypoints=" + model.latitude.toString() + "," + model.longitude.toString())
                else
                    waypoints.append("%7C" + model.latitude.toString() + "," + model.longitude.toString())

                flag++
            }


            Log.d(" url = ", waypoints.toString())


            // Sensor enabled
            val sensor = "sensor=false"

            // Building the parameters to the web service
            val parameters = "$str_origin&$str_dest&$sensor&$waypoints"

            // Output format
            val output = "json"

            // Building the url to the web service
            val url =
                "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + context!!.resources.getString(
                    R.string.google_map_api
                )



            return url

        } else if (points.size == 2) {

            val str_origin =
                "origin=" + points[0].latitude.toString() + "," + points[0].longitude.toString()

            // Destination of route
            val str_dest =
                "destination=" + points.get(points.size - 1).latitude.toString() + "," + points.get(
                    points.size - 1
                ).longitude.toString()


            // Sensor enabled
            val sensor = "sensor=false"

            // Building the parameters to the web service
            val parameters = "$str_origin&$str_dest&$sensor"

            // Output format
            val output = "json"

            //Building the url to the web service
            val url =
                "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + context!!.resources.getString(
                    R.string.google_map_api
                )



            return url


        } else {
            return ""
        }


    }


    private fun getDirectionsUrl(
        points: ArrayList<LatLng>,
        origin: LatLng,
        destination: LatLng
    ): String {

        val str_origin =
            "origin=" + origin.latitude.toString() + "," + origin.longitude.toString()

        // Destination of route
        val str_dest =
            "destination=" + destination.latitude.toString() + "," + destination.longitude.toString()


        // Waypoints
        var waypoints = ""
        for (i in 0 until points.size) {
            val point = points.get(i) as LatLng
            if (i == 0) waypoints = "waypoints=optimize:true|"
            waypoints += if (i == points.size - 1) {
                point.latitude.toString() + "," + point.longitude
            } else {
                point.latitude.toString() + "," + point.longitude + "|"
            }
        }

        // Building the parameters to the web service
        val parameters =
            "$str_origin&$str_dest&$waypoints&mode=driving&key=" + context!!.resources.getString(R.string.google_map_api)


        // Output format
        val output = "json"

        Logger.d(" LINK " + "https://maps.googleapis.com/maps/api/directions/$output?$parameters")

        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters"
    }


    private fun getNavigationUrl(
        points: ArrayList<LatLng>,
        origin: LatLng,
        destination: LatLng
    ): String {


        val str_origin =
            "origin=" + origin.latitude.toString() + "," + origin.longitude.toString()

        // Destination of route
        val str_dest =
            "destination=" + destination.latitude.toString() + "," + destination.longitude.toString()


        // Waypoints
        var waypoints = ""
        for (i in 0 until points.size) {
            val point = points.get(i) as LatLng
            if (i == 0) waypoints = "waypoints=optimize:true|"
            waypoints += if (i == points.size - 1) {
                point.latitude.toString() + "," + point.longitude
            } else {
                point.latitude.toString() + "," + point.longitude + "|"
            }
        }

        // Building the parameters to the web service
        val parameters =
            "$str_origin&$str_dest&$waypoints&key=" + context!!.resources.getString(R.string.google_map_api)


        // Output format
        val output = "json"

        //Building the url to the web service
        val url = "https://www.google.com/maps/dir/?api=1&" + parameters + "&travelmode=navigate"


        Logger.d(url)
        return url


    }

    override fun setOnStationClick(station: Station) {
        val cdd = StationDialog(
            baseActivity,
            station.id,
            station.name,
            null,
            binding.name.text.toString(),
            LatLng(mLastLocation?.latitude!!, mLastLocation?.longitude!!),
            this
        )
        com.orhanobut.logger.Logger.d(station.id)
        cdd.setCanceledOnTouchOutside(true)
        cdd.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        cdd.getWindow()?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        cdd.show()
    }

    override fun openPhonesDialog(stationUser: StationUser) {
        val cdd = PhonesDialog(
            baseActivity,
            stationUser
        )
        cdd.setCanceledOnTouchOutside(true)
        cdd.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        cdd.getWindow()?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        cdd.show()
    }

    override fun openUserChatDialog(stationUser: StationUser) {
        val cdd = DirectChatDialog(
            baseActivity,
            stationUser
        )
        cdd.setCanceledOnTouchOutside(true)
        cdd.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        cdd.getWindow()?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        cdd.show()
    }


/*
    @Throws(JSONException::class)
    private fun test(jsonString: String) {
        val jSONObject = JSONObject(jsonString)
        val `object` = JSONTokener(jsonString).nextValue()  as JSONObject
        val legsJson = `object`.getString("type")
        val featuresArr = jSONObject.getJSONArray("legs")

        var totalDistance: Long = 0
        var totalSeconds = 0

        for (i in 0 until featuresArr.length()) {

            totalSeconds = totalSeconds + Integer.parseInt(featuresArr[i].getJSONArray("duration").getString("value"));



            val anotherjsonObject = featuresArr.getJSONObject(i)
            //access the fields of that json object
            val str_type_one = anotherjsonObject.getString("type")
            val featuresArr_properties = anotherjsonObject.getJSONArray("properties")
            val propertiesjsonObject = featuresArr_properties.getJSONObject(0)
            val test = propertiesjsonObject.getString("type")
        }
    }*/

    fun parseJson(jObject: JSONObject) {
        val routes: List<List<HashMap<String, String>>> = ArrayList()
        val jRoutes: JSONArray
        var jLegs: JSONArray
        var jSteps: JSONArray
        var jDistance: JSONObject? = null
        var jDuration: JSONObject? = null
        var totalDistance: Long = 0
        var totalSeconds = 0
        try {
            jRoutes = jObject.getJSONArray("routes")

            /* Traversing all routes */for (i in 0 until jRoutes.length()) {
                jLegs = (jRoutes.get(i) as JSONObject).getJSONArray("legs")

                /* Traversing all legs */for (j in 0 until jLegs.length()) {
                    jDistance = (jLegs.get(j) as JSONObject).getJSONObject("distance")
                    totalDistance = totalDistance + jDistance.getString("value").toLong()
                    /** Getting duration from the json data  */
                    jDuration = (jLegs.get(j) as JSONObject).getJSONObject("duration")
                    totalSeconds = totalSeconds + jDuration.getString("value").toInt()
                }
            }
            val dist = totalDistance / 1000.0
            Log.d("distance", "Calculated distance:$dist")
            val days = totalSeconds / 86400
            val hours = (totalSeconds - days * 86400) / 3600
            val minutes = (totalSeconds / 60).toInt()
            val seconds = totalSeconds - days * 86400 - hours * 3600 - minutes * 60
            Log.d(
                " duration ",
                "$minutes"
            )
            startAtTime = minutes

            baseActivity.runOnUiThread {
                binding.totalDistance.text = dist.toString() + " " + getString(R.string.km)
                binding.totalDuration.text = minutes.toString() + " " + getString(R.string.munites)
            }


        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


    private fun sendNotification(title: String, messageBody: String) {
        val intent = Intent(baseActivity, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            baseActivity, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(baseActivity, channelId)
            .setSmallIcon(R.drawable.pp)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            baseActivity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(
            System.currentTimeMillis().toInt() /* ID of notification */,
            notificationBuilder.build()
        )
    }

}
