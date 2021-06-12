package qruz.t.qruzdriverapp.ui.main.fragments.business.seats


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.logger.Logger
import com.qruz.SingleSeatsTripTransactionsQuery
import qruz.t.qruzdriverapp.R
import qruz.t.qruzdriverapp.databinding.StationSeatUserPickItemBinding
import qruz.t.qruzdriverapp.databinding.TransactionItemBinding
import qruz.t.qruzdriverapp.model.DriverTrips
import qruz.t.qruzdriverapp.model.StationSeatUser
import qruz.t.qruzdriverapp.model.StationUser
import java.util.*


class TransactionsAdapter(
    private var users: ArrayList<SingleSeatsTripTransactionsQuery.SingleSeatsTripTransaction>,
    private var context: Activity
) :
    RecyclerView.Adapter<TransactionsAdapter.StationViewHolder>() {


    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): TransactionsAdapter.StationViewHolder {


        return StationViewHolder(
            TransactionItemBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup, false
            )
        )
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(hoder: StationViewHolder, i: Int) {

        val model = users[i]

        hoder.mBinding?.name?.text = model.user()?.name()

        hoder.mBinding?.confirm?.text ="دفع : " +  model.paid().toString()
    }

    override fun getItemCount(): Int {
        return users.size
    }

    fun setUsers(users: ArrayList<SingleSeatsTripTransactionsQuery.SingleSeatsTripTransaction>) {
        this.users = users
        context.runOnUiThread(Runnable {
            notifyDataSetChanged()
        })


        Logger.d(users.size)
    }


    fun getUsers(): ArrayList<SingleSeatsTripTransactionsQuery.SingleSeatsTripTransaction> {
        return this.users

    }


    inner class StationViewHolder(var binding: TransactionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {


        var mBinding: TransactionItemBinding? = null

        init {
            this.mBinding = binding;


        }


    }


}