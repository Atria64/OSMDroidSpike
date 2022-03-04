package com.atria.osmdroidspike

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.preference.PreferenceManager

import org.osmdroid.config.Configuration.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.Polyline


class MainActivity : AppCompatActivity() {
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private lateinit var map : MapView
    private val miraiDai = GeoPoint(41.8361445,140.7655297)
    private val otaruEki = GeoPoint(43.1970802,140.9933062)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

        setContentView(R.layout.activity_main)
        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        val mapController = map.controller
        mapController.setZoom(9.5)
        mapController.setCenter(miraiDai)
        map.setMultiTouchControls(true)//マップの2本指でのピンチアウト
        map.setLayerType(View.LAYER_TYPE_SOFTWARE,null)

        markerPractice()

        
    }
    override fun onResume() {
        super.onResume()
        map.onResume();
    }
    override fun onPause() {
        super.onPause()
        map.onPause()
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permissionsToRequest = ArrayList<String>();
        var i = 0
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i])
            i+=1
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }

    private fun markerPractice(){
        val marker = Marker(map)
        marker.position = GeoPoint(miraiDai)
        marker.icon = ResourcesCompat.getDrawable(resources,R.drawable.ic_point_24,null)
        marker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_CENTER)
        map.overlays.add(marker)
        val marker2 = Marker(map)
        marker2.position = GeoPoint(otaruEki)
        marker2.icon = ResourcesCompat.getDrawable(resources,R.drawable.ic_point_24,null)
        marker2.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_CENTER)
        map.overlays.add(marker2)
        val line = Polyline()
        line.setPoints(listOf(miraiDai,otaruEki))
        line.outlinePaint.color = 0xAA900F42.toInt()
        map.overlays.add(line)
    }
}
