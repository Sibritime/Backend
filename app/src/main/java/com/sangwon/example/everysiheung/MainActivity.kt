package com.sangwon.example.everysiheung

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import com.sangwon.example.everysiheung.databinding.ActivityMainBinding
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.net.Uri
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import androidx.viewpager2.widget.ViewPager2
import com.sangwon.example.everysiheung.adapter.CalendarAdapter
import com.sangwon.example.everysiheung.adapter.ViewPagerAdapter
import com.sangwon.example.everysiheung.model.CalendarDateModel
import com.sangwon.example.everysiheung.utils.HorizontalItemDecoration

private val MIN_SCALE = 0.85f // 뷰가 몇퍼센트로 줄어들 것인지
private val MIN_ALPHA = 0.5f // 어두워지는 정도를 나타낸 듯 하다.

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val sdf = SimpleDateFormat("yyyy년 MMMM", Locale.KOREAN)
    private val sdf2 = SimpleDateFormat("오늘은 MMMM dd일 EEEE입니다.", Locale.KOREAN)
    private val cal = Calendar.getInstance(Locale.KOREAN)
    private val currentDate = Calendar.getInstance(Locale.KOREAN)
    private val dates = ArrayList<Date>()
    private lateinit var adapter: CalendarAdapter
    private val calendarList2 = ArrayList<CalendarDateModel>()

    private var numBanner = 3 // 배너 갯수
    private var currentPosition = Int.MAX_VALUE / 3
    private var myHandler = MyHandler()
    private val intervalTime = 5000.toLong() // 몇초 간격으로 페이지를 넘길것인지 (1500 = 1.5초)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        binding.textViewTotalBanner.text = numBanner.toString()

        // 현재 몇번째 배너인지 보여주는 숫자를 변경함
        binding.posterViewpager.apply {
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.textViewCurrentBanner.text = "[" + (currentDate.get(Calendar.MONTH) + 1).toString() + "월 행사 포스터]" + "   ${(position % 3) + 1}" // 하드코딩. 현재 포스터 수는 3개이다.
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
                    true
                }
                R.id.map -> {
                    // 아이템 2에 대한 동작
                    moveMap()
                    true
                }
                R.id.event -> {
                    moveTable()
                    true
                }
                R.id.search -> {
                    // 아이템 3에 대한 동작
                    true
                }
                R.id.list -> {
                    //startActivity(Intent(this, PostUpActivity::class.java))
                    startActivity(Intent(this, PostListActivity::class.java))
                    true
                }
                else -> false
            }
        }

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
    private fun getIdolList(): ArrayList<Int> {
        return arrayListOf<Int>(R.drawable.pic1, R.drawable.pic2, R.drawable.pic3)
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
        binding.imageView2.setOnClickListener {
            if (cal.get(Calendar.MONTH) + 1 == 1) {
                Toast.makeText(applicationContext, "1월 행사 일정표로 이동합니다.", Toast.LENGTH_SHORT).show()
                val url = "https://blog.naver.com/csiheung/222970240186"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                it.context.startActivity(intent)
            }
            if (cal.get(Calendar.MONTH) + 1 == 2) {
                Toast.makeText(applicationContext, "2월 행사 일정표로 이동합니다.", Toast.LENGTH_SHORT).show()
                val url = "https://blog.naver.com/csiheung/223003096138"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                it.context.startActivity(intent)
            }
            if (cal.get(Calendar.MONTH) + 1 == 3) {
                Toast.makeText(applicationContext, "3월 행사 일정표로 이동합니다.", Toast.LENGTH_SHORT).show()
                val url = "https://blog.naver.com/csiheung/223032419482"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                it.context.startActivity(intent)
            }
            if (cal.get(Calendar.MONTH) + 1 == 4) {
                Toast.makeText(applicationContext, "4월 행사 일정표로 이동합니다.", Toast.LENGTH_SHORT).show()
                val url = "https://blog.naver.com/siheungblog?Redirect=Log&logNo=223065558869&from=postView"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                it.context.startActivity(intent)
            }
            if (cal.get(Calendar.MONTH) + 1 == 5) {
                Toast.makeText(applicationContext, "5월 행사 일정표로 이동합니다.", Toast.LENGTH_SHORT).show()
                val url =
                    "https://blog.naver.com/PostView.naver?blogId=siheungblog&logNo=223087428882&categoryNo=90&parentCategoryNo=71&viewDate=&currentPage=&postListTopCurrentPage=&isAfterWrite=true"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                it.context.startActivity(intent)
            }
            if (cal.get(Calendar.MONTH) + 1 == 6) { // 시흥시 동네소식알리미
                Toast.makeText(applicationContext, "6월 행사 일정표로 이동합니다.", Toast.LENGTH_SHORT).show()
                val url =
                    "https://www.siheung.go.kr/event/main.do"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                it.context.startActivity(intent)
            }
            if (currentDate.get(Calendar.MONTH) + 1 == 7) { // 시흥시 동네소식알리미
                Toast.makeText(applicationContext, "7월 행사 일정표로 이동합니다.", Toast.LENGTH_SHORT).show()
                val url =
                    "https://www.siheung.go.kr/event/main.do"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                it.context.startActivity(intent)
            }
        }
        binding.tvDateMonth.setOnClickListener {
            if (cal.get(Calendar.MONTH) + 1 == 1) {
                Toast.makeText(applicationContext, "1월 행사 일정표로 이동합니다.", Toast.LENGTH_SHORT).show()
                val url = "https://blog.naver.com/csiheung/222970240186"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                it.context.startActivity(intent)
            }
            if (cal.get(Calendar.MONTH) + 1 == 2) {
                Toast.makeText(applicationContext, "2월 행사 일정표로 이동합니다.", Toast.LENGTH_SHORT).show()
                val url = "https://blog.naver.com/csiheung/223003096138"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                it.context.startActivity(intent)
            }
            if (cal.get(Calendar.MONTH) + 1 == 3) {
                Toast.makeText(applicationContext, "3월 행사 일정표로 이동합니다.", Toast.LENGTH_SHORT).show()
                val url = "https://blog.naver.com/csiheung/223032419482"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                it.context.startActivity(intent)
            }
            if (cal.get(Calendar.MONTH) + 1 == 4) {
                Toast.makeText(applicationContext, "4월 행사 일정표로 이동합니다.", Toast.LENGTH_SHORT).show()
                val url = "https://blog.naver.com/siheungblog?Redirect=Log&logNo=223065558869&from=postView"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                it.context.startActivity(intent)
            }
            if (cal.get(Calendar.MONTH) + 1 == 5) {
                Toast.makeText(applicationContext, "5월 행사 일정표로 이동합니다.", Toast.LENGTH_SHORT).show()
                val url =
                    "https://blog.naver.com/PostView.naver?blogId=siheungblog&logNo=223087428882&categoryNo=90&parentCategoryNo=71&viewDate=&currentPage=&postListTopCurrentPage=&isAfterWrite=true"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                it.context.startActivity(intent)
            }
            if (cal.get(Calendar.MONTH) + 1 == 6) { // 시흥시 동네소식알리미
                Toast.makeText(applicationContext, "6월 행사 일정표로 이동합니다.", Toast.LENGTH_SHORT).show()
                val url =
                    "https://www.siheung.go.kr/event/main.do"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                it.context.startActivity(intent)
            }
            if (currentDate.get(Calendar.MONTH) + 1 == 7) { // 시흥시 동네소식알리미
                Toast.makeText(applicationContext, "7월 행사 일정표로 이동합니다.", Toast.LENGTH_SHORT).show()
                val url =
                    "https://www.siheung.go.kr/event/main.do"
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