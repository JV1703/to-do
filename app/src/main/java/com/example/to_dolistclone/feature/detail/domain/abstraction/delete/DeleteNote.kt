package com.example.to_dolistclone.feature.detail.domain.abstraction.delete

interface DeleteNote {
    suspend operator fun invoke(noteId: String): Int
}