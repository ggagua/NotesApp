package com.example.notesapp

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notesapp.data.Note
import com.example.notesapp.databinding.ActivityMainBinding
import com.example.notesapp.databinding.DialogAddNoteBinding
import com.example.notesapp.ui.NoteAdapter
import com.example.notesapp.ui.NoteViewModel
import java.util.Locale

/**
 * VIEW layer of MVVM.
 * Only responsible for showing data and forwarding user actions to the ViewModel.
 * Uses ViewBinding everywhere - findViewById is never called.
 */
class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: NoteViewModel by viewModels()
    private lateinit var adapter: NoteAdapter
    private lateinit var textToSpeech: TextToSpeech

    private var currentNotes: List<Note> = emptyList()
    private var sortByTitle = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // New feature: Text-to-Speech engine, reads notes out loud
        textToSpeech = TextToSpeech(this, this)

        adapter = NoteAdapter(
            onPlayClick = { note -> speakNote(note) },
            onDeleteClick = { note -> viewModel.deleteNote(note) }
        )

        binding.recyclerViewNotes.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewNotes.adapter = adapter

        binding.fabAddNote.setOnClickListener { showAddNoteDialog() }

        // Observing LiveData coming from the ViewModel (MVVM data flow)
        viewModel.allNotes.observe(this) { notes ->
            currentNotes = notes
            renderList()
        }

        setupMenu()
    }

    private fun setupMenu() {
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_sort_date -> {
                        sortByTitle = false
                        renderList()
                        true
                    }
                    R.id.action_sort_title -> {
                        sortByTitle = true
                        renderList()
                        true
                    }
                    R.id.action_about -> {
                        showAboutDialog()
                        true
                    }
                    else -> false
                }
            }
        })
    }

    private fun renderList() {
        val sorted = if (sortByTitle) {
            currentNotes.sortedBy { it.title.lowercase() }
        } else {
            currentNotes.sortedByDescending { it.timestamp }
        }
        adapter.submitList(sorted)
        binding.textEmpty.visibility = if (sorted.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showAddNoteDialog() {
        val dialogBinding = DialogAddNoteBinding.inflate(LayoutInflater.from(this))
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.add_note))
            .setView(dialogBinding.root)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                val title = dialogBinding.editTitle.text.toString().trim()
                val content = dialogBinding.editContent.text.toString().trim()
                viewModel.addNote(title, content)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun showAboutDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.about_title))
            .setMessage(getString(R.string.about_message))
            .setPositiveButton(getString(R.string.ok), null)
            .show()
    }

    private fun speakNote(note: Note) {
        val textToRead = "${note.title}. ${note.content}"
        textToSpeech.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, note.id.toString())
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.language = Locale.getDefault()
        }
    }

    override fun onDestroy() {
        textToSpeech.stop()
        textToSpeech.shutdown()
        super.onDestroy()
    }
}
