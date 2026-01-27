package com.pv.scaralina.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TermDao {

    @Query("SELECT * FROM termini ORDER BY parola ASC")
    suspend fun getAll(): List<TermEntity>

    @Query("SELECT * FROM termini WHERE parola = :parola COLLATE NOCASE LIMIT 1")
    suspend fun getByParola(parola: String): TermEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(termini: List<TermEntity>)

    @Query("DELETE FROM termini WHERE parola = :parola")
    suspend fun deleteByParola(parola: String)
}
