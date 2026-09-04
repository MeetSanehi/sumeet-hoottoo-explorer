package com.example.smbgallery

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var adapter: PhotoAdapter
    private lateinit var thumbnailer: Thumbnailer
    private var smbManager: SmbManager? = null

    private val prefs by lazy { getSharedPreferences("smb", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        thumbnailer = Thumbnailer(this)
        recycler = findViewById(R.id.recycler)
        fab = findViewById(R.id.fab_settings)

        recycler.layoutManager = GridLayoutManager(this, 3)
        // init with default values
        val host = prefs.getString("host", "10.10.10.254")!!
        val share = prefs.getString("share", "USB") ?: ""
        val username = prefs.getString("username", "admin")
        val password = prefs.getString("password", null)

        smbManager = SmbManager(host, share, username, password)
        adapter = PhotoAdapter(this, thumbnailer, smbManager!!)
        recycler.adapter = adapter

        fab.setOnClickListener { startActivity(Intent(this, SettingsActivity::class.java)) }

        adapter.onClick = { smbFile ->
            // Open full-resolution stream activity or use an external viewer — simplified:
            Toast.makeText(this, "Tap to implement full-view for ${smbFile.name}", Toast.LENGTH_SHORT).show()
            // Implement streaming viewer that opens InputStream and displays (left as exercise)
        }

        // load list
        loadList()
    }

    override fun onResume() {
        super.onResume()
        // reload if settings may have changed
        loadList()
    }

    private fun loadList() {
        val host = prefs.getString("host", "10.10.10.254")!!
        val share = prefs.getString("share", "USB") ?: ""
        val username = prefs.getString("username", "admin")
        val password = prefs.getString("password", null)
        smbManager = SmbManager(host, share, username, password)
        adapter = PhotoAdapter(this, thumbnailer, smbManager!!)
        recycler.adapter = adapter

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val files = smbManager!!.listFiles("")
                withContext(Dispatchers.Main) { adapter.submitList(files) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Error listing SMB files: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
