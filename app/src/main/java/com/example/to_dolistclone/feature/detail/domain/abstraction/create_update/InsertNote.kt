package com.example.to_dolistclone.feature.detail.domain.abstraction.create_update

import com.example.to_dolistclone.core.domain.model.Note

interface InsertNote{
    suspend operator fun invoke(note: Note): Long
}