package com.github.atok.imom.repository

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.github.atok.imom.Fixtures
import com.github.atok.imom.model.Note
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.time.ZonedDateTime
import kotlin.collections.arrayListOf
import kotlin.collections.toString
import kotlin.text.toByteArray

@JsonIgnoreProperties("objectMapper", "file")
public class Storage {

    val notes: MutableList<Note> = arrayListOf()

    @Transient private val objectMapper = createMapper()
    @Transient private var file = File("data/notes.yaml")

    private val path: Path
        get() = file.toPath()

    public fun allNotes(): List<Note> {
        return notes
    }

    public fun addAll(addedNotes: List<Note>){
        notes.addAll(addedNotes)
    }

    public fun save() {
        val text = objectMapper.writeValueAsString(this)
        if(!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
        Files.write(path, text.toByteArray(Charsets.UTF_8))
    }

    private fun createMapper(): ObjectMapper {
        val mapper = ObjectMapper(YAMLFactory())
        val module = SimpleModule()
        module.addSerializer(ZonedDateTime::class.java, object : JsonSerializer<ZonedDateTime>() {
            override fun serialize(value: ZonedDateTime, jsonGenerator: JsonGenerator, p2: SerializerProvider) {
                jsonGenerator.writeString(value.toString())
            }
        })
        module.addDeserializer(ZonedDateTime::class.java, object : JsonDeserializer<ZonedDateTime>() {
            override fun deserialize(p0: JsonParser, p1: DeserializationContext): ZonedDateTime {
                val text = p0.readValueAs(String::class.java)
                return ZonedDateTime.parse(text)
            }
        })
        mapper.registerModule(module)

        return mapper
    }

    public fun newNote(): Note {
        val note = Note("New note")
        notes.add(note)
        return note
    }

    public fun load(selectFile: File? = file) {
        if(selectFile != null) {
            this.file = selectFile
        }

        if(!Files.exists(path)) {
            println("Initializing with fixtures")
            Fixtures.initData(this)
            return
        }

        val bytes = Files.readAllBytes(path)
        val text = bytes.toString(Charsets.UTF_8)
        val read = objectMapper.readValue(text, Storage::class.java)

        notes.clear()
        notes.addAll(read.notes)
    }
}