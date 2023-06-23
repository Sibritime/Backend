package com.sangwon.example.everysiheung

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.*
import android.util.Base64
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.kakao.sdk.user.UserApiClient
import com.sangwon.example.everysiheung.adapter.CalendarAdapter
import com.sangwon.example.everysiheung.adapter.ImageData
import com.sangwon.example.everysiheung.adapter.TodayEventAdapter
import com.sangwon.example.everysiheung.adapter.ViewPagerAdapter
import com.sangwon.example.everysiheung.databinding.ActivityMainBinding
import com.sangwon.example.everysiheung.model.CalendarDateModel
import com.sangwon.example.everysiheung.model.TodayEventItem
import com.sangwon.example.everysiheung.utils.HorizontalItemDecoration
import com.sangwon.example.everysiheung.view.DiaryActivity
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*


private val MIN_SCALE = 0.85f // 뷰가 몇 퍼센트로 줄어들 것인지
private val MIN_ALPHA = 0.5f // 어두워지는 정도를 나타낸 듯 하다.

var kakaouid: String = ""

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private val cal = Calendar.getInstance(Locale.KOREAN)
    private val currentDate = Calendar.getInstance(Locale.KOREAN)
    private val dates = ArrayList<Date>()
    private lateinit var adapter: CalendarAdapter
    private val calendarList2 = ArrayList<CalendarDateModel>()

    private val idolList: ArrayList<ImageData> = getIdolList()
    private val numBanner: Int = idolList.size
    private var currentPosition = numBanner * 1000

    private var myHandler = MyHandler()
    private val intervalTime = 5000.toLong() // 몇초 간격으로 페이지를 넘길것인지 (1500 = 1.5초)

    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout

    private val PREF_NAME = "MyPrefs"
    private val KEY_NAME = "name"

    private lateinit var sharedPreferences: SharedPreferences

    private var backButtonPressedOnce = false

    private lateinit var header_image: CircleImageView


    /**
     * 메인화면에서 종료하는 함수.
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (backButtonPressedOnce) {
                finishAffinity() // 모든 액티비티 종료
            } else {
                backButtonPressedOnce = true
                Toast.makeText(this, "뒤로 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
                Handler().postDelayed({ backButtonPressedOnce = false }, 2000)
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * (230602기준) 6월 포스터 표시됨. 이미지 데이터 추가해서 포스터 추가 가능.
     */
    private fun getIdolList(): ArrayList<ImageData> {
        val idolList = ArrayList<ImageData>()

        // 아까워서 잠시 주석 처리.
        /*val imageData1 = ImageData("https://www.siheung.go.kr/cmm/fms/FileDown.do?atchFileId=148c15d19a358e7fd81799db36f4771c42076e7d19cdb7974115393f2eb97c1a&fileSn=f9a1967c526603d17ab488b9d2747cda",
            "https://www.siheung.go.kr/event/main.do?idx=6277&year=2023&month=6&stateFlag=calendar", 0)
        val imageData2 = ImageData("https://www.siheung.go.kr/cmm/fms/FileDown.do?atchFileId=148c15d19a358e7fd81799db36f4771c91995d2aad34f5a69ce6a53d0e720bb6&fileSn=f9a1967c526603d17ab488b9d2747cda",
            "https://www.siheung.go.kr/event/main.do?idx=6276&year=2023&month=6&stateFlag=calendar",0)
        val imageData3 = ImageData("https://www.siheung.go.kr/cmm/fms/FileDown.do?atchFileId=148c15d19a358e7fd81799db36f4771c37415ce797b610c5d717a5b8e6d87f74&fileSn=f9a1967c526603d17ab488b9d2747cda",
            "https://www.siheung.go.kr/event/main.do?idx=6275&year=2023&month=6&stateFlag=calendar",0)
        val imageData4 = ImageData("https://www.siheung.go.kr/cmm/fms/FileDown.do?atchFileId=148c15d19a358e7fd81799db36f4771c23d5d53603818a0c7bc591e475634fbc&fileSn=f9a1967c526603d17ab488b9d2747cda",
            "https://www.siheung.go.kr/event/main.do?idx=6215&year=2023&month=6&stateFlag=calendar",0)
        val imageData5 = ImageData("https://www.siheung.go.kr/cmm/fms/FileDown.do?atchFileId=148c15d19a358e7fd81799db36f4771c7564eebd039cc288b9ce14bedce381ab&fileSn=f9a1967c526603d17ab488b9d2747cda",
            "https://www.siheung.go.kr/event/main.do?idx=6098&year=2023&month=6&stateFlag=calendar",0)
        val imageData6 = ImageData("https://www.siheung.go.kr/cmm/fms/FileDown.do?atchFileId=148c15d19a358e7fd81799db36f4771cab62a6bdb36afc5d7135aef1ef6c8580&fileSn=f9a1967c526603d17ab488b9d2747cda",
            "https://www.siheung.go.kr/event/main.do?idx=6304&year=2023&month=6&stateFlag=calendar",0)
        val imageData7 = ImageData("https://www.siheung.go.kr/cmm/fms/FileDown.do?atchFileId=148c15d19a358e7fd81799db36f4771c676b5464ed1b6ea919802b8a931af345&fileSn=f9a1967c526603d17ab488b9d2747cda",
            "https://www.siheung.go.kr/event/main.do?idx=6251&year=2023&month=6&stateFlag=calendar",0)
        val imageData8 = ImageData("https://www.siheung.go.kr/cmm/fms/FileDown.do?atchFileId=148c15d19a358e7fd81799db36f4771c8972f79b8c4c374bf9e4420bc35a0f6c&fileSn=f9a1967c526603d17ab488b9d2747cda",
            "https://www.siheung.go.kr/event/main.do?idx=6133&year=2023&month=6&stateFlag=calendar",0)
        val imageData9 = ImageData("https://www.siheung.go.kr/cmm/fms/FileDown.do?atchFileId=148c15d19a358e7fd81799db36f4771c39ea3d9ce5696b8dc08e7dd5aadcfbae&fileSn=3743fa93046d4584eb9e93f9fe62c66e",
            "https://www.siheung.go.kr/event/main.do?idx=6115&year=2023&month=6&stateFlag=calendar",0)
        val imageData10 = ImageData("https://www.siheung.go.kr/cmm/fms/FileDown.do?atchFileId=148c15d19a358e7fd81799db36f4771c87abc44a2384319b02953a94634a14b4&fileSn=f9a1967c526603d17ab488b9d2747cda",
            "https://www.siheung.go.kr/event/main.do?idx=6062&year=2023&month=6&stateFlag=calendar",0)
        val imageData11 = ImageData("https://www.siheung.go.kr/cmm/fms/FileDown.do?atchFileId=148c15d19a358e7fd81799db36f4771c2f754a6fb67d873b1c3245380485b46b&fileSn=f9a1967c526603d17ab488b9d2747cda",
            "https://www.siheung.go.kr/event/main.do?idx=6111&year=2023&month=6&stateFlag=calendar",0)
        val imageData12 = ImageData("https://www.siheung.go.kr/cmm/fms/FileDown.do?atchFileId=148c15d19a358e7fd81799db36f4771ce730bf53712b80a331263ef6e9d56931&fileSn=f9a1967c526603d17ab488b9d2747cda",
            "https://www.siheung.go.kr/event/main.do?idx=6075&year=2023&month=6&stateFlag=calendar",0)*/
        val imageData1 = ImageData(
            "", // imageUrl 비어있음
            "https://www.siheung.go.kr/event/main.do?idx=6215&year=2023&month=6&stateFlag=calendar",
            R.drawable.poster1 // drawable 리소스 ID
        )
        val imageData2 = ImageData(
            "", // imageUrl 비어있음
            "https://www.siheung.go.kr/event/main.do?idx=6070&year=2023&month=6&stateFlag=calendar",
            R.drawable.poster2 // drawable 리소스 ID
        )
        val imageData3 = ImageData(
            "", // imageUrl 비어있음
            "https://www.siheung.go.kr/event/main.do?idx=6276&year=2023&month=6&stateFlag=calendar",
            R.drawable.poster3 // drawable 리소스 ID
        )

        idolList.add(imageData1)
        idolList.add(imageData2)
        idolList.add(imageData3)

        return idolList
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 카카오 사용자 ID 요청
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                // 사용자 정보 요청 실패
            } else if (user != null) {
                kakaouid = user.id.toString()
            }
        }


        // 네비게이션 바
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_menu_24)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)


        /**
         * 상단 네비게이션바 헤더 부분에 이름/프로필 사진 출력 및 저장
         */
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val headerView = navigationView.getHeaderView(0)
        val headerTextView = headerView.findViewById<TextView>(R.id.text_name)
        val name = intent.getStringExtra("name")

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val savedName = sharedPreferences.getString(KEY_NAME, "")
        if (name != null && name.length >= 2) {
            val editor = sharedPreferences.edit()
            editor.putString(KEY_NAME, name)
            editor.apply()
        }
        if (savedName != null && savedName.isNotEmpty()) {
            headerTextView.setText(savedName)
        } else {
            headerTextView.setText(name)
        }

        header_image = headerView.findViewById<CircleImageView>(R.id.image_profile)
        header_image.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, 0)
        }

        val storedBitmap = loadImageFromSharedPreferences()
        if (storedBitmap != null) {
            header_image.setImageBitmap(storedBitmap)
        }


        // 게시판 등록 버튼 그냥 이동만 담당
        //findViewById<Button>(R.id.postbtn).setOnClickListener {
        //startActivity(Intent(this, PostUpActivity::class.java))
        //finish()
        //}


        getKeyHash()
        // 게시물 확인 버튼 일단 위에 꺼 먼저 구현하면서 파이어베이스 데이터 삽입, 이거는 데이터 탐색
        //findViewById<Button>(R.id.checkpostbtn).setOnClickListener {
        //}

        //추가한 내용
        binding.posterViewpager.adapter = ViewPagerAdapter(getIdolList())
        binding.posterViewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.posterViewpager.setCurrentItem(currentPosition, false) // 현재 위치를 지정
        binding.posterViewpager.setPageTransformer(ZoomOutPageTransformer())
        binding.textViewBanner.text =
            "[${(currentDate.get(Calendar.MONTH) + 1)}월 행사 포스터]   1 / $numBanner"


        // 현재 몇번째 배너인지 보여주는 숫자를 변경함
        binding.posterViewpager.apply {
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.textViewBanner.text =
                        "[${(currentDate.get(Calendar.MONTH) + 1)}월 행사 포스터]   ${(position % numBanner) + 1} / $numBanner"
                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    when (state) {
                        // 뷰페이저에서 손 떼었을때 / 뷰페이저 멈춰있을 때
                        ViewPager2.SCROLL_STATE_IDLE -> autoScrollStart(intervalTime)
                        // 뷰페이저 움직이는 중
                        ViewPager2.SCROLL_STATE_DRAGGING -> autoScrollStop()
                    }
                }
            })
        }

        setUpAdapter()
        setUpClickListener()
        setUpCalendar()

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            /**
             * 하단 네비게이션바
             */
            when (item.itemId) {
                R.id.home -> {
                    // 아이템 1에 대한 동작
                    false
                }

                R.id.map -> {
                    // 아이템 2에 대한 동작
                    moveMap()
                    false
                }

                R.id.event -> {
                    moveTable()
                    false
                }

                R.id.search -> {
                    val intent = Intent(this, PostBoardActivity::class.java)
                    intent.putExtra("Role", "Searching")
                    startActivity(intent)
                    false
                }

                R.id.list -> {
                    // intent를 통해 돌아가는 함수를 달리함
                    val intent = Intent(this, PostBoardActivity::class.java)
                    intent.putExtra("Role", "Posts")
                    startActivity(intent)
                    false
                }

                else -> false
            }
        }
        binding.recyclerView.scrollToPosition(currentDate.get(Calendar.DAY_OF_MONTH) - 2)

        val todayAdapter = TodayEventAdapter()

        val listview = binding.todayEventList
        listview.adapter = todayAdapter
        val firebaseStorage = FirebaseStorage.getInstance()
        GlobalScope.launch(Dispatchers.Main) {
            val result = withContext(Dispatchers.IO) {
                val db = Firebase.firestore
                db.collection("event")
                    .orderBy("index", Query.Direction.DESCENDING)
                    .get()
                    .await()
            }

            for (document in result) {
                val event = TodayEventItem(
                    title = document.getString("title")!!,
                    type = document.getString("type")!!,
                    time = document.getString("time")!!,
                    address = document.getString("address")!!,
                    index = document.getString("index")!!.toInt(),
                    idx = document.getString("idx")!!.toInt()
                )
                todayAdapter.addEvent(event)
            }
            todayAdapter.notifyDataSetChanged()
//            val eventListParams =
            var totalHeight = 0
            val desiredWidth =
                View.MeasureSpec.makeMeasureSpec(listview.getWidth(), View.MeasureSpec.AT_MOST)

            val listItem: View = todayAdapter.getView(0, null, listview)
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED)
            totalHeight = listItem.measuredHeight * todayAdapter.count

            val params: ViewGroup.LayoutParams = listview.getLayoutParams()
            params.height = totalHeight + listview.getDividerHeight() * (todayAdapter.count - 1)
            listview.setLayoutParams(params)
            listview.requestLayout()
        }
        listview.setOnItemClickListener { parent, view, position, id ->
            val url = "https://www.siheung.go.kr/event/main.do?idx="+(todayAdapter.getItem(position) as TodayEventItem).idx
            val intent = Intent(this, TableActivity::class.java)
            intent.putExtra("url", url)
            startActivity(intent)
            Toast.makeText(applicationContext, "페이지 로드 중...", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 300) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) finish() else finish()
            }
        }

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImage: Uri? = data.data
            if (selectedImage != null) {
                var inputStream: InputStream? = null
                try {
                    inputStream = contentResolver.openInputStream(selectedImage)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    header_image.setImageBitmap(bitmap)
                    saveImageToSharedPreferences(bitmap)
                } finally {
                    try {
                        inputStream?.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        binding.recyclerView.scrollToPosition(currentDate.get(Calendar.DAY_OF_MONTH) - 2)
        //다이어리 현재 날짜가 화면 중간에 오도록 수정
    }

    // 이미지를 SharedPreferences에 저장하는 함수
    private fun saveImageToSharedPreferences(bitmap: Bitmap) {
        val sharedPreferences = getSharedPreferences("ImagePreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Bitmap을 ByteArray로 변환
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()

        // ByteArray를 Base64로 인코딩하여 저장
        val encodedString = Base64.encodeToString(byteArray, Base64.DEFAULT)
        editor.putString("image", encodedString)
        editor.apply()
    }

    // SharedPreferences에서 이미지를 가져오는 함수
    private fun loadImageFromSharedPreferences(): Bitmap? {
        val sharedPreferences = getSharedPreferences("ImagePreferences", Context.MODE_PRIVATE)
        val encodedString = sharedPreferences.getString("image", null)

        if (encodedString != null) {
            // Base64로 인코딩된 문자열을 ByteArray로 디코딩
            val byteArray = Base64.decode(encodedString, Base64.DEFAULT)

            // ByteArray를 Bitmap으로 변환하여 반환
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        }

        return null
    }

    /**
     * 상단 네비게이션바
     */
    // 툴바 메뉴 버튼이 클릭 됐을 때 실행하는 함수
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // 클릭한 툴바 메뉴 아이템 id 마다 다르게 실행하도록 설정
        when (item.itemId) {
            android.R.id.home -> {
                // 햄버거 버튼 클릭시 네비게이션 드로어 열기
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 드로어 내 아이템 클릭 이벤트 처리하는 함수
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.post -> {
                val intent = Intent(this, PostBoardActivity::class.java)
                intent.putExtra("Role", "MyPosts")
                startActivity(intent)

                return true
            }

            R.id.bookmark -> {
                val intent = Intent(this, PostBoardActivity::class.java)
                intent.putExtra("Role", "BookMarks")
                startActivity(intent)
            }

            R.id.diary -> startActivity(Intent(this@MainActivity, DiaryActivity::class.java))
            R.id.blog -> {
                val dateFormat = SimpleDateFormat("M", Locale.getDefault())
                val currentMonth = dateFormat.format(Calendar.getInstance().time)
                val url = "https://m.blog.naver.com/PostSearchList.naver?blogId=siheungblog&orderType=sim&searchText=$currentMonth%EC%9B%94%20%EB%AC%B8%ED%99%94%EC%98%88%EC%88%A0%EB%8B%AC%EB%A0%A5"
                val intent = Intent(this, TableActivity::class.java)
                intent.putExtra("url", url)
                startActivity(intent)
            }
        }
        return false
    }

    fun getKeyHash() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val packageInfo =
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            for (signature in packageInfo.signingInfo.apkContentsSigners) {
                try {
                    val md = MessageDigest.getInstance("SHA")
                    md.update(signature.toByteArray())
                    Log.d(
                        "getKeyHash",
                        "key hash: ${Base64.encodeToString(md.digest(), Base64.NO_WRAP)}"
                    )
                } catch (e: NoSuchAlgorithmException) {
                    Log.w("getKeyHash", "Unable to get MessageDigest. signature=$signature", e)
                }
            }
        }
    }


    private fun autoScrollStart(intervalTime: Long) {
        myHandler.removeMessages(0) // 이거 안하면 핸들러가 1개, 2개, 3개 ... n개 만큼 계속 늘어남
        myHandler.sendEmptyMessageDelayed(0, intervalTime) // intervalTime 만큼 반복해서 핸들러를 실행하게 함
    }

    private fun autoScrollStop() {
        myHandler.removeMessages(0) // 핸들러를 중지시킴
    }

    private inner class MyHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            if (msg.what == 0) {
                currentPosition = binding.posterViewpager.currentItem
                binding.posterViewpager.setCurrentItem(++currentPosition, true) // 다음 페이지로 이동
                autoScrollStart(intervalTime) // 스크롤을 계속 이어서 한다.
            }
        }
    }

    // 다른 페이지 갔다가 돌아오면 다시 스크롤 시작
    override fun onResume() {
        super.onResume()
        autoScrollStart(intervalTime)
    }

    // 다른 페이지로 떠나있는 동안 스크롤이 동작할 필요는 없음. 정지
    override fun onPause() {
        super.onPause()
        autoScrollStop()
    }

    private fun moveTable() {
        val url = "https://www.siheung.go.kr/event/main.do?stateFlag=list"
        val intent = Intent(this, TableActivity::class.java)
        intent.putExtra("url", url)
        startActivity(intent)
        Toast.makeText(applicationContext, "페이지 로드 중...", Toast.LENGTH_SHORT).show()
    }

    private fun moveMap() {
        var intent = Intent(this, FestivalLocationActicity::class.java)
        startActivity(intent)
    }

    /**
     * 포스터 애니메이션
     */
    inner class ZoomOutPageTransformer : ViewPager2.PageTransformer {
        override fun transformPage(view: View, position: Float) {
            view.apply {
                val pageWidth = width
                val pageHeight = height
                when {
                    position < -1 -> { // [-Infinity,-1)
                        // This page is way off-screen to the left.
                        alpha = 0f
                    }

                    position <= 1 -> { // [-1,1]
                        // Modify the default slide transition to shrink the page as well
                        val scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position))
                        val vertMargin = pageHeight * (1 - scaleFactor) / 2
                        val horzMargin = pageWidth * (1 - scaleFactor) / 2
                        translationX = if (position < 0) {
                            horzMargin - vertMargin / 2
                        } else {
                            horzMargin + vertMargin / 2
                        }

                        // Scale the page down (between MIN_SCALE and 1)
                        scaleX = scaleFactor
                        scaleY = scaleFactor

                        // Fade the page relative to its size.
                        alpha = (MIN_ALPHA +
                                (((scaleFactor - MIN_SCALE) / (1 - MIN_SCALE)) * (1 - MIN_ALPHA)))
                    }

                    else -> { // (1,+Infinity]
                        // This page is way off-screen to the right.
                        alpha = 0f
                    }
                }
            }
        }
    }

    /**
     * Set up click listener
     */
    private fun setUpClickListener() {
        binding.ivCalendarNext.setOnClickListener {
            if (cal.get(Calendar.MONTH) + 1 == 12 && cal.get(Calendar.YEAR) == 2023) {
                Toast.makeText(applicationContext, "현재 월로 이동합니다.", Toast.LENGTH_SHORT).show()
                cal.set(Calendar.MONTH, currentDate.get(Calendar.MONTH))
                setUpCalendar()
            } else {
                binding.ivCalendarPrevious.visibility = View.VISIBLE
                cal.add(Calendar.MONTH, 1)
                setUpCalendar()
            }
        }
        binding.ivCalendarPrevious.setOnClickListener {
            cal.add(Calendar.MONTH, -1)
            if (cal.get(Calendar.MONTH) + 1 == 1) {
                binding.ivCalendarPrevious.visibility = View.GONE
            }
            setUpCalendar()
        }

        // 삭제 예정
        /*val monthUrlMap = mapOf( // 1 ~ 6월까지 행사 일정표
            1 to "https://blog.naver.com/csiheung/222970240186",
            2 to "https://blog.naver.com/csiheung/223003096138",
            3 to "https://blog.naver.com/csiheung/223032419482",
            4 to "https://blog.naver.com/siheungblog?Redirect=Log&logNo=223065558869&from=postView",
            5 to "https://blog.naver.com/PostView.naver?blogId=siheungblog&logNo=223087428882&categoryNo=90&parentCategoryNo=71&viewDate=&currentPage=&postListTopCurrentPage=&isAfterWrite=true",
            6 to "https://blog.naver.com/siheungblog/223116423530"
        )

        val calendarClickListener = OnClickListener {
            val month = cal.get(Calendar.MONTH) + 1
            val url = monthUrlMap[month]
            if (url != null) {
                Toast.makeText(applicationContext, "$month 월 행사 일정표로 이동합니다.", Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent(this, TableActivity::class.java)
                intent.putExtra("url", url)
                startActivity(intent)
            }
        }

        binding.iVCalendar.setOnClickListener(calendarClickListener)

        binding.tvDateMonth.setOnClickListener(calendarClickListener)*/
    }


    /**
     * Setting up adapter for recyclerview
     */
    private fun setUpAdapter() {
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.single_calendar_margin)
        binding.recyclerView.addItemDecoration(HorizontalItemDecoration(spacingInPixels))
        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerView)
        adapter = CalendarAdapter { calendarDateModel: CalendarDateModel, position: Int ->
            calendarList2.forEachIndexed { index, calendarModel ->
                calendarModel.isSelected = index == position
            }
            adapter.setData(calendarList2)
        }
        binding.recyclerView.adapter = adapter
    }

    /**
     * Function to setup calendar for every month
     */
    private fun setUpCalendar() {
        val calendarList = ArrayList<CalendarDateModel>()
        binding.textView.text =
            SimpleDateFormat("오늘은 MMMM dd일 EEEE입니다.", Locale.KOREAN).format(currentDate.time)
        binding.tvDateMonth.text = SimpleDateFormat("yyyy년 MMMM", Locale.KOREAN).format(cal.time)
        val monthCalendar = cal.clone() as Calendar
        val maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        dates.clear()
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)

        while (dates.size < maxDaysInMonth) {
            dates.add(monthCalendar.time)
            calendarList.add(CalendarDateModel(monthCalendar.time))
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        calendarList2.clear()
        calendarList2.addAll(calendarList)
        adapter.setData(calendarList)


        // 삭제 예정
        /*if (cal.get(Calendar.MONTH) + 1 >= 1 && cal.get(Calendar.MONTH) + 1 <= currentDate.get(
                Calendar.MONTH
            ) + 1
        ) {
            binding.iVCalendar.visibility = View.VISIBLE;
        } else {
            binding.iVCalendar.visibility = View.GONE
        }*/
    }


}



