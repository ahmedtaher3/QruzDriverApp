package qruz.t.qruzdriverapp.ui.dialogs.startion

import qruz.t.qruzdriverapp.model.StationUser

interface StationInterface {

    fun openPhonesDialog(stationUser: StationUser)
    fun openUserChatDialog(stationUser: StationUser)
}