package com.sangwon.example.everysiheung.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

/**
 * Data Access Object
 */
@Dao
interface DiaryDao {
    /**
     * 삽입
     * @param diary 저장할 다이어리 인스턴스
     */
    @Insert
    fun insertDiary(diary: Diary?)

    /**
     * 수정
     * @param diary 수정할 다이어리 인스턴스
     */
    @Update
    fun updateDiary(diary: Diary?)

    /**
     * 수정
     * @param date 수정할 날짜
     * @param text 수정할 일기
     */
    @Query(
        "UPDATE diary " +
                "SET text = :text " +
                "WHERE date LIKE :date"
    )
    fun updateExceptImage(date: String?, text: String?)

    /**
     * 삭제
     * @param diary 삭제할 다이어리 인스턴스
     */
    @Delete
    fun deleteDiary(diary: Diary?)

    /**
     * 조회
     * @param date 조회할 날짜 스트링
     * @return 다이어리 인스턴스
     */
    @Query(
        ("SELECT * " +
                "FROM Diary " +
                "WHERE date LIKE :date")
    )
    fun findByDate(date: String?): Diary?
}