package com.sangwon.example.everysiheung

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import android.Manifest
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint


class MapsActivity : AppCompatActivity(), MapView.MapViewEventListener, MapView.POIItemEventListener {
    private lateinit var mapView : MapView
    private var isMarkerAdded = false
    private var latitude : Double = 1.0
    private var longitude : Double= 1.0
    private var LOCATION_PERMISSION_REQUEST_CODE = 1
    private var isMapMovedToCurrentLocation = false // 현재 위치인가?
    private var eventListener: MapView.MapViewEventListener? = null


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        mapView = MapView(this)
        val mapLayout = findViewById<RelativeLayout>(R.id.map_view)
        mapLayout.addView(mapView)

        mapView.setMapViewEventListener(this)
        mapView.setPOIItemEventListener(this)
        // 위치 권한 요청
        requestLocationPermission()


        val inIntent = intent
        findViewById<Button>(R.id.returnbtn).setOnClickListener {
            var outIntent = Intent(applicationContext,MapsActivity::class.java)
            outIntent.putExtra("latitude", latitude)
            outIntent.putExtra("longitude", longitude)
            setResult(Activity.RESULT_OK, outIntent)
            finish()
        }

    }

    override fun onMapViewSingleTapped(mapView: MapView?, mapPoint: MapPoint?) {
        if (!isMarkerAdded) {
            // 마커의 크기를 증가시킬 비율
            val markerSizeRatio = 0.05f

            // 마커 이미지 리소스 ID
            val markerImageResourceId = R.drawable.bluepin

            // 마커 이미지를 비율에 맞게 크기 조정
            val originalBitmap = BitmapFactory.decodeResource(resources, markerImageResourceId)
            val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, (originalBitmap.width * markerSizeRatio).toInt(), (originalBitmap.height * markerSizeRatio).toInt(), false)

            // 증가된 크기의 마커 이미지로 사용할 비트맵을 생성
            val customMarker = Bitmap.createBitmap(scaledBitmap.width, scaledBitmap.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(customMarker)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
            canvas.drawBitmap(scaledBitmap, 0f, 0f, paint)

            val marker = MapPOIItem()
            marker.itemName = "Clicked Marker"
            marker.tag = 0
            marker.mapPoint = mapPoint
            marker.markerType = MapPOIItem.MarkerType.CustomImage
            marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
            marker.customImageBitmap = customMarker

            latitude = mapPoint?.mapPointGeoCoord?.latitude ?: 0.0
            longitude = mapPoint?.mapPointGeoCoord?.longitude ?: 0.0

            mapView?.addPOIItem(marker)
            isMarkerAdded = true
        }
    }
    private fun requestLocationPermission() {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        val permissionGranted = PackageManager.PERMISSION_GRANTED
        if (ContextCompat.checkSelfPermission(this, permission) != permissionGranted) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            // 위치 권한이 이미 허용된 경우, 현재 위치로 지도 이동
            moveMapToCurrentLocation()
        }
    }

    /*private fun moveMapToCurrentLocation() {
        mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
    }*/

    private fun moveMapToCurrentLocation() {
        if (!isMapMovedToCurrentLocation) {
            mapView.currentLocationTrackingMode =
                MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
            eventListener = object : MapView.MapViewEventListener {
                override fun onMapViewInitialized(mapView: MapView) {}
                override fun onMapViewCenterPointMoved(mapView: MapView, mapPoint: MapPoint) {}
                override fun onMapViewZoomLevelChanged(mapView: MapView, zoomLevel: Int) {}
                override fun onMapViewSingleTapped(mapView: MapView, mapPoint: MapPoint) {}
                override fun onMapViewDoubleTapped(mapView: MapView, mapPoint: MapPoint) {}
                override fun onMapViewLongPressed(mapView: MapView, mapPoint: MapPoint) {}
                override fun onMapViewDragStarted(mapView: MapView, mapPoint: MapPoint) {}
                override fun onMapViewDragEnded(mapView: MapView, mapPoint: MapPoint) {}
                override fun onMapViewMoveFinished(mapView: MapView, mapPoint: MapPoint) {
                    isMapMovedToCurrentLocation = true
                    mapView.currentLocationTrackingMode =
                        MapView.CurrentLocationTrackingMode.TrackingModeOff
                    // 이벤트 리스너 해제
                    eventListener = null
                }
            }
            mapView.setMapViewEventListener(eventListener)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 위치 권한이 허용된 경우, 현재 위치로 지도 이동
                moveMapToCurrentLocation()
            } else {
                // 위치 권한이 거부된 경우, 사용자에게 메시지 표시 또는 다른 동작 수행
            }
        }
    }


    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {
        mapView?.removePOIItem(p1)
        isMarkerAdded = false
    }


    // 나머지 MapViewEventListener 메서드들은 필요에 따라 구현할 수 있습니다.
    override fun onMapViewInitialized(mapView: MapView?) {}
    override fun onMapViewCenterPointMoved(mapView: MapView?, mapPoint: MapPoint?) {}
    override fun onMapViewDoubleTapped(mapView: MapView?, mapPoint: MapPoint?) {}
    override fun onMapViewLongPressed(mapView: MapView?, mapPoint: MapPoint?) {}
    override fun onMapViewZoomLevelChanged(mapView: MapView?, zoomLevel: Int) {}
    override fun onMapViewDragStarted(mapView: MapView?, mapPoint: MapPoint?) {}
    override fun onMapViewDragEnded(mapView: MapView?, mapPoint: MapPoint?) {}
    override fun onMapViewMoveFinished(mapView: MapView?, mapPoint: MapPoint?) {}

    // MapView.POIItemEventListener 나머지 메서드
    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {}
    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?, p2: MapPOIItem.CalloutBalloonButtonType?, ) {}
    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {}
}
