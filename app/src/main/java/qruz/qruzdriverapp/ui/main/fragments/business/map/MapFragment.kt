package qruz.qruzdriverapp.ui.main.fragments.business.map


import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.google.firebase.crashlytics.internal.common.AbstractSpiCall
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox.getApplicationContext
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.maps.Style.OnStyleLoaded
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import com.orhanobut.logger.Logger
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.PrivateChannel
import com.pusher.client.channel.PrivateChannelEventListener
import com.pusher.client.channel.PusherEvent
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import com.pusher.client.util.HttpAuthorizer
import qruz.qruzdriverapp.R
import qruz.qruzdriverapp.Utilities.CommonUtilities
import qruz.qruzdriverapp.Utilities.GPS_Service
import qruz.qruzdriverapp.base.BaseFragment
import qruz.qruzdriverapp.databinding.FragmentMapBinding
import qruz.qruzdriverapp.model.Station
import qruz.qruzdriverapp.ui.dialogs.chat.ChatDialog
import qruz.qruzdriverapp.ui.dialogs.startion.StationDialog
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


private const val ARG_START_AT = "startAt"
private const val ARG_TRIP_ID = "tripId"
private const val ARG_STATUS = "status"


var permissionsManager: PermissionsManager? = null

/**
 * A simple [Fragment] subclass.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


class MapFragment : BaseFragment<FragmentMapBinding>(), View.OnClickListener,
    PermissionsListener, MapboxMap.OnMarkerClickListener {

    var startAt = ""
    var tripId = "0"
    var status = false

    lateinit var binding: FragmentMapBinding
    private lateinit var viewModel: MapViewModel

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private val INTERVAL: Long = 7000
    private val FASTEST_INTERVAL: Long = 1000
    private var mLastLocation: Location? = null
    internal lateinit var mLocationRequest: LocationRequest
    private val REQUEST_PERMISSION_LOCATION = 10
    private var ch: PrivateChannel? = null
    val bounds = LatLngBounds.Builder()
    var route: DirectionsRoute? = null

    private var map_boxMap: MapboxMap? = null
    private var navigationMapRoute: NavigationMapRoute? = null
    private var stationsPoints: ArrayList<Point>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            status = it.getBoolean(ARG_STATUS)
            startAt = it.getString(ARG_START_AT).toString()
            tripId = it.getString(ARG_TRIP_ID).toString()


        }
        viewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)
        stationsPoints = ArrayList()
        mLocationRequest = LocationRequest()

        val locationManager =
            baseActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        }


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
        fun newInstance(status: Boolean, tripId: String, startAt: String) =
            MapFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_START_AT, startAt)
                    putString(ARG_TRIP_ID, tripId)
                    putBoolean(ARG_STATUS, status)
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
        }

        // You can now create a LatLng Object for use with maps
    }

    private fun stoplocationUpdates() {
        mFusedLocationProviderClient!!.removeLocationUpdates(mLocationCallback)
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = viewDataBinding



        if (viewModel.dataManager.isTripLive) {

            binding.startTripCardView.visibility = View.GONE
            binding.bottomSheet.visibility = View.VISIBLE

            viewModel.tripLiveTrip(viewModel.dataManager.tripId)

        } else {

            binding.startTripCardView.visibility = View.VISIBLE
            binding.bottomSheet.visibility = View.GONE

            viewModel.tripLiveTrip(tripId.toString().toString())
        }


        setLiseners()
        binding.mapView?.onCreate(savedInstanceState)
        binding.mapView?.getMapAsync { mapboxMap ->

            map_boxMap = mapboxMap
            map_boxMap!!.setPadding(100, 100, 100, 100)

            map_boxMap!!.getUiSettings().setZoomGesturesEnabled(true)
            map_boxMap!!.getUiSettings().setScrollGesturesEnabled(true)
            map_boxMap!!.getUiSettings().setAllGesturesEnabled(true)
            map_boxMap!!.getUiSettings().setRotateGesturesEnabled(true)
            map_boxMap!!.getUiSettings().getFocalPoint()
            map_boxMap!!.getUiSettings().setRotateVelocityAnimationEnabled(true)
            map_boxMap!!.getUiSettings().setTiltGesturesEnabled(true)
            map_boxMap!!.getUiSettings().setScaleVelocityAnimationEnabled(true)
            map_boxMap!!.getUiSettings().setCompassEnabled(false)
            map_boxMap!!.getUiSettings().setDeselectMarkersOnTap(true)
            map_boxMap!!.setOnMarkerClickListener(this)

            mapboxMap.setStyle(Style.MAPBOX_STREETS, Style.OnStyleLoaded() { t ->
                enableLocationComponent(t);
            });

            subscribeObservers()


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
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.startTrip -> {

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

                if (status) {


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


                } else {
                    Toast.makeText(baseActivity, "cant start trip now", Toast.LENGTH_SHORT).show()

                }


            }

            R.id.endTrip -> {

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

            R.id.my_location -> {


                if (mLastLocation != null) {
                    val position = CameraPosition.Builder()
                        .target(
                            LatLng(
                                mLastLocation!!.latitude,
                                mLastLocation!!.longitude
                            )
                        ) // Sets the new camera position
                        .zoom(17.0) // Sets the zoom
                        .build(); // Creates a CameraPosition from the builder


                    if (map_boxMap != null) {
                        map_boxMap!!.animateCamera(
                            CameraUpdateFactory
                                .newCameraPosition(position), 7000
                        );
                    }
                }


            }

            R.id.zoom_route -> {


                try {   // zero padding
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

                }


            }


            R.id.navigation -> {

                if (route != null) {
                    val options = NavigationLauncherOptions.builder()
                        .directionsRoute(route)
                        .build()

                    NavigationLauncher.startNavigation(baseActivity, options)
                }


            }

            R.id.openChat -> {


                open_chat_dialog()


            }


            R.id.passengersButton -> {


                open_station_dialog(null, viewModel.dataManager.tripId)


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
        binding.mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView?.onResume()
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView?.onStop()
        stoplocationUpdates()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView?.onDestroy()
        stoplocationUpdates()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView?.onSaveInstanceState(outState);
    }

    private fun enableLocationComponent(@NonNull loadedMapStyle: Style) {
        if (PermissionsManager.areLocationPermissionsGranted(activity)) {
            val locationComponent: LocationComponent = map_boxMap?.getLocationComponent()!!
            locationComponent.activateLocationComponent(
                LocationComponentActivationOptions.builder(activity!!, loadedMapStyle).build()
            )
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
            locationComponent.isLocationComponentEnabled = true
            // Set the LocationComponent's camera mode
            locationComponent.cameraMode = CameraMode.TRACKING
            // Set the LocationComponent's render mode
            locationComponent.renderMode = RenderMode.NORMAL
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager!!.requestLocationPermissions(activity)
        }
    }


    override fun onExplanationNeeded(permissionsToExplain: List<String?>?) {
        Toast.makeText(activity, "location not enabled", Toast.LENGTH_LONG).show()
    }

    override fun onPermissionResult(granted: Boolean) {

        if (granted) {
            map_boxMap?.getStyle(OnStyleLoaded { style -> enableLocationComponent(style) })
        } else {
            Toast.makeText(activity, "Location services not allowed", Toast.LENGTH_LONG).show()
            activity!!.finish()
        }
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

                binding?.name.text = t.data()?.trip()?.name()

                try {
                } catch (e: Exception) {
                }

                try {
                    binding?.startStation.text = t.data()?.trip()?.stations()?.get(0)?.name()
                } catch (e: Exception) {
                }

                binding?.startsAt.text = startAt

                try {

                    var count = 0
                    for (model in t.data()?.trip()?.stations()!!) {
                        count += model.users()?.size!!
                    }

                    binding?.userCount.text = count.toString()
                } catch (e: Exception) {
                }


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

                    for (model in t.data()?.trip()?.stations()!!) {

                        Logger.d(model)

                        map_boxMap?.addMarker(
                            MarkerOptions()
                                .position(
                                    LatLng(
                                        model.latitude()?.toDouble()!!,
                                        model.longitude()?.toDouble()!!
                                    )
                                )
                                .setIcon(
                                    CommonUtilities.drawableToIcon(
                                        baseActivity,
                                        R.drawable.ic_station_icon
                                    )
                                )
                                .setSnippet(model.id())
                                .title(model.name())
                        )


                        bounds.include(
                            LatLng(
                                model.latitude()?.toDouble()!!,
                                model.longitude()?.toDouble()!!
                            )
                        )

                    }
                    // From Mapbox to The White House
                    val origin = Point.fromLngLat(
                        t.data()?.trip()?.stations()!![0].longitude()?.toDouble()!!,
                        t.data()?.trip()?.stations()!![0].latitude()?.toDouble()!!
                    )
                    val destination = Point.fromLngLat(
                        t.data()?.trip()?.stations()!!.last().longitude()?.toDouble()!!,
                        t.data()?.trip()?.stations()!!.last().latitude()?.toDouble()!!
                    )


                    val cameraPosition2 =
                        map_boxMap?.getCameraForLatLngBounds(
                            bounds.build(),
                            intArrayOf(100, 100, 100, 100)
                        )
                    map_boxMap?.easeCamera(
                        CameraUpdateFactory.newCameraPosition(cameraPosition2!!),
                        5000
                    )


                    val navigation = NavigationRoute.builder(baseActivity)
                        .accessToken(getString(R.string.mapbox_access_token))
                        .origin(origin)
                        .destination(destination)
                        .profile(DirectionsCriteria.PROFILE_DRIVING)



                    for (model in t.data()?.trip()?.stations()!!.subList(
                        1,
                        t.data()?.trip()?.stations()!!.size - 1
                    )) {

                        Logger.d(model.longitude()?.toDouble()!!.toString())
                        Logger.d(t.data()?.trip()?.stations()!!.size)

                        navigation.addWaypoint(
                            Point.fromLngLat(
                                model.longitude()?.toDouble()!!,
                                model.latitude()?.toDouble()!!
                            )
                        )
                    }

                    navigation.build()
                        .getRoute(object : retrofit2.Callback<DirectionsResponse?> {
                            override fun onResponse(
                                call: retrofit2.Call<DirectionsResponse?>,
                                response: retrofit2.Response<DirectionsResponse?>
                            ) {


                                if (response.body() == null) {
                                    Logger.d("null")
                                    return
                                } else if (response.body()!!.routes().size == 0) {
                                    Logger.d("0")
                                    return
                                }

                                Logger.d("done")

                                val directionsRoute = response.body()!!.routes()[0]
                                if (navigationMapRoute != null) {
                                    navigationMapRoute?.removeRoute()

                                    Logger.d("removeRoute")
                                } else {
                                    navigationMapRoute = NavigationMapRoute(
                                        null, binding.mapView, map_boxMap!!
                                    )

                                    Logger.d("new navigationMapRoute")
                                }
                                route = directionsRoute
                                navigationMapRoute!!.addRoute(directionsRoute)

                            }

                            override fun onFailure(
                                call: retrofit2.Call<DirectionsResponse?>,
                                t: Throwable
                            ) {
                                Logger.d("onFailure")

                            }
                        })


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
                                model.users()!!.size
                            )
                        )
                    }

                    binding.stations.adapter = StationsAdapter(stations, baseActivity)


                }

            } else {
                Logger.d(t.errors()[0].message())

            }

        })

        viewModel?.responseLiveStartTrip?.observe(viewLifecycleOwner, Observer { t ->


            if (!t.hasErrors()) {

                viewModel.dataManager.saveIsTripLive(true)
                viewModel.dataManager.saveTripId(t.data()?.startTrip()?.id())
                viewModel.dataManager.saveLogId(t.data()?.startTrip()?.log_id())

                viewModel.dataManager?.saveTripType("business")

                binding.startTripCardView.visibility = View.GONE
                binding.bottomSheet.visibility = View.VISIBLE
                pusher()

            } else {
                Logger.d(t.errors()[0].message())
            }

        })

        viewModel?.responseLiveEndTrip?.observe(viewLifecycleOwner, Observer { t ->

            if (!t.hasErrors()) {

                viewModel.dataManager.saveIsTripLive(false)
                viewModel.dataManager.saveTripId(null)
                viewModel.dataManager.saveLogId(null)
                viewModel.dataManager?.saveTripType("CAB")

                binding.startTripCardView.visibility = View.VISIBLE
                binding.bottomSheet.visibility = View.GONE

                baseActivity.onBackPressed()

            } else {
                Logger.d(t.errors()[0].message())
            }

        })


        viewModel?.progress?.observe(viewLifecycleOwner, Observer {

            when (it) {
                0 -> {
                    CommonUtilities.hideDialog()
                }
                1 -> {
                    CommonUtilities.showStaticDialog(baseActivity)
                }
            }
        })

    }

    override fun onMarkerClick(marker: Marker): Boolean {
        Logger.d(marker.snippet)

        if (viewModel.dataManager.isTripLive) {
            open_station_dialog(marker.snippet, null)
        } else {
            Toast.makeText(baseActivity, "Trip Not Started Yet", Toast.LENGTH_SHORT).show()
        }

        return true
    }

    fun open_station_dialog(stationID: String?, tripId: String?) {
        val cdd = StationDialog(baseActivity, stationID, tripId)
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

        cdd.getWindow()?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        cdd.show()
    }

    fun pusher() {
        val httpAuthorizer = HttpAuthorizer("https://qruz.xyz/broadcasting/auth")


        val hashMap: HashMap<String, String> = HashMap<String, String>()
        hashMap.put(AbstractSpiCall.HEADER_ACCEPT, "application/json")
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
        pusher.connect(object : ConnectionEventListener {
            override fun onConnectionStateChange(connectionStateChange: ConnectionStateChange) {
                val printStream = System.out
                val sb = StringBuilder()
                sb.append("State changed to ")
                sb.append(connectionStateChange.currentState)
                sb.append(" from ")
                sb.append(connectionStateChange.previousState)
                printStream.println(sb.toString())
            }

            override fun onError(str: String, str2: String, exc: java.lang.Exception) {
                println("There was a problem connecting!")
            }
        }, ConnectionState.ALL)
        val sb2 = StringBuilder()
        sb2.append("private-App.BusinessTrip.")
        sb2.append(viewModel.dataManager.getLogId())

        this.ch = pusher.subscribePrivate(sb2.toString())
        this.ch!!.bind("client-driver.location", object : PrivateChannelEventListener {
            override fun onSubscriptionSucceeded(str: String) {
                val str2 = "onSubscriptionSucceeded"
                Log.e(str2, str2)
            }

            override fun onAuthenticationFailure(str: String, exc: java.lang.Exception) {
                Log.e("failed 1", str)
            }

            override fun onEvent(pusherEvent: PusherEvent) {

                System.out.println("pusherEvent" + pusherEvent)
            }
        })
        pusher.connect()
    }


}
