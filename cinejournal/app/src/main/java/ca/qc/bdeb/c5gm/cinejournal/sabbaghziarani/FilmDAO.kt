package ca.qc.bdeb.c5gm.cinejournal.sabbaghziarani

import android.graphics.ColorSpace.Model
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface FilmDAO {
    @Query("SELECT * FROM Film")
    suspend fun getAll(): List<Film>
    @Query("DELETE FROM Film")
    suspend fun delete()
    @Insert
    suspend fun insertAll(films:List<Film>)
    @Update
    suspend fun updateAll(vararg films: Film)
    @Delete
    suspend fun delete(film: Film)
}
