package com.mao.myapplication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.mao.myapplication.databinding.ActivityMainBinding
import java.io.IOException

class MainActivity : AppCompatActivity() {
    companion object {
        private const val CAMERA_REQUEST_CODE = 1
        const val MESSAGE = "Message"
    }

    private lateinit var binding: ActivityMainBinding

    private val cameraActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val savedUriString = result.data?.getStringExtra("savedUri")
                if (savedUriString != null) {
                    // When using Latin script library
                    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                    val savedUri = Uri.parse(savedUriString)

                    val image: InputImage
                    try {
                        image = InputImage.fromFilePath(baseContext, savedUri)
                        val result = recognizer.process(image)
                            .addOnSuccessListener { visionText ->

                                val resultText = visionText.text
                                // TODO: use these blocks to insert them to the encryption
                                for (block in visionText.textBlocks) {
                                    val blockText = block.text
                                    val blockCornerPoints = block.cornerPoints
                                    val blockFrame = block.boundingBox
                                    for (line in block.lines) {
                                        val lineText = line.text
                                        val lineCornerPoints = line.cornerPoints
                                        val lineFrame = line.boundingBox
                                        for (element in line.elements) {
                                            val elementText = element.text
                                            val elementCornerPoints = element.cornerPoints
                                            val elementFrame = element.boundingBox
                                        }
                                    }
                                }
                                val msg = "The text found was ${resultText}"
                                Log.d("Andas", msg)
                                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, EncryptionActivity::class.java)
                                intent.putExtra(MESSAGE, msg)
                                startActivity(intent)
                            }
                            .addOnFailureListener { e ->
                                val msg = "No text was found in this image"
                                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                            }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnTakePicture.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            cameraActivityResult.launch(intent)
        }
    }
}