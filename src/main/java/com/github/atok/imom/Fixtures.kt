package com.github.atok.imom

import com.github.atok.imom.model.Note
import com.github.atok.imom.repository.Storage
import kotlin.collections.hashSetOf
import kotlin.collections.listOf

public object Fixtures {
    fun initData(storage: Storage) {
        val notes = listOf(
                Note("Item 1", "Note1", hashSetOf("tag1")),
                Note("Item 0", "<h1>Note 0</h1>", hashSetOf()),
                Note("Item 2", "Item2", hashSetOf("tag1")),
                Note("Html note", "HTML", hashSetOf("tag1", "tag2"))
        )

        storage.addAll(notes)
    }
}