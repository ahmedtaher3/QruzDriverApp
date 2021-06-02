package qruz.t.qruzdriverapp.model

class StationSeatUser(
    var id: String,
    var name: String,
    var phone: String,
    var payable: Double,
    var wallet_balance: Double,
    var booking_id: String,
    var seats: Int,
    var boarding_pass: Int,
    var paid: Double
) 