package com.artf.inventoryapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val INITIAL_REQUEST = 1337
    private val INITIAL_PERMS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissions()
    }

    private fun startApp(){
        setContentView(R.layout.activity_main)
    }

    private fun checkPermissions() {
        if (!checkIfAlreadyHavePermission()) {
            requestForSpecificPermission()
        } else {
            startApp()
        }
    }

    private fun checkIfAlreadyHavePermission(): Boolean {
        var result = false
        for (i in INITIAL_PERMS) {
            if (ContextCompat.checkSelfPermission(this, i) == PackageManager.PERMISSION_GRANTED) {
                result = true
            } else {
                return false
            }
        }
        return result
    }

    private fun requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, INITIAL_PERMS, INITIAL_REQUEST)
    }

    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        when (requestCode) {
            INITIAL_REQUEST -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startApp()
            } else {
                Toast.makeText(this, "Permissions are necessary", Toast.LENGTH_SHORT).show()
                Handler().postDelayed({ checkPermissions() }, 1500)
            }
        }
    }

}
