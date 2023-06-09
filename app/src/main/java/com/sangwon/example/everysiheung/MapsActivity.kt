package com.sangwon.example.everysiheung

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import net.daum.mf.map.api.CameraUpdateFactory
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView



class MapsActivity : AppCompatActivity(), MapView.MapViewEventListener, MapView.POIItemEventListener {
    private lateinit var mapView : MapView
    private var isMarkerAdded = false
    private var latitude : Double = 1.0
    private var longitude : Double= 1.0


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        mapView = MapView(this)
        val mapLayout = findViewById<RelativeLayout>(R.id.map_view)
        mapLayout.addView(mapView)

        mapView.setMapViewEventListener(this)
        mapView.setPOIItemEventListener(this)

        val targetPoint = MapPoint.mapPointWithGeoCoord(37.380119, 126.803254)
        mapView?.moveCamera(CameraUpdateFactory.newMapPoint(targetPoint))
        val zoomLevel = 14.0 // 원하는 줌 레벨 값 숫자 높으면
        mapView?.setZoomLevel(zoomLevel.toInt())



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

private fun MapView.setZoomLevel(toInt: Int) {

}
