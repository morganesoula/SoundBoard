package com.ms.soundboard

import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.widget.Chronometer
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    /************************************************
        DATA
     ************************************************/

    private val PERMISSIONS_REQUEST_CODE = 100

    private var output: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var state: Boolean = false
    private var recordingStopped: Boolean = false

    private lateinit var nameEditText: EditText

    private lateinit var chronometer: Chronometer

    private var elapsedMillis: Long = 0


    /************************************************
        METHOD
     ************************************************/

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        defineRecordingNamePopUp()

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
     * Popup - User needs to fill it with an original name
     */
    private fun defineRecordingNamePopUp() {

        startRecordButton.isEnabled = true

        nameEditText = EditText(this)

        var alertDialog = AlertDialog.Builder(this)
            .setMessage("How do you want to name this recording?")
            .setView(nameEditText)
            .setCancelable(false)
            .setPositiveButton(R.string.OK) { dialog, _ ->
                dialog.dismiss()
                initializeMediaRecorder()
            }

        val dialogPopUp = alertDialog.create()
        dialogPopUp.show()

        dialogPopUp.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        nameEditText.addTextChangedListener (object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                dialogPopUp.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = !s.isNullOrEmpty() || !s.isNullOrBlank()
            }
        })
    }


    /**
     * Define directory where audios will be saved
     * Set up media recorder
     */
    private fun initializeMediaRecorder() {
        output = getExternalFilesDir(null)?.absolutePath + "/${nameEditText.text}.mp3"
        mediaRecorder = MediaRecorder()

        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.DEFAULT)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder?.setOutputFile(output)
    }

    /**
     * Method called when user clicks on Start button
     * To see duration, chronometer also starts - base 00:00
     */
    private fun startRecording() {
        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()

            startRecordButton.isEnabled = false

            chronometer = findViewById(R.id.recordingTime)
            chronometer.base = SystemClock.elapsedRealtime()

            chronometer.start()

            state = true
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }

    }

    /**
     * Method called when user clicks on Pause button
     * Elapsed time is saved
     */
    // Function to pause recording audio
    @RequiresApi(Build.VERSION_CODES.N)
    private fun pauseRecording() {
        if (state) {
            if (!recordingStopped) {
                mediaRecorder?.pause()

                elapsedMillis = SystemClock.elapsedRealtime() - chronometer.base

                chronometer.stop()
                recordingStopped = true
                pauseRecordButton.text = "Resume"
            } else {
                chronometer.base = SystemClock.elapsedRealtime() - elapsedMillis
                chronometer.start()

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
            chronometer.stop()
            mediaRecorder?.stop()
            mediaRecorder?.release()

            state = false

            Toast.makeText(this, "Recording saved under the name ${nameEditText.text}.mp3", Toast.LENGTH_SHORT).show()
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
