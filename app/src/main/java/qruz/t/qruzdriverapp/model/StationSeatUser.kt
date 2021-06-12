package qruz.t.qruzdriverapp.model

import android.os.Parcelable
import androidx.annotation.Nullable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StationSeatUser(
    @Nullable
    var id: String?="",
    @Nullable
    var name: String?="",
    @Nullable
    var phone: String? = "",
    @Nullable
    var payable: Double,
    @Nullable
    var wallet_balance: Double,
    @Nullable
    var booking_id: String?="",
    @Nullable
    var seats: Int,
    @Nullable
    var boarding_pass: Int,
    @Nullable
    var paid: Double
) : Parcelable