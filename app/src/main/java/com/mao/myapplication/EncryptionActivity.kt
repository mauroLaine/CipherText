package com.mao.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.mao.myapplication.databinding.ActivityEncryptionBinding

class EncryptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEncryptionBinding

    private lateinit var cypherViewModel: CypherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEncryptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up ViewModel
        cypherViewModel = ViewModelProvider(this)[CypherViewModel::class.java]

        val intent = intent
        val value = intent.getStringExtra(MainActivity.MESSAGE)
        binding.encryptionTextView.text = value

        binding.buttonYes.setOnClickListener{
            value?.let {
                encryptInput(value)
            }
        }

        binding.buttonNo.setOnClickListener{
            finish()
        }
    }


    private fun encryptInput(encryptionInput: String) {
        cypherViewModel.encryptInputText(encryptionInput)
    }

    private fun decryptInput() {
        cypherViewModel.decryptInputText()
    }
}