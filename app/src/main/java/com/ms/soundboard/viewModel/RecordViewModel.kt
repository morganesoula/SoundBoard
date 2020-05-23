package com.ms.soundboard.viewModel

import android.app.Application
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.ms.soundboard.model.Record
import com.ms.soundboard.repository.RecordRepository
import com.ms.soundboard.utils.SoundBoardDatabase
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.lang.Exception

class RecordViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RecordRepository
    val allRecords: LiveData<List<Record>>
    val allRecordsOrderedByTitle : LiveData<List<Record>>
    private val mediaPlayer: MediaPlayer

    init {
        val recordsDAO = SoundBoardDatabase.getDatabase(application, viewModelScope).recordDAO()
        repository = RecordRepository(recordsDAO)
        allRecords = repository.allRecords
        allRecordsOrderedByTitle = repository.allRecordsOrderedByTitle
        mediaPlayer = MediaPlayer()
    }

    /**
     * Give record to save it into DB via repository
     */
    fun insert(record: Record) = viewModelScope.launch {
        repository.insert(record)
    }

    /**
     * Get all records from DB
     */
    fun getAll() = viewModelScope.launch {
        repository.getAll()
    }

    /**
     * Get each record from external storage
     * TODO Will probably be useless at some time if you save Record directly in DB
     */
    /* fun getRecordsFromExternalDir(context: Context) {
        val recordsToDisplay: ArrayList<File> = ArrayList()
        val recordsFromExternalDir = File(context.getExternalFilesDir("SoundboardDir/").toString()).listFiles()

        for (record in recordsFromExternalDir) {
            if (record.name.endsWith(".mp3"))
                recordsToDisplay.add(record.absoluteFile)
        }

        for (record in recordsToDisplay) {
            val recordToInsert = Record(0, record.name, "", record.absolutePath, null)

            //To define duration of record
            setDuration(recordToInsert)
            insert(recordToInsert)
        }

    } */

    fun setDuration(record: Record) {
        val mediaRetriever = MediaMetadataRetriever()
        mediaRetriever.setDataSource(record.source)

        val duration = (mediaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)).toLong()

        record.time = duration

        mediaRetriever.release()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun onMusicNoteClicked(record: Record) {
        playRecord(record, null, null)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun onReplayButtonClicked(source: String, context: Context) {
        playRecord(null, source, context)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun playRecord(record: Record?, source: String?, context: Context?) {
        mediaPlayer.reset()

        mediaPlayer.setAudioAttributes(
            AudioAttributes
                .Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                .build()
        )

        if (null != record)
            mediaPlayer.setDataSource(record.source)
        else {
            val outputSource = context!!.getExternalFilesDir("TmpRecording").toString() + "/$source.mp3"
            mediaPlayer.setDataSource(outputSource)
        }

        try {
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: IOException) {
            Toast.makeText(context, "Problem found IOException", Toast.LENGTH_SHORT).show()
            Log.e("XXX", e.message!!)
        } catch (ex: Exception) {
            Toast.makeText(context, "Problem found Exception", Toast.LENGTH_SHORT).show()
            Log.e("XXX", ex.message!!)
        }
    }
}