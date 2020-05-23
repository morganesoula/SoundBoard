package com.ms.soundboard.utils

import android.content.Context
import com.ms.soundboard.R
import java.util.concurrent.TimeUnit

object TimeFormatter {

    public fun formatTime(context: Context, millis: Long) : String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60

        return when {
            hours == 0L && minutes == 0L -> String.format(
                context.resources.getString(R.string.time_seconds_formatter, seconds)
            )

            hours == 0L && minutes > 0L -> String.format(
                context.resources.getString(R.string.time_minutes_seconds_formatter, minutes, seconds)
            )

            else -> context.resources.getString(R.string.time_hours_minutes_seconds_formatter, hours, minutes, seconds)
        }
    }
}

