package com.mao.myapplication

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
        val value = intent.getStringExtra(MainActivity.MESSAGE)
        binding.encryptionTextView.text = value

        binding.buttonYes.setOnClickListener {
            value?.let {
                encryptInput(value)
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

    private fun decryptInput() {
        cypherViewModel.decryptInputText()
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