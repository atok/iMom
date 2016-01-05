package com.github.atok.imom

import com.github.atok.imom.model.Note
import com.github.atok.imom.repository.Storage
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Hyperlink
import javafx.scene.control.TextArea
import javafx.scene.input.KeyCode
import javafx.scene.layout.BorderPane
import javafx.scene.text.TextFlow
import javafx.stage.Stage
import java.util.regex.Pattern
import kotlin.collections.first
import kotlin.text.lines
import kotlin.text.removePrefix
import kotlin.text.split
import kotlin.text.startsWith

class NoteController {

    lateinit var storage: Storage
    lateinit var root: BorderPane
    lateinit var noteTextArea: TextArea
    lateinit var clearButton: Button
    lateinit var stage: Stage
    lateinit var suggestionFrame: TextFlow

    var shownNote: Note? = null

    fun init(stage: Stage) {
        this.stage = stage

        noteTextArea.setOnMouseClicked { event ->
            if(event.clickCount == 3) {
                val link = noteTextArea.selectedText
                if(link.startsWith("http://") || link.startsWith("https://"))
                Main.hostServices.showDocument(link)
            }
        }

        noteTextArea.onKeyPressed = EventHandler { event ->
            if(event.isControlDown && event.code == KeyCode.ENTER) {
                val suggestionLinks = suggestionFrame.children
                if(suggestionLinks.size > 0) {
                    suggestionLinks.first().requestFocus()
                }
            }
        }

        noteTextArea.textProperty().addListener { observableValue, old, new ->
            Platform.runLater {
                if(new.length > 0) {
                    var position = noteTextArea.caretPosition
                    if(position > 0) {

                        var currentLine: String? = null
                        var cnt = 0
                        for(line in new.lines()) {
                            cnt += line.length + 1
                            if(cnt > position) {
                                currentLine = line
                                break
                            }
                        }

                        var currentWord: String? = null
                        var currentWordPosition: Int? = null
                        var cnt2 = 0
                        for(word in new.split(Pattern.compile("\\s+"))) {
                            cnt2 += word.length + 1
                            if(cnt2 > position) {
                                currentWord = word
                                currentWordPosition = position - cnt2 + word.length + 1
                                break
                            }
                        }

                        onEdit(currentLine, currentWord, position, currentWordPosition)

                        println("Line: $currentLine\nWord: $currentWord")


                    }
                }
            }


        }
    }

    fun onEdit(line: String?, word: String?, caretPosition: Int, positionInWord: Int?) {
        if(word != null && positionInWord != null) {
            println("PosInWord: $positionInWord")

            val suggestedWord = word + "!!"
            val missingPart = suggestedWord.removePrefix(word)

            val link = Hyperlink(suggestedWord)
            link.onAction = EventHandler {
                noteTextArea.insertText(caretPosition + word.length - positionInWord, missingPart)
            }
            suggestionFrame.children.setAll(link)
        }
    }

    fun save() {
        println("save")
        shownNote?.updateFromText()
        storage.save()
    }

    fun show(note: Note) {
        this.shownNote = note

        stage.title = note.title

        noteTextArea.text = note.text
        noteTextArea.textProperty().addListener { obs, old, new ->
            note.text = new
        }
    }

    companion object {
        fun show(storage: Storage): NoteController {
            val stage = Stage()

            val loader = FXMLLoader(this.javaClass.getResource("/item2.fxml"))
            val root = loader.load<Parent>()
            val controller = loader.getController<NoteController>()
            controller.storage = storage
            controller.init(stage)

            val scene = Scene(root, 800.0, 600.0)
            scene.onKeyPressed = EventHandler { event ->
                if(event.code == KeyCode.ESCAPE) {
                    Platform.runLater {
                        stage.close()
                        controller.save()
                    }
                }
            }

            stage.scene = scene

            stage.show()
            stage.setOnCloseRequest { event ->
                controller.save()
            }

            return controller
        }
    }
}
