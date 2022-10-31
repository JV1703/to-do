package com.example.to_dolistclone.feature.detail.domain.abstraction.create_update

import com.example.to_dolistclone.core.domain.model.Attachment

interface InsertAttachments{
    suspend operator fun invoke(attachments: List<Attachment>): LongArray
}