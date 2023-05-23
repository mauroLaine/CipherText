package com.mao.myapplication

import android.app.Application
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import java.io.File
import java.nio.charset.StandardCharsets

class CypherViewModel(private val applicationContext: Application) :
    AndroidViewModel(applicationContext) {

    fun cypherText(text: String) {
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        val fileToWrite = "my_sensitive_data.txt"
        val encryptedFile = EncryptedFile.Builder(
            File(
                applicationContext.getExternalFilesDir(
                    Environment.DIRECTORY_DOCUMENTS
                ), fileToWrite
            ),
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
    }
}