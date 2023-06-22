package com.sangwon.example.everysiheung

import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kakao.sdk.user.UserApiClient
import com.sangwon.example.everysiheung.view.DatePickerFragment
import com.sangwon.example.everysiheung.view.TimePicker
import java.util.*


class PostUpActivity : AppCompatActivity() {
    val db = Firebase.firestore
    var latitude: Double = 1.0
    var longitude: Double = 1.0

    var storage = Firebase.storage

    // Reference to an image file in Cloud Storage
    var storageReference = storage.reference




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_up)
        title = "게시물 작성"

        val user = Firebase.auth.currentUser


        val timestamp = Timestamp.now() //Date()와 Timestamp는 서로 대입가능한 관계인가?


        val spinner: Spinner = findViewById<Spinner>(R.id.subscriptSpinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.subscript_spinner,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = it
        }

        findViewById<Button>(R.id.makePostBtn).setOnClickListener {
            // 버튼 누르면 객체를 만들어
            val title = findViewById<EditText>(R.id.titleText).text.toString()
            val date =
                findViewById<Button>(R.id.startDate).text.toString() + "~" + findViewById<Button>(R.id.endDate).text.toString()
            val locate = findViewById<EditText>(R.id.locateText).text.toString()
            val target = if (findViewById<RadioButton>(R.id.anybody).isChecked) {
                findViewById<RadioButton>(R.id.anybody).text.toString()
            } else if (findViewById<RadioButton>(R.id.adult).isChecked) {
                findViewById<RadioButton>(R.id.adult).text.toString()
            } else if (findViewById<RadioButton>(R.id.youth).isChecked) {
                findViewById<RadioButton>(R.id.youth).text.toString()
            } else {
                findViewById<RadioButton>(R.id.child).text.toString()
            }
            val fee = findViewById<EditText>(R.id.feeText).text.toString()
            val subscript = findViewById<Spinner>(R.id.subscriptSpinner).selectedItem.toString()
            val image = findViewById<EditText>(R.id.imageText).text.toString()
            val time =
                findViewById<Button>(R.id.startTime).text.toString() + findViewById<Button>(R.id.endTime).text.toString()
            var bookmark: Int = 0



            val Post = Posts(// 된겨?
                title,
                date, // 혹시 날짜 타입으로 변경을 해줘야 할 수도 있겠다 싶어 아니야 그냥 필요 없을 듯
                locate, // 재밌는거f
                target, //no touch
                fee, //no touch
                subscript, // 설명 글자 수 제한 정도 추가
                image, // 재밌는거
                timestamp, // 딱히 노터치
                kakaouid, //카톡으로 변경시 얻어오는 방법이 따로 있겠지
                latitude,
                longitude,
                time,
                bookmark
            )

            db.collection("Posts")
                .add(Post)
                .addOnSuccessListener { documentReference ->
                    Log.d("db", "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("db", "Error adding document", e)
                }

            var outIntent = Intent(applicationContext, PostUpActivity::class.java)
            setResult(RESULT_OK, outIntent)

            finish()
        }


        // 시간 버튼 누를 때(처음 시간)
        findViewById<Button>(R.id.startTime).setOnClickListener {
            var intent = Intent(application, TimePicker::class.java)
            startActivityForResult(intent, 1)
            // finish()
        }

        // 시간 버튼 누를 때(나중 시간)
        findViewById<Button>(R.id.endTime).setOnClickListener {
            var intent = Intent(application, TimePicker::class.java)
            startActivityForResult(intent, 3)
            // finish()
        }

        // 날짜 버튼 누를 때(처음 날짜)
        findViewById<Button>(R.id.startDate).setOnClickListener {
            var intent = Intent(application, com.sangwon.example.everysiheung.view.DatePicker::class.java)
            startActivityForResult(intent, 10)
            // finish()
        }

        // 날짜 버튼 누를 때(나중 날짜)
        findViewById<Button>(R.id.endDate).setOnClickListener {
            var intent = Intent(application, com.sangwon.example.everysiheung.view.DatePicker::class.java)
            startActivityForResult(intent, 11)
            // finish()
        }


        //지도 버튼 누를 때
        findViewById<ImageButton>(R.id.mapBtn).setOnClickListener {
            var intent = Intent(application, MapsActivity::class.java)
            startActivityForResult(intent, 0)
            // finish()
        }

        //이미지 버튼 누를 때
        findViewById<ImageButton>(R.id.imgBtn).setOnClickListener {
            var intent = Intent(application, ImgActivity::class.java)
            startActivityForResult(intent, 2)
            // finish()
        }

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0) {
                latitude = data!!.getDoubleExtra("latitude", 0.0)
                longitude = data.getDoubleExtra("longitude", 0.0)

                var geocoder = Geocoder(this)
                var addressList = geocoder.getFromLocation(latitude, longitude, 1)
                for (addrres in addressList!!) {
                    findViewById<EditText>(R.id.locateText).setText("${addrres.getAddressLine(0)}")
                    Log.e("address", "${addrres.getAddressLine(0)}")
                }
            } else if (requestCode == 2) {
                var imagePath = data!!.getStringExtra("imagePath")
                findViewById<EditText>(R.id.imageText).setText("${imagePath}")
            }
            else if (requestCode == 1) { // 처음 시간
                val selectedTime = data?.getStringExtra("selectedTime")
                findViewById<Button>(R.id.startTime).text = selectedTime
            }
            else if (requestCode == 3) { // 나중 시간
                val selectedTime = data?.getStringExtra("selectedTime")
                findViewById<Button>(R.id.endTime).text = selectedTime
            }
            else if (requestCode == 10) { // 처음 날짜
                val selectedDate = data?.getStringExtra("selectedDate")
                findViewById<Button>(R.id.startDate).text = selectedDate
            }
            else if (requestCode == 11) { // 나중 날짜
                val selectedDate = data?.getStringExtra("selectedDate")
                findViewById<Button>(R.id.endDate).text = selectedDate
            }
        }
    }



    //좌표를
    fun geoCoding(address: String): Location {
        return try {
            Geocoder(this, Locale.KOREA).getFromLocationName(address, 1)?.let {
                Location("").apply {
                    latitude = it[0].latitude
                    longitude = it[0].longitude
                }
            } ?: Location("").apply {
                latitude = 0.0
                longitude = 0.0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            geoCoding(address) //재시도
        }
    }


}

