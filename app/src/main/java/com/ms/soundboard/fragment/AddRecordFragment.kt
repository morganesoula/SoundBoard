package com.ms.soundboard.fragment

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.transition.TransitionManager
import com.ms.soundboard.model.Record
import com.ms.soundboard.R
import com.ms.soundboard.viewModel.RecordViewModel
import com.transitionseverywhere.Slide
import kotlinx.android.synthetic.main.fragment_add_record.*
import java.io.File
import java.io.IOException

class AddRecordFragment : Fragment() {

    /************************************************
        DATA
     ************************************************/

    private val PERMISSIONS_REQUEST_CODE = 100

    private var output: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var state: Boolean = false
    private var recordingStopped: Boolean = false

    private lateinit var nameEditText: EditText

    private var elapsedMillis: Long = 0

    private var startButtonClicked: Boolean = false

    private lateinit var recordViewModel : RecordViewModel

    private var recordToSave : Record? = null
    private val tmpRecordName = "chien"

    private var replayRecordButton: Button? = null

    companion object {

        fun newInstance() : AddRecordFragment {
            return AddRecordFragment()
        }
    }


    /************************************************
        METHOD
     ************************************************/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        recordViewModel = ViewModelProvider(this).get(RecordViewModel::class.java)
        recordToSave = Record()

        return inflater.inflate(R.layout.fragment_add_record, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeMediaRecorder()

        val startRecordButton = activity?.findViewById(R.id.startRecordButton) as ImageButton
        val stopRecordButton = activity?.findViewById(R.id.stopRecordButton) as ImageButton
        replayRecordButton = activity?.findViewById(R.id.replayRecordButton) as Button

        startRecordButton.setOnClickListener {
            // CHECK PERMISSIONS IN ORDER TO RECORD, SAVE AND PLAY AUDIO
            if (activity?.applicationContext?.let { it1 ->
                    ContextCompat.checkSelfPermission(
                        it1,
                        android.Manifest.permission.RECORD_AUDIO)
                } != PackageManager.PERMISSION_GRANTED &&
                activity?.applicationContext?.let { it1 -> ContextCompat.checkSelfPermission(it1, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) } != PackageManager.PERMISSION_GRANTED) {

                val permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)

                ActivityCompat.requestPermissions(activity?.applicationContext as Activity, permissions, PERMISSIONS_REQUEST_CODE)
            } else {
                startRecording()
            }
        }

        stopRecordButton.setOnClickListener {
            stopRecording()
        }

    }

    /**
     * Popup - User needs to fill it with an original name
     */
    private fun defineRecordingNamePopUp() {

        startRecordButton?.isEnabled = true

        val alertDialog = activity?.let {
            AlertDialog.Builder(it)
                .setMessage("How do you want to name this recording?")
                .setView(nameEditText)
                .setCancelable(true)
                .setOnCancelListener { dialog ->
                    val intent = Intent(activity, activity!!::class.java)
                    dialog.dismiss()

                    startActivity(intent)
                }
                .setPositiveButton(R.string.OK) { dialog, _ ->
                    dialog.dismiss()
                    initializeMediaRecorder()
                }
        }

        val dialogPopUp = alertDialog?.create()
        dialogPopUp?.show()

        dialogPopUp?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = false
        nameEditText.addTextChangedListener (object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                dialogPopUp?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = !s.isNullOrEmpty() || !s.isNullOrBlank()
            }
        })
    }


    /**
     * Define directory where audios will be saved
     * Set up media recorder
     */
    private fun initializeMediaRecorder() {
        output = activity?.getExternalFilesDir("SoundboardDir").toString()

        mediaRecorder = MediaRecorder()

        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.DEFAULT)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder?.setOutputFile("$output/$tmpRecordName.mp3")

        mediaRecorder?.prepare()
    }

    /**
     * Method called when user clicks on Start button
     * To see duration, chronometer also starts - base 00:00
     */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun startRecording() {
        try {
            mediaRecorder?.start()

            startButtonClicked = true

            chronometer.base = SystemClock.elapsedRealtime()

            chronometer.start()

            state = true
            startRecordButton.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_pause_circle_outline_24))
            startRecordButton.setBackgroundColor(resources.getColor(R.color.pauseColor))

            startRecordButton.setOnClickListener {
                pauseRecording()
            }

        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("XXX", e.message)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            Log.e("XXX", e.message)
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
                startRecordButton.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_play_circle_outline_24))
                startRecordButton.setBackgroundColor(resources.getColor(R.color.holoGreen))
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

        startRecordButton.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_pause_circle_outline_24))
        startRecordButton.setBackgroundColor(resources.getColor(R.color.pauseColor))
        recordingStopped = false
    }

    /**
     * Method called when user clicks on Stop button
     */
    // Function to stop recording audio
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun stopRecording() {
        if (state) {
            chronometer.stop()
            mediaRecorder?.apply {
                stop()
                release()
            }
            state = false

            startRecordButton.isEnabled = false
            stopRecordButton.isEnabled = false

            saveOrDeleteRecord()
        }

    }

    private fun saveOrDeleteRecord() {
        val saveButton = activity?.findViewById(R.id.saveRecordIB) as ImageButton
        val deleteButton = activity?.findViewById(R.id.deleteRecordIB) as ImageButton

        saveButton.visibility = View.VISIBLE
        saveButton.animate().translationX(-40F)

        deleteButton.visibility = View.VISIBLE
        deleteButton.animate().translationX((40F))

        saveButton.setOnClickListener {
            context?.let{ defineRecordName(it) }
        }

        deleteButton.setOnClickListener {
            Log.i("XXX", "Deleted")
        }

    }

    private fun defineRecordName(context: Context) {
        var recordName = ""
        val editText = EditText(context)

        val alertDialogBuilder = AlertDialog.Builder(context)
            .setMessage(R.string.addTitle)
            .setView(editText)
            .setCancelable(false)
            .setPositiveButton(R.string.ok) {_, _ ->
                recordName = editText.text.toString()

                if ("" != recordName)
                    recordToSave?.let { record ->

                        val tmpRecordFile = File(activity?.getExternalFilesDir("SoundboardDir"), "${tmpRecordName}.mp3")
                        val fileRecordName = File(activity?.getExternalFilesDir("SoundboardDir"), "${recordName}.mp3")
                        val recordRenamed = tmpRecordFile.renameTo(fileRecordName)

                        if (recordRenamed)
                            record.source = activity?.getExternalFilesDir("SoundboardDir").toString() + "/${recordName}.mp3"
                            recordViewModel.setDuration(record)
                            record.title = recordName

                            recordViewModel.insert(record)
                    }
            }
            .setNeutralButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    /************************************************
        LIFECYCLE
     ************************************************/


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onPause() {
        super.onPause()

        // Release media recorder according to Android doc
        stopRecording()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onResume() {
        super.onResume()

        // Release media recorder according to Android doc
        stopRecording()
    }
}
