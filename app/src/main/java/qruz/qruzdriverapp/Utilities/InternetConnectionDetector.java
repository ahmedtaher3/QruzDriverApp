package qruz.qruzdriverapp.Utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;


  /*  CLASS DESCRIPTION : THIS CLASS WILL CHECK WHETHER THE USER DEVICE IS CONNECTED TO THE INTERNET CONNECTION OR NOT. */

public class InternetConnectionDetector {

     public static boolean IsInternetAvailable(Context activityContext) {


        //GETTING CONNECTION SERVICE FROM OS THROUGH CONNECTIVITY MANAGER
        ConnectivityManager connectivityManager = (ConnectivityManager) activityContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        // CHECKING CONNECTION OBJECT IS NULL OR NOT
        if (connectivityManager != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                // GETTING ALL THE NETWORKS
                Network[] networks = connectivityManager.getAllNetworks();
                NetworkInfo networkInfo;

                // CHECKING IF ANY CONNECTION INFO FOUND OR NOT
                for (Network mNetwork : networks) {

                    // GET CURRENT NETWORK CONNECTION INFO
                    networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                    if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                        return true;
                    }

                }

            } else {

                // GET ALL NETWORK CONNECTION INFO
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();

                // CHECKING IF ANY CONNECTION INFO FOUND OR NOT
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            return true; // THE DEVICE IS CONNECTED TO INTERNET
                        }
                    }
                }

            }

        }

        return false;

    }
}