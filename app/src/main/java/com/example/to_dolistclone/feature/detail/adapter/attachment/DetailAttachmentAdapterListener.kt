package com.example.to_dolistclone.feature.detail.adapter.attachment

import com.example.to_dolistclone.core.domain.model.Attachment

interface DetailAttachmentAdapterListener {

    fun openFile(attachment: Attachment)
    fun deleteAttachment(attachment: Attachment)

}