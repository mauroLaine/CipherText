package com.mao.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.mao.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object {
        private const val CAMERA_REQUEST_CODE = 1
    }

    private lateinit var binding: ActivityMainBinding

    private val cameraActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val savedUri = result.data?.getStringExtra("savedUri")
                // Use the savedUri as needed
                if (savedUri != null) {
                    val msg = "the uri was: ${savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // When using Latin script library
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        binding.btnTakePicture.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            cameraActivityResult.launch(intent)
        }
    }
}