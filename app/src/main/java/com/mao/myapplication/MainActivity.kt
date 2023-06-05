package com.mao.myapplication

import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
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
        const val MESSAGE = "Message"
        const val MODE = "Mode"
        const val ENCRYPTION_MODE = "Encryption"
        const val DECRYPTION_MODE = "Decryption"
    }

    private lateinit var binding: ActivityMainBinding

    private val cameraActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val savedUriString = result.data?.getStringExtra("savedUri")
                if (savedUriString != null) {
                    // When using Latin script library
                    val recognizer =
                        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
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
//                                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()

                                val mode = getCypherMode()
                                val intent = Intent(this, EncryptionActivity::class.java)
                                intent.putExtra(MESSAGE, resultText)
                                intent.putExtra(MODE, mode)
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

        if (!areNotificationsEnabled()) {
            showNotificationPermissionDialog();
        }
    }

    private fun areNotificationsEnabled(): Boolean {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.areNotificationsEnabled()
    }

    private fun showNotificationPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Notifications Permission")
            .setMessage("For optimal use of this application, please enable notifications.")
            .setPositiveButton("Enable", DialogInterface.OnClickListener { dialog, which ->
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                startActivity(intent)
            })
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun getCypherMode(): String? {
        val encryptionRadio = binding.radioEncryption.isChecked
        val decryptionRadio = binding.radioDecryption.isChecked
        if (encryptionRadio) {
            return ENCRYPTION_MODE
        } else if (decryptionRadio) {
            return DECRYPTION_MODE
        }
        return null
    }
}