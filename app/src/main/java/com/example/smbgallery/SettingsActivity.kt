package com.example.smbgallery

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity: AppCompatActivity() {
    private val prefs by lazy { getSharedPreferences("smb", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val etHost: EditText = findViewById(R.id.et_host)
        val etShare: EditText = findViewById(R.id.et_share)
        val etUser: EditText = findViewById(R.id.et_username)
        val etPass: EditText = findViewById(R.id.et_password)
        val btnSave: Button = findViewById(R.id.btn_save)
        val btnClear: Button = findViewById(R.id.btn_clear_cache)

        etHost.setText(prefs.getString("host", "10.10.10.254"))
        etShare.setText(prefs.getString("share", "USB"))
        etUser.setText(prefs.getString("username", "admin"))

        btnSave.setOnClickListener {
            prefs.edit().putString("host", etHost.text.toString())
                .putString("share", etShare.text.toString())
                .putString("username", etUser.text.toString())
                .putString("password", etPass.text.toString()).apply()
            finish()
        }
        btnClear.setOnClickListener {
            Thumbnailer(this).clearCache()
        }
    }
}
