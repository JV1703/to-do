package com.example.to_dolistclone.core.common

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import com.example.to_dolistclone.core.di.coroutine_dispatchers.CoroutinesQualifiers
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URLConnection
import javax.inject.Inject


class FileManager @Inject constructor(
    @ApplicationContext private val context: Context,
    @CoroutinesQualifiers.IoDispatcher private val dispatcherIO: CoroutineDispatcher
) {

    init {
        if (!isDirectoryAvailable(context.filesDir.path + File.separatorChar + "attachments")) {
            createDirectory(context.filesDir.path + File.separatorChar + "attachments")
        }
        Log.i("FileManager", "external path my_images:${context.getExternalFilesDir("my_images")}")
        Log.i("FileManager", "external path images:${context.getExternalFilesDir("images")}")
    }

    fun getFile(uri: Uri): File {

        val fileName = queryName(uri)
        val extension = getExtension(uri)
        val targetDestination = if (extension != null) {
            context.filesDir.path + File.separatorChar + "attachments" + File.separatorChar + extension
        } else {
            context.filesDir.path + File.separatorChar + "attachments" + File.separatorChar + "misc"
        }

        if (!isDirectoryAvailable(targetDestination)) {
            createDirectory(targetDestination)
        }

        val destinationFilename = File(targetDestination + File.separatorChar + fileName)

        try {
            context.contentResolver.openInputStream(uri).use { ins ->
                ins?.let {
                    createFileFromStream(
                        ins, destinationFilename
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("FileManager", "saveFile - errorMsg: ${e.message}")
            e.printStackTrace()
        }
        return destinationFilename
    }

    fun createFileFromStream(ins: InputStream, destination: File?) {
        try {
            FileOutputStream(destination).use { os ->
                val buffer = ByteArray(4096)
                var length: Int
                while (ins.read(buffer).also { length = it } > 0) {
                    os.write(buffer, 0, length)
                }
                os.flush()
            }
        } catch (e: Exception) {
            Log.e("FileManager", "createFileFromStream - errorMsg: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun queryName(uri: Uri): String {
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

    private fun createDirectory(filePath: String): Boolean {
        return try {
            val file = File(filePath)
            file.mkdir()
            Log.i("FileManager", "createDirectory - dirName: $filePath")
            true
        } catch (e: Exception) {
            Log.e("FileManager", "createDirectory - errorMsg: ${e.message}")
            false
        }
    }

    private fun isDirectoryAvailable(path: String): Boolean {
        val directory = File(path)
        return directory.exists()
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