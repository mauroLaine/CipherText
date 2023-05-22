package com.mao.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class CameraActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // When using Latin script library
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)



    }
}