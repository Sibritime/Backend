package com.sangwon.example.everysiheung

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
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

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_festival_location)

        mapView = MapView(this)
        val mapLayout = findViewById<RelativeLayout>(R.id.map_view)
        mapLayout.addView(mapView)

        mapView.setMapViewEventListener(this)
        mapView.setPOIItemEventListener(this)

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
                        marker.mapPoint = mapPoint
                        marker.markerType = MapPOIItem.MarkerType.BluePin // 마커 아이콘 타입 설정
                        marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin // 선택된 마커 아이콘 타입 설정

                        mapView.addPOIItem(marker)
                        mapView.setMapCenterPoint(mapPoint, true)
                    }

                }
            }
            .addOnFailureListener { exception ->
                Log.d("PostInfoFailed", "Error getting documents: ", exception)
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
