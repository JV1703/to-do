package com.example.to_dolistclone.feature.detail.domain.abstraction.create_update

import com.example.to_dolistclone.core.domain.model.Attachment

interface InsertAttachment {
    suspend operator fun invoke(attachment: Attachment): Long
}