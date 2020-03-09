package com.ms.soundboard

import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.lang.IllegalStateException
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CODE = 100

    private var output: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var state: Boolean = false
    private var recordingStopped: Boolean = false

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startRecordButton.setOnClickListener {
            // CHECK PERMISSIONS IN ORDER TO RECORD, SAVE AND PLAY AUDIO
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                val permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)

                ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_CODE)
            } else {
                initializeMediaRecorder()
                startRecording()
            }
        }

        stopRecordButton.setOnClickListener {
            stopRecording()
        }

        pauseRecordButton.setOnClickListener {
            pauseRecording()
        }
    }

    /**
     * Define directory where audios will be saved
     * Set up media recorder
     */
    private fun initializeMediaRecorder() {
        output = getExternalFilesDir(null)?.absolutePath + "/recording.mp3"
        mediaRecorder = MediaRecorder()

        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.DEFAULT)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder?.setOutputFile(output)
    }

    /**
     * Method called when user clicks on Start button
     */
    private fun startRecording() {
        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()

            state = true
            Toast.makeText(this, "You're recording", Toast.LENGTH_SHORT).show()

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }

    }

    /**
     * Method called when user clicks on Pause button
     */
    // Function to pause recording audio
    @RequiresApi(Build.VERSION_CODES.N)
    private fun pauseRecording() {
        if (state) {
            if (!recordingStopped) {
                mediaRecorder?.pause()
                recordingStopped = true
                pauseRecordButton.text = "Resume"
            } else {
                resumeRecording()
            }
        }
    }

    /**
     * Method called when user wants to resume recording after pausing it
     */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun resumeRecording() {
        mediaRecorder?.resume()
        pauseRecordButton.text = "Pause"
        recordingStopped = false
    }

    /**
     * Method called when user clicks on Stop button
     */
    // Function to stop recording audio
    private fun stopRecording() {
        if (state) {
            mediaRecorder?.stop()
            mediaRecorder?.release()

            state = false
        } else {
            Toast.makeText(this, "You're not recording right now", Toast.LENGTH_SHORT).show()
        }

    }

    /************************************************
        LIFECYCLE
     ************************************************/

    override fun onPause() {
        super.onPause()

        // Release media recorder according to Android doc
        stopRecording()
    }

    override fun onResume() {
        super.onResume()

        // Release media recorder according to Android doc
        stopRecording()
    }
}
