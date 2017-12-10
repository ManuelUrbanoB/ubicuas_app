 package software.ubicuas.ubicuasfinal

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import software.dobleclick.doblieclick.util.MyLocationListener
import java.net.URISyntaxException
import java.math.*


 class MainActivity  : AppCompatActivity()  {
     lateinit var socket:Socket
     lateinit var dialog: ProgressDialog
     private val locationListener = MyLocationListener()
     private lateinit var locationManager: LocationManager
     var nickname = ""
     var latitud:Double=0.0
     var longitud:Double=0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        locationListener.mainActivity = this
        nickname =intent.extras.getString("nick")
        try {
            socket = IO.socket("http://192.168.43.103:3000")
        }catch (e: URISyntaxException){
            throw RuntimeException(e)
        }
        socket.connect()

    }


     override fun onResume() {
         super.onResume()

         loadLocation()
         socket.on("ubication",newUbication)

     }
     private val newUbication = Emitter.Listener { args ->
         runOnUiThread {
             var data = args[0] as JSONObject
             var longitudS: String
             var latitudS: String
             var user: String
             try {
                 longitudS = data.getString("longitud").toString()
                 latitudS= data.getString("latitud").toString()
                 user= data.getString("user").toString()
                 text_user.text="Usuario: "+user
                 var disLat = latitud - latitudS.toDouble()
                 var disLon = longitud - longitudS.toDouble()
                 val distancia = Math.sqrt(disLat*disLat+disLon*disLon)
                 text_distancia.text="distancia a ti: "+distancia


             } catch (e: JSONException) {

             }
         }
     }



 override fun onDestroy() {
         super.onDestroy()
         socket.disconnect()
     }

     @SuppressLint("MissingPermission")
     private fun loadLocation(){
         locationManager = this.getSystemService(android.content.Context.LOCATION_SERVICE) as LocationManager

         locationListener.mainActivity = this
         if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
             ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 200)
             ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 200)
             return
         }
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE
            criteria.isAltitudeRequired = false
            criteria.isBearingRequired = false
            criteria.isCostAllowed = true
            criteria.powerRequirement = Criteria.POWER_LOW
            val provider = locationManager.getBestProvider(criteria, true)
            val location = locationManager.getLastKnownLocation(provider)
            setLocation(location)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 10f, locationListener)
     }
     private fun setLocation(location: Location){
         socket.emit("ubication","{\"lat\":\""+location.latitude
                 +"\",\"lon\":\""+location.longitude+"\",\"user\":\""+nickname+"\"}")


      }
     fun isLocationEnabled(){
         if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
             val alertDialog = AlertDialog.Builder(this)
             alertDialog.setTitle("Habilita tu ubicación")
             alertDialog.setMessage("Tú GPS esta desactivado, por favor activalo para buscar tu ubicaión automaticamente")
             alertDialog.setPositiveButton("Ir a ajustes GPS") { _, _ ->
                 val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                 startActivity(intent)
             }
             alertDialog.setNegativeButton("Cancelar") { dialog, _ ->
                 dialog.cancel()
             }
             val alert = alertDialog.create()
             alert.show()
         } else {
             val alertDialog = AlertDialog.Builder(this)
             alertDialog.setTitle("GPS activado")
             alertDialog.setNegativeButton("Siguiente") { dialog, _ -> dialog.cancel() }
             val alert = alertDialog.create()
             alert.show()
         }
     }








}
