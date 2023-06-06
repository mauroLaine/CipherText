package com.mao.myapplication

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import com.mao.myapplication.utils.CHANNEL_ID
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files

class CypherViewModel(private val applicationContext: Application) :
    AndroidViewModel(applicationContext) {
    companion object {
        private const val FILE_NAME = "my_sensitive_data.txt"
    }

    fun encryptInputText(text: String): Boolean {
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(
            path, FILE_NAME
        )
        val result = Files.deleteIfExists(file.toPath())
        Log.d("###", "deleteIfExists: $result")
        try {
            val encryptedFile = EncryptedFile.Builder(
                file,
                applicationContext,
                mainKeyAlias,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()

            val fileContent = text.toByteArray(StandardCharsets.UTF_8)
            encryptedFile.openFileOutput().apply {
                write(fileContent)
                flush()
                close()
            }
            // Check file in  /sdcard/Download or /sdcard/Android/data/com.mao.myapplication/files/Download
            createAndDownloadFile(
                applicationContext,
                FILE_NAME,
                fileContent.toString()
            )
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun decryptInputText(uri: Uri): String {
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        val fileToRead = FILE_NAME
        val encryptedFile = EncryptedFile.Builder(
            File(
                applicationContext.getExternalFilesDir(
                    Environment.DIRECTORY_DOWNLOADS
                ), fileToRead
            ),
            applicationContext,
            mainKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

//        val encryptedFile = EncryptedFile.Builder(
//            File(
//                uri.path
//            ),
//            applicationContext,
//            mainKeyAlias,
//            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
//        ).build()

        val inputStream = encryptedFile.openFileInput()
        val byteArrayOutputStream = ByteArrayOutputStream()
        var nextByte: Int = inputStream.read()
        while (nextByte != -1) {
            byteArrayOutputStream.write(nextByte)
            nextByte = inputStream.read()
        }

        val plaintext: ByteArray = byteArrayOutputStream.toByteArray()
        val message = String(plaintext)
        Log.d("###", "message: $message")
        return message
    }

    private fun createAndDownloadFile(context: Context, fileName: String, content: String) {
        // Notify the user that the file has been created and downloaded
        // Note: You can further customize the notification as per your requirement
        val notificationManager = NotificationManagerCompat.from(context)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.cryptography)
            .setContentTitle("File Downloaded")
            .setContentText("$fileName is downloaded to your device.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(
                applicationContext,
                "Please grant the Notification permission",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        notificationManager.notify(0, notification)
    }
}