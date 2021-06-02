package qruz.t.qruzdriverapp.ui.dialogs.startion

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.orhanobut.logger.Logger
import com.qruz.DropSeatsTripUserMutation
import com.qruz.data.remote.ApolloClientUtils.setupApollo
import qruz.t.qruzdriverapp.R
import qruz.t.qruzdriverapp.Utilities.CommonUtilities
import qruz.t.qruzdriverapp.base.BaseApplication
import qruz.t.qruzdriverapp.data.local.DataManager
import qruz.t.qruzdriverapp.model.StationSeatUser

class SeatsUserDropDialog(
    /* access modifiers changed from: private */
    var c: Activity?=null,
    var model: StationSeatUser?=null,
    var trip_time: String?=""
) : Dialog(c!!) {
    var STATUS_INT = 1

    var d: Dialog? = null
    var dataManager: DataManager? = null
    var paid: Double? = null
    var confirm: Button? = null
    var other: Button? = null
    var cancel: Button? = null
    var name: TextView? = null
    var bookingID: TextView? = null
    var seatsCount: TextView? = null
    var tripCost: TextView? = null
    var preCost: TextView? = null
    var totalCost: TextView? = null
    public override fun onCreate(bundle: Bundle) {
        super.onCreate(bundle)
        setContentView(R.layout.seat_user_drop_dialog)
        dataManager = (c?.application as BaseApplication).dataManager
        confirm = findViewById<View>(R.id.confirm) as Button
        other = findViewById<View>(R.id.other) as Button
        cancel = findViewById<View>(R.id.cancel) as Button
        name = findViewById<View>(R.id.name) as TextView
        bookingID = findViewById<View>(R.id.bookingID) as TextView
        seatsCount = findViewById<View>(R.id.seatsCount) as TextView
        tripCost = findViewById<View>(R.id.tripCost) as TextView
        preCost = findViewById<View>(R.id.preCost) as TextView
        totalCost = findViewById<View>(R.id.totalCost) as TextView
        name!!.text = model?.name
        bookingID!!.text = model?.booking_id
        seatsCount!!.text = model?.seats.toString()
        tripCost!!.text = model?.payable.toString()
        preCost!!.text = model?.wallet_balance.toString()
        totalCost!!.text = (model?.payable!! - model?.wallet_balance!!).toString()
        paid = model?.payable!! - model?.paid!!
        confirm!!.text = "تاكبد دفع : " + paid.toString()
        confirm!!.setOnClickListener {
            CommonUtilities.showStaticDialog(c)

            //progressBar.setVisibility(View.VISIBLE);
            setupApollo(dataManager?.getAccessToken())
                ?.mutate(
                    DropSeatsTripUserMutation.builder()
                        .user_id(model?.id!!)
                        .payable(model?.payable!!)
                        .paid(paid.toString().toFloat().toDouble())
                        .booking_id(model?.booking_id!!)
                        .log_id(dataManager?.getLogId()!!)
                        .trip_id(dataManager?.getTripId()!!)
                        .trip_time(trip_time!!)
                        .build()
                )?.enqueue(object : ApolloCall.Callback<DropSeatsTripUserMutation.Data?>() {
                    override fun onResponse(response: Response<DropSeatsTripUserMutation.Data?>) {
                        CommonUtilities.hideDialog()
                        if (!response.hasErrors()) {
                            Logger.d(response.data())
                        } else {
                            Logger.d(response.errors())
                        }
                        dismiss()
                    }

                    override fun onFailure(apolloException: ApolloException) {
                        Logger.d(apolloException.message)
                        CommonUtilities.hideDialog()
                    }
                })
        }
        other!!.setOnClickListener {
            val dialogBuilder =
                AlertDialog.Builder(c)
            val dialogView =
                c?.layoutInflater?.inflate(R.layout.seat_user_drop_other_dialog, null)
            dialogBuilder.setView(dialogView)
            val confirm =
                dialogView?.findViewById<Button>(R.id.confirm)
            val paidEditText: AppCompatEditText = dialogView?.findViewById(R.id.paid)!!
            confirm?.setOnClickListener {
                CommonUtilities.showStaticDialog(c)
                //progressBar.setVisibility(View.VISIBLE);
                setupApollo(dataManager?.getAccessToken())
                    ?.mutate(
                        DropSeatsTripUserMutation.builder()
                            .user_id(model?.id!!)
                            .payable(model?.payable!!  )
                            .paid(paid.toString().toFloat().toDouble())
                            .booking_id(model?.booking_id!!)
                            .log_id(dataManager?.getLogId()!!)
                            .trip_id(dataManager?.getTripId()!!)
                            .trip_time(trip_time!!)
                            .build()
                    )?.enqueue(object :
                        ApolloCall.Callback<DropSeatsTripUserMutation.Data?>() {
                        override fun onResponse(response: Response<DropSeatsTripUserMutation.Data?>) {
                            CommonUtilities.hideDialog()
                            if (!response.hasErrors()) {
                                Logger.d(response.data())
                            } else {
                                Logger.d(response.errors())
                            }
                            dismiss()
                        }

                        override fun onFailure(apolloException: ApolloException) {
                            Logger.d(apolloException.message)
                            CommonUtilities.hideDialog()
                        }
                    })
            }
            val alertDialog = dialogBuilder.create()
            alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            alertDialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            alertDialog.show()
        }
        cancel!!.setOnClickListener { dismiss() }
    }

}