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
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files

class CypherViewModel(private val applicationContext: Application) :
    AndroidViewModel(applicationContext) {

    fun encryptInputText(text: String): Boolean {
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        val fileToWrite = "my_sensitive_data.txt"
        val file = File(
            applicationContext.getExternalFilesDir(
                Environment.DIRECTORY_DOWNLOADS
            ), fileToWrite
        )
        val result = Files.deleteIfExists(file.toPath())
        Log.d("###", result.toString())
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
            // Check file in /sdcard/Android/data/com.mao.myapplication/files/Download
            createAndDownloadFile(
                applicationContext,
                "my_sensitive_data2.txt",
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

        val fileToRead = "my_sensitive_data.txt"
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
        Log.d("###", message)
        return message
    }

    private fun createAndDownloadFile(context: Context, fileName: String, content: String) {
        // Step 1: Create a file in the external storage directory
        val directory = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val file = File(directory, fileName)
        FileOutputStream(file).use {
            it.write(content.toByteArray())
        }
        // Step 2: Notify the user that the file has been created and downloaded
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