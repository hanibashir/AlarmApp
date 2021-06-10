package com.example.alarmapp.data.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.example.alarmapp.data.models.AlarmItem

@Dao
interface AlarmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alarmItem: AlarmItem)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(alarmItem: AlarmItem)

    @Query("UPDATE alarm_table SET isScheduled = :isScheduled WHERE alarmId = :id")
    suspend fun updateScheduled(isScheduled: Boolean, id: Long)

    @Delete
    suspend fun delete(alarmItem: AlarmItem)

    @Query("DELETE FROM `alarm_table`")
    suspend fun deleteAllAlarms()

    @Query("SELECT * FROM alarm_table WHERE hour LIKE :hour AND minute LIKE :minute AND alarmDay LIKE :alarmDay")
    fun getAlarm(hour: Int, minute: Int, alarmDay: String): LiveData<AlarmItem?>

    @Query("SELECT * FROM `alarm_table` ORDER BY currentTime DESC")
    fun getAllAlarms(): LiveData<List<AlarmItem>>

}