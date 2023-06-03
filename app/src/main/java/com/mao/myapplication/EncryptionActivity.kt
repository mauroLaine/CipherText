package com.mao.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mao.myapplication.databinding.ActivityEncryptionBinding

class EncryptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEncryptionBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEncryptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}