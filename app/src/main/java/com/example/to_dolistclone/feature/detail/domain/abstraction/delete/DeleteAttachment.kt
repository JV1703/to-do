package com.example.to_dolistclone.feature.detail.domain.abstraction.delete

interface DeleteAttachment {
    suspend operator fun invoke(attachmentId: String): Int
}