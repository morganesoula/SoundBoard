package com.ms.soundboard.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ms.soundboard.adapter.RecordListAdapter
import com.ms.soundboard.R
import com.ms.soundboard.model.Record
import com.ms.soundboard.viewModel.RecordViewModel

class ListRecordFragment : Fragment() {


    /************************************************
        DATA
     ************************************************/

    private lateinit var recordViewModel: RecordViewModel

    companion object {

        fun newInstance() : ListRecordFragment {
            return  ListRecordFragment()
        }
    }


    /************************************************
        METHODS
     ************************************************/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_record, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = activity?.findViewById<RecyclerView>(R.id.listRecords)
        val adapter = RecordListAdapter(activity?.applicationContext!!)

        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = GridLayoutManager(activity?.applicationContext, 3)

        recordViewModel = ViewModelProvider(this).get(RecordViewModel::class.java)

        activity?.applicationContext.let {
            if (it != null) {
                recordViewModel.getAll()
            }
        }

        recordViewModel.allRecords.observe(viewLifecycleOwner, Observer { records ->
            records?.let {
                adapter.setRecords(it)
            }
        })

        adapter.onItemClick = { record ->
            recordViewModel.onMusicNoteClicked(record)
        }
    }



    /************************************************
        LIFECYCLE
     ************************************************/
}
