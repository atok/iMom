package com.github.atok.imom.model

import com.github.atok.imom.IdGenerator
import java.time.ZonedDateTime
import kotlin.collections.*
import kotlin.text.*

public data class Note(
        var title: String = "",
        var text: String = "",
        val tags: MutableSet<String> = hashSetOf(),
        val date: ZonedDateTime = ZonedDateTime.now(),
        val attachments: MutableList<Attachment> = arrayListOf(),
        override val id: String = IdGenerator.generate()
) : HasId {

    fun textPreview(): String {
        return text
                .lines()
                .map { it.trim() }
                .filter { !it.startsWith("#") && it.length > 1 }
                .take(4)
                .joinToString("")
                .replace("\n", "")
                .replace(this.title, "")
                .take(60)
    }

    fun updateFromText() {
        val tags = this.text.lines()
                .filter { it.length > 0 }
                .filterNotNull()
                .map { it.trim() }
                .filter { it.startsWith("#") }
                .flatMap { it.split(' ') }
                .filter { it.startsWith("#") }
                .map { it.toLowerCase() }

        this.tags.clear()
        this.tags.addAll(tags)

        val title = text.lines().firstOrNull { it.trim().length > 0 && !it.trim().startsWith("#") } ?: "New note"
        this.title = title
    }

}