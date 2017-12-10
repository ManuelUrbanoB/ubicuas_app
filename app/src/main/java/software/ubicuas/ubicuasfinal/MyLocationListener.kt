package software.dobleclick.doblieclick.util

import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import software.ubicuas.ubicuasfinal.MainActivity

/**
 * Created by ASUS on 1/11/2017.
 */
class MyLocationListener: LocationListener{
    lateinit var mainActivity:MainActivity
    override fun onLocationChanged(location: Location) {
        mainActivity.socket.emit("ubication","{\"lat\":\""+location.latitude
                +"\",\"lon\":\""+location.longitude+"\",\"user\":\""+mainActivity.nickname+"\"}")
        mainActivity.longitud = location.longitude
        mainActivity.latitud = location.latitude

    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String) {


    }

    override fun onProviderDisabled(provider: String) {



    }

}