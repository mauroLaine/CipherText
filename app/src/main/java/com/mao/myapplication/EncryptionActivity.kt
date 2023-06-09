package com.mao.myapplication

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.mao.myapplication.databinding.ActivityEncryptionBinding
import com.mao.myapplication.utils.createNotificationChannel


class EncryptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEncryptionBinding

    private lateinit var cypherViewModel: CypherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEncryptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up ViewModel
        cypherViewModel = ViewModelProvider(this)[CypherViewModel::class.java]

        createNotificationChannel(this)

        val intent = intent
        val message = intent.getStringExtra(MainActivity.MESSAGE)
        val mode = intent.getStringExtra(MainActivity.MODE)
        val uriFile = intent.getStringExtra(MainActivity.FILE)

        val actionMessage = if (mode.equals(MainActivity.ENCRYPTION_MODE)) {
            applicationContext.getString(R.string.encryption_question)
        } else if (mode.equals(MainActivity.DECRYPTION_MODE)) {
            applicationContext.getString(R.string.decryption_question)
        } else {
            applicationContext.getString(R.string.encryption_question)
        }
        binding.textView.text = actionMessage
        binding.encryptionTextView.text = message

        binding.buttonYes.setOnClickListener {
            if (mode.equals(MainActivity.ENCRYPTION_MODE)) {
                message?.let {
                    encryptInput(message)
                }
            } else if (mode.equals(MainActivity.DECRYPTION_MODE)) {
                uriFile?.let {
                    decryptInput(uriFile)
                }
            }
        }

        binding.buttonNo.setOnClickListener {
            finish()
        }
    }


    private fun encryptInput(encryptionInput: String) {
        val result = cypherViewModel.encryptInputText(encryptionInput)
        showMessage(result)
    }

    private fun decryptInput(uriFile: String) {
        val uri = Uri.parse(uriFile)
        val result = cypherViewModel.decryptInputText(uri)
        binding.encryptionTextView.text = "Original text: \n\n$result"
    }

    private fun showMessage(result: Boolean) {
        binding.encryptionTextView.visibility = View.GONE
        binding.buttonNo.visibility = View.GONE
        binding.buttonYes.visibility = View.GONE
        val message = if (result) {
            applicationContext.getString(R.string.file_successful)
        } else {
            applicationContext.getString(R.string.file_failure)
        }
        binding.textView.text = message
    }
}