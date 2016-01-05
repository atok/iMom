package com.github.atok.imom

import com.github.atok.imom.model.Note
import com.github.atok.imom.repository.Storage
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventHandler
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.stage.FileChooser
import javafx.stage.Stage
import javafx.util.Callback
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.collections.filter
import kotlin.collections.firstOrNull
import kotlin.collections.joinToString
import kotlin.text.contains
import kotlin.text.removePrefix
import kotlin.text.startsWith
import kotlin.text.toLowerCase

public class MainController {

    lateinit var noteListView: TableView<Note>
    lateinit var queryTextField: TextField

    lateinit var newButton: Button
    lateinit var clearButton: Button
    lateinit var loadButton: Button

    lateinit var stage: Stage

    lateinit var storage: Storage
    var query: String = ""

    fun init(stage: Stage, storage: Storage) {
        this.storage = storage
        this.stage = stage

        setupList()

        queryTextField.textProperty().addListener { obs, old, new ->
            show(new.toLowerCase())
        }

        newButton.onAction = EventHandler { event ->
            newNote()
        }

        clearButton.onAction = EventHandler { event ->
            queryTextField.text = ""
        }

        loadButton.onAction = EventHandler { event ->
            selectNoteFile()
        }
    }

    fun setupList() {
        val nameColumn = TableColumn<Note, String>()
        nameColumn.text = "Title"
        nameColumn.cellValueFactory = Callback { features ->
            SimpleStringProperty(features.value.title)
        }

        val tagsColumn = TableColumn<Note, String>()
        tagsColumn.text = "Tags"
        tagsColumn.cellValueFactory = Callback { features ->
            SimpleStringProperty(features.value.tags.joinToString(" "))
        }

        val dateColumn = TableColumn<Note, String>()
        dateColumn.text = "Date"
        dateColumn.cellValueFactory = Callback { features ->

            SimpleStringProperty(features.value.date.format(
                    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
            ))
        }

        val previewColumn = TableColumn<Note, String>()
        previewColumn.text = "Content"
        previewColumn.cellValueFactory = Callback { features ->
            SimpleStringProperty(features.value.textPreview())
        }


        noteListView.columns.setAll(nameColumn, tagsColumn, dateColumn, previewColumn)

        noteListView.rowFactory = Callback { tableView ->
            val row = TableRow<Note>()
            row.onMouseClicked = EventHandler { event ->
                if(event.clickCount == 2 && !row.isEmpty) {
                    openNote(row.item)
                }
            }
            row
        }

        noteListView.onKeyPressed = EventHandler { event ->
            if(event.code == KeyCode.ENTER) {
                val item = noteListView.selectionModel.selectedItem
                if(item != null) openNote(item)
            }
        }

        noteListView.focusedProperty().addListener { obs ->
            show()
        }
    }

    fun selectNoteFile() {
        val fileChooser = FileChooser()
        fileChooser.title = "Open Resource File";
        fileChooser.extensionFilters.addAll(
                FileChooser.ExtensionFilter("Text Files", "*.yaml")
        )
        val selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            storage.load(selectedFile)
            show()
        }
    }

    fun newNote() {
        val note = storage.newNote()
        openNote(note)
    }

    fun show(query: String = this.query) {
        this.query = query

        noteListView.selectionModel.clearSelection()

        val notes = if(query.length == 0) {
            storage.allNotes()
        } else {
            if(query.startsWith("#")) {
                val tag = "#" + query.removePrefix("#")
                storage.allNotes().filter { it.tags.firstOrNull { it.startsWith(tag) } != null }
            } else {
                storage.allNotes().filter { it.text.toLowerCase().contains(query) || it.title.toLowerCase().contains(query) }
            }
        }

        noteListView.items.setAll(notes)
    }

    fun openNote(note: Note) {
        val noteController = NoteController.show(storage)
        noteController.show(note)
    }

}
