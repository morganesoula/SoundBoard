package com.ms.soundboard.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.ms.soundboard.model.Record
import com.ms.soundboard.R
import com.ms.soundboard.utils.TimeFormatter

class RecordListAdapter(context: Context) :
        RecyclerView.Adapter<RecordListAdapter.ViewHolder>() {

        private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
        private var records = emptyList<Record>()

        var onItemClick : ((Record) -> Unit?)? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val itemView = layoutInflater.inflate(R.layout.item_record_list, parent, false)
                return ViewHolder(itemView)
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                val record = records[position]

                holder.imageRecord.background = holder.imageRecord.resources.getDrawable(R.drawable.ic_music_note_black_75dp, holder.imageRecord.context?.theme)
                holder.titleRecord?.text = record.title
                holder.timeRecord?.text = TimeFormatter.formatTime(holder.timeRecord.context, record.time)
        }

        internal fun setRecords(records : List<Record>) {
                this.records = records
                notifyDataSetChanged()
        }

        override fun getItemCount(): Int = records.size

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                var imageRecord = itemView.findViewById<ImageButton>(R.id.recordImageButton)
                val titleRecord = itemView.findViewById<TextView>(R.id.titleRecordTextView)
                val timeRecord = itemView.findViewById<TextView>(R.id.timeRecordTextView)

                init {
                    itemView.setOnClickListener {
                            onItemClick?.invoke(records[adapterPosition])
                    }
                        imageRecord.setOnClickListener {
                                onItemClick?.invoke(records[adapterPosition])
                        }
                }
        }

}