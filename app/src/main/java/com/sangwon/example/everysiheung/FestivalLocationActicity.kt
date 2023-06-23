package com.sangwon.example.everysiheung

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView


class FestivalLocationActicity : AppCompatActivity(), MapView.MapViewEventListener, MapView.POIItemEventListener {
    private lateinit var mapView: MapView
    var storage = Firebase.storage
    val db = Firebase.firestore
    private var LOCATION_PERMISSION_REQUEST_CODE = 2
    private var isMapMovedToCurrentLocation = false // 현재 위치인가?
    private var eventListener: MapView.MapViewEventListener? = null



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_festival_location)

        mapView = MapView(this)
        val mapLayout = findViewById<RelativeLayout>(R.id.map_view)
        mapLayout.addView(mapView)

        mapView.setMapViewEventListener(this)
        mapView.setPOIItemEventListener(this)

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

        db.collection("Posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    var title = document.getString("title")
                    var latitude = document.getDouble("latitude")?.toDouble()
                    var longitude = document.getDouble("longitude")?.toDouble()


                    if (latitude != null && longitude != null ) {
                        var mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude)

                        var marker = MapPOIItem()
                        marker.itemName = "${title}"
                        marker.tag = 0
                        marker.mapPoint = mapPoint
                        marker.markerType = MapPOIItem.MarkerType.CustomImage
                        marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                        marker.customImageBitmap = customMarker
                        mapView.addPOIItem(marker)
                        //mapView.setMapCenterPoint(mapPoint, true) // 마커된 포인트로 화면 이동
                    }

                }
            }
            .addOnFailureListener { exception ->
                Log.d("PostInfoFailed", "Error getting documents: ", exception)
            }

        requestLocationPermission()
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
        mapView.setCustomCurrentLocationMarkerTrackingImage(R.drawable.custom_marker)
    }*/

    private fun moveMapToCurrentLocation() {
        if (!isMapMovedToCurrentLocation) {
            mapView.currentLocationTrackingMode =
                MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
            mapView.setCustomCurrentLocationMarkerTrackingImage(R.drawable.custom_marker)
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






















    override fun onMapViewInitialized(p0: MapView?) {}

    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {}

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {}

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {}

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {}

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {}

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {}

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {}
    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {}

    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {}
    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {}
    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?, p2: MapPOIItem.CalloutBalloonButtonType?, ) {}
    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {}
}

private fun MapView.setCustomCurrentLocationMarkerTrackingImage(customMarker: Int) {

}
