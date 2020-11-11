package com.aminivan.mapslearn

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Telephony
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_maps.*
import java.lang.Exception

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    var loopsndntf : Int = 0;
    private val channelId = "com.aminivan.learnnotification"
    private  val description = "Test notification"
    var soundNotification = MediaPlayer()
    var soundNotificationArrive = MediaPlayer()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

            val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
            checkPermission()
            LoadHalte()

    }

    var AccessLocation = 123
    fun checkPermission() {
        if(Build.VERSION.SDK_INT >=23)
        {
            if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),AccessLocation)
                return
            }
        }
        getUserLocation()
    }

    @SuppressLint("MissingPermission")
    fun getUserLocation() {
        Toast.makeText(this,"User Location Access On",Toast.LENGTH_SHORT).show()

        var loclic = MyLocationListener()
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3,3f,loclic)

        var thread = MyThread()
        thread.start()
    }

    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<out String>,grantResults: IntArray ) {
        when(requestCode)
        {
            AccessLocation -> {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    getUserLocation()
                }
                else {
                    Toast.makeText(this,"User Location Not Granted Permission",Toast.LENGTH_SHORT).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera

    }

    var location : Location? = null

    inner class MyLocationListener : LocationListener {


        constructor(

        )
        override fun onLocationChanged(p0: Location?) {
            location = p0
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

        override fun onProviderEnabled(provider: String?) {
        }

        override fun onProviderDisabled(provider: String?) {
        }

    }


    inner class MyThread : Thread
    {

        var oldLocation : Location?=null

        constructor() : super()
        {
            oldLocation = Location("Start")
            oldLocation!!.longitude = 0.0
            oldLocation!!.latitude = 0.0
        }

        override fun run()
        {
            while (true)
            {
                try {

                    if(oldLocation!!.distanceTo(location)==0f) {
                        continue
                    }
                    oldLocation=location

                    runOnUiThread {
                        mMap.clear()

                        //Show user
                        val sydney = LatLng(location!!.latitude, location!!.longitude)
                        mMap.addMarker(MarkerOptions()
                            .position(sydney)
                            .title("You")
                            .snippet("This is my location")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.man)))
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,30f))
                        mMap.uiSettings.setZoomControlsEnabled(true)        //Custom Button

                        for ( i in 0..listHalte.size-1)
                        {
                            var newHalte = listHalte[i]
                            if(newHalte.IsCatch == false ) {
                                val halteLoc = LatLng(newHalte.location!!.latitude, newHalte.location!!.longitude)
                                mMap.addMarker(MarkerOptions()
                                    .position(halteLoc)
                                    .title(newHalte.name)
                                    .snippet(newHalte.des)
                                    .icon(BitmapDescriptorFactory.fromResource(newHalte.image!!)))
                                if(location!!.distanceTo(newHalte.location)<20)
                                {
                                    LoadHalte()
                                    newHalte.IsCatch = true
                                    listHalte[i] = newHalte
                                    try{
                                        val afsel = applicationContext.assets.openFd("SelamatHalteSMK.mp3")
                                        soundNotificationArrive.setDataSource(afsel.getFileDescriptor(), afsel.getStartOffset(),afsel.getLength())
                                        afsel.close()
                                        soundNotificationArrive.prepare()
                                    }
                                    catch (e: Exception){
                                        e.printStackTrace()
                                    }
                                    soundNotificationArrive.start()
                                    Toast.makeText(applicationContext,"Selamat Datang Di " + newHalte.name,Toast.LENGTH_LONG).show()
                                    loopsndntf == 0
                                }

                                else if(location!!.distanceTo(newHalte.location)<200 )
                                {
                                    if(loopsndntf == 0)
                                    {
                                        LoadHalte()
                                        newHalte.IsCatch = true
                                        listHalte[i] = newHalte
                                        try{
                                            val afd = applicationContext.assets.openFd("in200Meters.mp3")
                                            soundNotification.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),afd.getLength())
                                            afd.close()
                                            soundNotification.prepare()
                                        }
                                        catch (e: Exception){
                                            e.printStackTrace()
                                        }
                                        soundNotification.start()
                                        Toast.makeText(applicationContext,"Dalam 200 meter , kita akan sampai di  "+newHalte.name+"periksa barang bawaan anda sebelum meninggalkan bis ",Toast.LENGTH_LONG).show()
                                        loopsndntf = 1;
                                    }
                                    else {

                                    }

                                }
                            }
                        }
                    }
                    Thread.sleep(1000)
                }
                catch (ex : Exception) {

                }
            }
            super.run()
        }
    }

    val listHalte  = ArrayList<Halte>()

    fun LoadHalte() {
        listHalte.add(Halte(R.drawable.station,"Halte SMK N 1 Purbalingga","Depan SMK N 1 Purbalinngga",-7.403905,109.347179))
        listHalte.add(Halte(R.drawable.station,"Halte Kalikabong","Setelah stasiun",-7.395075,109.352773))
        listHalte.add(Halte(R.drawable.station,"Halte Banjarsari","Depan Setelah jembatan",-7.444266,109.319601))
    }


}
