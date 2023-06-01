package com.sangwon.example.everysiheung

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationView
import com.sangwon.example.everysiheung.adapter.CalendarAdapter
import com.sangwon.example.everysiheung.adapter.ViewPagerAdapter
import com.sangwon.example.everysiheung.databinding.ActivityMainBinding
import com.sangwon.example.everysiheung.model.CalendarDateModel
import com.sangwon.example.everysiheung.utils.HorizontalItemDecoration
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*

private val MIN_SCALE = 0.85f // 뷰가 몇퍼센트로 줄어들 것인지
private val MIN_ALPHA = 0.5f // 어두워지는 정도를 나타낸 듯 하다.

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private val sdf = SimpleDateFormat("yyyy년 MMMM", Locale.KOREAN)
    private val sdf2 = SimpleDateFormat("오늘은 MMMM dd일 EEEE입니다.", Locale.KOREAN)
    private val cal = Calendar.getInstance(Locale.KOREAN)
    private val currentDate = Calendar.getInstance(Locale.KOREAN)
    private val dates = ArrayList<Date>()
    private lateinit var adapter: CalendarAdapter
    private val calendarList2 = ArrayList<CalendarDateModel>()


    private var numBanner = 3 // 배너 갯수
    private var currentPosition = Int.MAX_VALUE / numBanner
    private var myHandler = MyHandler()
    private val intervalTime = 5000.toLong() // 몇초 간격으로 페이지를 넘길것인지 (1500 = 1.5초)

    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 네비게이션 바
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_menu_24_white)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)


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

        // 현재 몇번째 배너인지 보여주는 숫자를 변경함
        binding.posterViewpager.apply {
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.textViewCurrentBanner.text = "[${(currentDate.get(Calendar.MONTH) + 1)}월 행사 포스터]   ${(position % numBanner) + 1} / $numBanner" // 하드코딩. 현재 포스터 수는 3개이다.
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


        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
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
                    // 아이템 3에 대한 동작
                    false
                }
                R.id.list -> {
                    //startActivity(Intent(this, PostUpActivity::class.java))
                    startActivity(Intent(this, PostListActivity::class.java))
                    false
                }
                else -> false
            }
        }
    }

    // 툴바 메뉴 버튼이 클릭 됐을 때 실행하는 함수
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // 클릭한 툴바 메뉴 아이템 id 마다 다르게 실행하도록 설정
        when(item!!.itemId){
            android.R.id.home->{
                // 햄버거 버튼 클릭시 네비게이션 드로어 열기
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 드로어 내 아이템 클릭 이벤트 처리하는 함수
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.post-> {
                Toast.makeText(this,"menu_item1 실행",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, PostListActivity::class.java))
                true
            }
            R.id.bookmark-> Toast.makeText(this,"menu_item2 실행",Toast.LENGTH_SHORT).show()
            R.id.diary-> Toast.makeText(this,"menu_item3 실행",Toast.LENGTH_SHORT).show()
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
                    Log.d("getKeyHash", "key hash: ${Base64.encodeToString(md.digest(), Base64.NO_WRAP)}")
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

    private fun autoScrollStop(){
        myHandler.removeMessages(0) // 핸들러를 중지시킴
    }

    private inner class MyHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            if(msg.what == 0) {
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

    //추가한 내용
    private fun getIdolList(): ArrayList<String> {
        //return arrayListOf<Int>(R.drawable.pic1, R.drawable.pic2, R.drawable.pic3)
        return arrayListOf<String>("https://www.siheung.go.kr/common/imgView.do?attachId=148c15d19a358e7fd81799db36f4771c6111c4314f80b6b967aa9fccff04d2e1&fileSn=f9a1967c526603d17ab488b9d2747cda&mode=origin","https://www.siheung.go.kr/common/imgView.do?attachId=148c15d19a358e7fd81799db36f4771c42076e7d19cdb7974115393f2eb97c1a&fileSn=f9a1967c526603d17ab488b9d2747cda&mode=origin","https://www.siheung.go.kr/common/imgView.do?attachId=148c15d19a358e7fd81799db36f4771c7564eebd039cc288b9ce14bedce381ab&fileSn=f9a1967c526603d17ab488b9d2747cda&mode=origin")
    }

    private fun moveTable(){
        val intent = Intent(this, TableActivity::class.java)
        startActivity(intent)
    }

    private fun moveMap(){
        var intent = Intent(this, FestivalLocationActicity::class.java)
        startActivity(intent)
    }

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
                Toast.makeText(applicationContext, "2024년 행사는 확정되지 않았습니다.", Toast.LENGTH_SHORT).show()
                cal.set(Calendar.MONTH, currentDate.get(Calendar.MONTH))
                setUpCalendar()
            }
            else {
                binding.ivCalendarPrevious.setVisibility(View.VISIBLE)
                cal.add(Calendar.MONTH, 1)
                setUpCalendar()
            }
        }
        binding.ivCalendarPrevious.setOnClickListener {
            cal.add(Calendar.MONTH, -1)
            if (cal.get(Calendar.MONTH) + 1 == 1) {
                binding.ivCalendarPrevious.setVisibility(View.GONE)
            }
            if (cal == currentDate)
                setUpCalendar()
            else
                setUpCalendar()
        }

        val monthUrlMap = mapOf( // 1 ~ 6월까지 행사 일정표
            1 to "https://blog.naver.com/csiheung/222970240186",
            2 to "https://blog.naver.com/csiheung/223003096138",
            3 to "https://blog.naver.com/csiheung/223032419482",
            4 to "https://blog.naver.com/siheungblog?Redirect=Log&logNo=223065558869&from=postView",
            5 to "https://blog.naver.com/PostView.naver?blogId=siheungblog&logNo=223087428882&categoryNo=90&parentCategoryNo=71&viewDate=&currentPage=&postListTopCurrentPage=&isAfterWrite=true",
            6 to "https://blog.naver.com/siheungblog/223116423530"
        )

        binding.imageView2.setOnClickListener {
            val month = cal.get(Calendar.MONTH) + 1
            val url = monthUrlMap[month]
            if (url != null) {
                Toast.makeText(applicationContext, "$month 월 행사 일정표로 이동합니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                it.context.startActivity(intent)
            }
        }

        binding.tvDateMonth.setOnClickListener {
            val month = cal.get(Calendar.MONTH) + 1
            val url = monthUrlMap[month]
            if (url != null) {
                Toast.makeText(applicationContext, "$month 월 행사 일정표로 이동합니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                it.context.startActivity(intent)
            }
        }
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
        binding.textView.text = sdf2.format(currentDate.time)
        binding.tvDateMonth.text = sdf.format(cal.time)
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

        if (cal.get(Calendar.MONTH) + 1 >= 1 && cal.get(Calendar.MONTH) + 1 <= currentDate.get(Calendar.MONTH) + 1) {
            binding.imageView2.setVisibility(View.VISIBLE);
        }
        else {
            binding.imageView2.setVisibility(View.GONE)
        }
    }
}



