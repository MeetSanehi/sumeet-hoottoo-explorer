package com.example.smbgallery

import jcifs.smb.NtlmPasswordAuthentication
import jcifs.smb.SmbFile
import java.io.InputStream

class SmbManager(private val host: String, private val share: String, private val username: String?, private val password: String?) {

    private fun baseUrl(): String {
        val userPart = "" // include username in URL is optional; we pass auth separately
        return "smb://$host/$share/"
    }

    private fun auth(): NtlmPasswordAuthentication? {
        return if (!username.isNullOrBlank()) {
            NtlmPasswordAuthentication(null, username, password ?: "")
        } else null
    }

    fun listFiles(path: String = ""): List<SmbFile> {
        val url = baseUrl() + path
        val smb = if (auth() != null) SmbFile(url, auth()) else SmbFile(url)
        val arr = smb.listFiles() ?: arrayOf()
        return arr.toList()
    }

    fun openFileStream(path: String): InputStream {
        val url = baseUrl() + path
        val f = if (auth() != null) SmbFile(url, auth()) else SmbFile(url)
        return f.inputStream
    }
}
