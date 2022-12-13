package com.example.to_dolistclone.core.common

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import com.example.to_dolistclone.core.di.coroutine_dispatchers.CoroutinesQualifiers
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URLConnection
import javax.inject.Inject


class FileManager @Inject constructor(
    @ApplicationContext private val context: Context,
    @CoroutinesQualifiers.IoDispatcher private val dispatcherIO: CoroutineDispatcher
) {

    init {
        createDefaultDirectory()
    }

    fun generateInternalStorageDestination(uri: Uri): String {
        val fileName = queryName(uri)
        val fileExtension = getExtension(uri)
        val directories = generateInternalStorageDirectories(uri)
        Log.i(
            "FileManager",
            "generateInternalStorageDestination: ${directories + File.separatorChar + fileName}"
        )
        return directories + File.separatorChar + fileName
    }

    private fun generateInternalStorageDirectories(uri: Uri): String {
        val fileExtension = getExtension(uri)
        val mimeType = fileExtension?.let { getMime(it) } ?: "misc"
        val subFolder = mimeType.substringBefore('/')
        return context.filesDir.path + File.separatorChar + "attachments" + File.separatorChar + subFolder
    }

    fun generateNetworkStorageDestination(
        userId: String, storageRef: StorageReference, uri: Uri
    ): String {
        val fileName = queryName(uri)
        val extension = getExtension(uri)
        val mimeType = extension?.let { getMime(it) }
        val subFolder = mimeType?.substringBefore('/') ?: "misc"
        return storageRef.child("$userId/attachments/${subFolder}/${fileName}").path
    }

    fun copyToInternalStorage(originalFileUri: Uri) {
        val fileName = queryName(originalFileUri)
        val directory = createExtensionDirectory(originalFileUri)
        try {
            val file = File(directory, fileName)
            context.contentResolver.openInputStream(originalFileUri).use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream?.copyTo(outputStream)
                }
            }
        } catch (e: Exception) {
            Log.e("FileManager", "copyToInternalStorage - ${e.message}")
        }
    }

    fun queryName(uri: Uri): String {
        val returnCursor: Cursor = context.contentResolver.query(uri, null, null, null, null)!!
        val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name: String = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }

    fun getExtension(uri: Uri): String? {
        return if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            //If scheme is a content
            val mime = MimeTypeMap.getSingleton()
            mime.getExtensionFromMimeType(context.contentResolver.getType(uri))
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            uri.path?.let {
                MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(it)).toString())
            }
        }
    }

    fun getMime(extension: String): String? {
        val mime = MimeTypeMap.getSingleton()
        return mime.getMimeTypeFromExtension(extension)
    }

    fun getMime(uri: Uri): String? {
        return context.contentResolver.getType(uri)
    }

    private fun createDefaultDirectory(): String{
        val defaultDirectoryPath = context.filesDir.path + File.separatorChar + "attachments"
        val defaultDirectory = File(defaultDirectoryPath)
        if(defaultDirectory.exists().not()){
            defaultDirectory.mkdir()
        }
        return defaultDirectoryPath
    }

    private fun createExtensionDirectory(uri: Uri): String{
        val fileExtension = getExtension(uri)
        val mimeType = fileExtension?.let { getMime(it) } ?: "misc"
        val subFolder = mimeType.substringBefore('/')
        val extDirectoryPath =
            context.filesDir.path + File.separatorChar + "attachments" + File.separatorChar + subFolder
        val extensionDirectory = File(extDirectoryPath)

        if(extensionDirectory.exists().not()){
            extensionDirectory.mkdir()
        }

        return extDirectoryPath
    }

    suspend fun deleteFileFromInternalStorage(filePath: String): Boolean {
        return withContext(dispatcherIO) {
            try {
                File(filePath).delete()
                true
            } catch (e: Exception) {
                Log.e("FileManager", "deleteFileFromInternalStorage - errorMsg: ${e.message}")
                false
            }
        }
    }

    private fun isImage(path: String): Boolean {
        val mimeType = URLConnection.guessContentTypeFromName(path)
        return mimeType != null && mimeType.startsWith("image")
    }

    fun fileSizeInKb(file: File): Long {
        return file.length() / 1024
    }

    fun fileSizeInMb(file: File): Long {
        return fileSizeInKb(file) / 1024
    }

    fun getFileSize(file: File): String {
        val kb = fileSizeInKb(file)
        return if (kb >= 1024) {
            "${fileSizeInMb(file)} MB"
        } else {
            "$kb KB"
        }
    }
}