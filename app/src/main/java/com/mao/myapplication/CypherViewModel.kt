package com.mao.myapplication

import android.app.Application
import android.os.Environment
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.Exception
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
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun decryptInputText() {
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        val fileToRead = "my_sensitive_data.txt"
        val encryptedFile = EncryptedFile.Builder(
            File(
                applicationContext.getExternalFilesDir(
                    Environment.DIRECTORY_DOCUMENTS
                ), fileToRead
            ),
            applicationContext,
            mainKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

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
    }
}