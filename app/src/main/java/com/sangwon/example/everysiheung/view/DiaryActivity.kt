package com.sangwon.example.everysiheung.view

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.sangwon.example.everysiheung.MainActivity
import com.sangwon.example.everysiheung.R
import com.sangwon.example.everysiheung.database.Diary
import com.sangwon.example.everysiheung.database.DiaryDao
import com.sangwon.example.everysiheung.database.DiaryDatabase
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DiaryActivity : AppCompatActivity() {

    /* 위젯 */
    private lateinit var btn_diary_options: ImageView
    private lateinit var iv_photo: ImageView
    private lateinit var btn_save_back: ImageView
    private lateinit var tv_diary_date: TextView
    private lateinit var et_diary: EditText

    private val mFormat = SimpleDateFormat("yyyy.MM.dd.", Locale.getDefault())
    private val dbFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    /* 데이터베이스 */
    private lateinit var mDiaryDao: DiaryDao
    private lateinit var database: DiaryDatabase

    /* 이미지 */
    private val REQUEST_CODE = 0
    private var img: Bitmap? = null
    private var resizedBitmapImg: Bitmap? = null
    private var isImgUpdated = false
    private var imgInByte: ByteArray? = null

    private var date = Date()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)


        /* 데이터베이스 생성 */
        database = DiaryDatabase.getInstance(this)!!
        mDiaryDao = database.diaryDao()!!

        btn_diary_options = findViewById(R.id.btn_diary_options)
        iv_photo = findViewById(R.id.iv_photo)
        btn_save_back = findViewById(R.id.btn_save_back)
        tv_diary_date = findViewById(R.id.tv_diary_date)
        et_diary = findViewById(R.id.et_diary)

        /* 일기 날짜 세팅 */
        val diaryIntent = intent
        val isToday = diaryIntent.getBooleanExtra("today", true)
        if (isToday)
            tv_diary_date.text = getTime()
        else {
            val tv_date = diaryIntent.getStringExtra("date")
            tv_diary_date.text = tv_date
            try {
                date = mFormat.parse(tv_date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }

        /* 일기 레코드 불러오기 */
        val diaryRecord = isExist()
        try {
            if (diaryRecord?.text != null) {
                et_diary.setText(diaryRecord.text)
            }
            if (diaryRecord?.image != null) {
                img = getBitmapInByte(diaryRecord.image!!)
                iv_photo.setImageBitmap(img)
            }
        } catch (e: NullPointerException) {
        }

        iv_photo.setOnClickListener { pickFromGallery() }

        btn_save_back.setOnClickListener {
            val diaryRecord = isExist()

            if (diaryRecord == null) {
                if (iv_photo.drawable == null && et_diary.text.toString().isEmpty()) {
                    finish()
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    if (iv_photo.drawable != null || et_diary.text.toString().isNotEmpty()) {
                        insertRecord()
                        finish()
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                }
            } else if (diaryRecord.text != et_diary.text.toString() || isImgUpdated) {
                updateRecord()
                finish()
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                finish()
                startActivity(Intent(this, MainActivity::class.java))
            }
        }

        btn_diary_options?.setOnClickListener(View.OnClickListener { v: View? ->
            var deleteRecord = Diary()
            deleteRecord.date = dbFormat.format(date)
            mDiaryDao?.deleteDiary(deleteRecord)
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        })

        iv_photo.setBackground(resources.getDrawable(R.drawable.round_image_border, null))
        iv_photo.clipToOutline = true
    }

    /**
     * 뒤로가기 버튼으로 diaryActivity 나가도 저장
     */
    override fun onBackPressed() {
        val diaryRecord = isExist()

        if (diaryRecord == null) {
            if (iv_photo.drawable == null && et_diary.text.toString().isEmpty()) {
                finish()
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                if (iv_photo.drawable != null || et_diary.text.toString().isNotEmpty()) {
                    Toast.makeText(applicationContext, "저장 완료", Toast.LENGTH_SHORT).show()
                    insertRecord()
                    finish()
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
        } else if (diaryRecord.text != et_diary.text.toString() || isImgUpdated) {
            updateRecord()
            finish()
            Toast.makeText(applicationContext, "저장 완료", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    /***
     * 현재 시간 반환 메소드
     */
    private fun getTime(): String {
        val now = System.currentTimeMillis()
        date = Date(now)
        return mFormat.format(date)
    }

    /***
     * 갤러리에서 사진 가져오는 메소드
     */
    private fun pickFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, REQUEST_CODE)
    }

    /**
     *  이미지뷰에 이미지 비트맵으로 넣기
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    val `in`: InputStream? = contentResolver.openInputStream(data?.data!!)
                    img = BitmapFactory.decodeStream(`in`)
                    val stream = ByteArrayOutputStream()
                    img?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    Glide.with(applicationContext)
                        .load(img)
                        .centerCrop()
                        .into(iv_photo)
                    iv_photo.setBackground(resources.getDrawable(R.drawable.round_image_border, null))
                    iv_photo.clipToOutline = true

                    isImgUpdated = true
                    imgInByte = getByteInBitmap(img)
                    while (imgInByte!!.size > 500000) {
                        img = Bitmap.createScaledBitmap(
                            img!!,
                            (img!!.width * 0.8).toInt(),
                            (img!!.height * 0.8).toInt(),
                            true
                        )
                        imgInByte = getByteInBitmap(img)
                    }
                } catch (e: Exception) {
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show()
            }
        }
    }



    /**
     * convert from bitmap to byte array
     *
     * @param bitmap Bitmap
     * @return byte array
     */
    private fun getByteInBitmap(bitmap: Bitmap?): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }

    /**
     * convert from byte array to bitmap
     *
     * @param image byte array
     * @return bitmap
     */
    private fun getBitmapInByte(image: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(image, 0, image.size)
    }

    /**
     * 해당 날짜에 레코드가 존재하는지 확인
     */
    private fun isExist(): Diary? {
        return mDiaryDao.findByDate(dbFormat.format(date))
    }

    /**
     * 데이터베이스 삽입, 수정 메소드
     */
    private fun insertRecord() {
        val insertRecord = Diary()
        insertRecord.date = dbFormat.format(date)
        insertRecord.text = et_diary.text.toString()
        if (img == null)
            insertRecord.image = null
        else {
            insertRecord.image = imgInByte
        }
        mDiaryDao.insertDiary(insertRecord)

    }

    private fun updateRecord() {
        if (iv_photo.drawable == null || !isImgUpdated) {
            mDiaryDao.updateExceptImage(
                dbFormat.format(date),
                et_diary.text.toString()
            )
        } else { // 이미지 변경시에만 이미지 업데이트
            val updateRecord = Diary()
            updateRecord.date = dbFormat.format(date)
            updateRecord.text = et_diary.text.toString()
            updateRecord.image = imgInByte
            mDiaryDao.updateDiary(updateRecord)
        }
    }


}