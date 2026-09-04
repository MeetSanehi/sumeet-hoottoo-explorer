package com.example.smbgallery

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.security.MessageDigest

class Thumbnailer(private val context: Context) {

    private val cacheDir = File(context.cacheDir, "thumbs").apply { mkdirs() }
    private val maxCacheBytes = 1024L * 1024L * 512L // 512MB default; you may reduce for small library

    private fun keyFor(path: String): String {
        val md = MessageDigest.getInstance("MD5")
        md.update(path.toByteArray())
        return md.digest().joinToString("") { "%02x".format(it) }
    }

    suspend fun getOrCreateThumbnail(remotePath: String, inputStreamProvider: suspend () -> InputStream): File =
        withContext(Dispatchers.IO) {
            val key = keyFor(remotePath)
            val outFile = File(cacheDir, "$key.jpg")
            if (outFile.exists()) return@withContext outFile

            // Generate thumbnail (decode small)
            inputStreamProvider().use { ins ->
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = false; inSampleSize = 8 } // moderate downsample
                val bmp = BitmapFactory.decodeStream(ins, null, options)
                val thumb = Bitmap.createScaledBitmap(bmp, 200, (200f * (bmp.height.toFloat()/bmp.width)).toInt(), true)
                FileOutputStream(outFile).use { fos -> thumb.compress(Bitmap.CompressFormat.JPEG, 80, fos) }
                trimCacheIfNeeded()
            }
            return@withContext outFile
        }

    private fun trimCacheIfNeeded() {
        try {
            var total = cacheDir.walkTopDown().filter { it.isFile }.map { it.length() }.sum()
            if (total <= maxCacheBytes) return
            val files = cacheDir.listFiles()?.sortedBy { it.lastModified() } ?: return
            for (f in files) {
                if (total <= maxCacheBytes) break
                total -= f.length()
                f.delete()
            }
        } catch (_: Exception) {}
    }

    fun clearCache() {
        cacheDir.listFiles()?.forEach { it.delete() }
    }
}
