package com.example.stuffs

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stuffs.databinding.ActivityMainBinding
import com.example.stuffs.databinding.ItemNoteBinding
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity(), INotesAdapter {
    private lateinit var binding: ActivityMainBinding
    private lateinit var noteBinding: ItemNoteBinding
    private lateinit var viewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        noteBinding = ItemNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.noteRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = NotesAdapter(this, this)
        binding.noteRecyclerView.adapter = adapter


        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(NoteViewModel::class.java)
        viewModel.allNotes.observe(this, Observer { list ->
            list?.let {
                adapter.updateList(it)
            }
        })

    }

    override fun onItemClicked(note: Note) {
        var temp = note
        viewModel.deleteNote(note)
        Snackbar.make(binding.root,"Note Deleted", Snackbar.LENGTH_LONG).setAction("Undo") {
            viewModel.insertNote(temp)
            binding.inputBox.text.clear();
        }.setBackgroundTint(Color.parseColor("#202020")).setActionTextColor(Color.GREEN).setTextColor(Color.LTGRAY).show()
    }

    fun addNote(view: View) {
        val noteText = binding.inputBox.text.toString()
        if (noteText.isNotEmpty()) {
            viewModel.insertNote(Note(noteText))
            binding.inputBox.text.clear();
        } else {
            binding.inputBox.startAnimation(shakeError())
        }
    }

    private fun shakeError(): Animation {
        val shake = TranslateAnimation(0f, 10f, 0f, 0f)
        shake.duration = 250
        CycleInterpolator(5F).also { shake.interpolator = it }
        return shake
    }
}