package com.atria.osmdroidspike

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.preference.PreferenceManager

import org.osmdroid.config.Configuration.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.Polyline
import java.lang.Double.max
import java.lang.Double.min
import kotlin.math.abs


class MainActivity : AppCompatActivity() {
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private lateinit var map : MapView
    private val miraiDai = GeoPoint(41.8361445,140.7655297)
    private val otaruEki = GeoPoint(43.1970802,140.9933062)
    private lateinit var points : MutableList<GeoPoint>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

        setContentView(R.layout.activity_main)
        points = mutableListOf()
        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.minZoomLevel = 3.0
        map.maxZoomLevel = 15.0
        val mapController = map.controller
        mapController.setZoom(9.0)
        mapController.setCenter(miraiDai)
        map.setMultiTouchControls(true)//マップの2本指でのピンチアウト
        map.setLayerType(View.LAYER_TYPE_SOFTWARE,null)

        drawLine()
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener{
            val latitude = findViewById<EditText>(R.id.editText1).text.toString().toDouble()
            val longitude = findViewById<EditText>(R.id.editText2).text.toString().toDouble()
            addMarker(GeoPoint(latitude,longitude))
            val area = getArea()
            map.zoomToBoundingBox(area,true)
            drawLine()
            findViewById<TextView>(R.id.comboTextView).text = points.size.toString() + " Combo!"
        }
    }
    private fun addMarker(geoPoint: GeoPoint){
        val marker = Marker(map)
        marker.position = geoPoint
        marker.icon = ResourcesCompat.getDrawable(resources,R.drawable.ic_point_24,null)
        marker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_CENTER)
        map.overlays.add(marker)
        points.add(geoPoint)
    }
    private fun drawLine(){
        val line = Polyline()
        line.setPoints(points)
        line.outlinePaint.color = 0xAA900F42.toInt()
        map.overlays.add(line)
    }
    private fun getArea() : BoundingBox{
        var north : Double = Double.MIN_VALUE
        var south : Double = Double.MAX_VALUE
        var east : Double = Double.MIN_VALUE
        var west : Double = Double.MAX_VALUE
        points.forEach{
            val latitude = it.latitude
            val longitude = it.longitude
            north = max(latitude,north)
            south = min(latitude,south)
            west = min(longitude,west)
            east = max(longitude,east)
        }
        println("n:$north e:$east s:$south w: $west")
        val abs1 = (abs(north)-abs(south))/5
        val abs2 = (abs(east)-abs(west))/5
        return BoundingBox(north+abs1, east+abs2, south-abs1, west-abs2)
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
}
