package uz.futuresoft.sendsms

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.telephony.SmsManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import uz.futuresoft.sendsms.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.message.addTextChangedListener {
            binding.sendMessage.isEnabled = true
        }
        binding.sendMessage.setOnClickListener {
            sendMessage()
        }
    }

    // Nomalum sabablarga ko`ra permission ni tekshirmayapti
    private fun checkPermission() {
        Dexter.withContext(this)
            .withPermission(Manifest.permission.SEND_SMS)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse?) {
                    sendMessage()
                }

                override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse?) {
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", packageName, "")
                    intent.data = uri
                    startActivity(intent)
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissionRequest: PermissionRequest?,
                    permissionToken: PermissionToken?,
                ) {
                    permissionToken!!.continuePermissionRequest()
                }
            })
    }

    private fun sendMessage() {
        val message = binding.message.text.toString().trim()
        val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSystemService(SmsManager::class.java)
        } else {
            SmsManager.getDefault()
        }
        smsManager.sendTextMessage("${binding.phoneNumber.text}", null, message, null, null)
        Toast.makeText(this, "Sending...", Toast.LENGTH_SHORT).show()
        binding.sendedMessage.text = binding.message.text.toString().trim()
        binding.message.text.clear()
    }
}