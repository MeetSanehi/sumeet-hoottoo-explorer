package com.example.smbgallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import jcifs.smb.SmbFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PhotoAdapter(
    private val context: android.content.Context,
    private val thumbnailer: Thumbnailer,
    private val smbManager: SmbManager
) : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {

    private val items = mutableListOf<SmbFile>()
    var onClick: ((SmbFile)->Unit)? = null

    fun submitList(list: List<SmbFile>) {
        items.clear()
        items.addAll(list.filter { !it.isDirectory })
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val smb = items[position]
        holder.bind(smb)
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val iv: ImageView = view.findViewById(R.id.thumb)
        fun bind(smbFile: SmbFile) {
            // Generate or use cached thumbnail asynchronously
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val thumbFile = thumbnailer.getOrCreateThumbnail(smbFile.path, suspend { smbManager.openFileStream(smbFile.name) })
                    Glide.with(context).load(thumbFile).into(iv)
                } catch (e: Exception) {
                    iv.setImageResource(android.R.drawable.ic_menu_report_image)
                }
            }
            itemView.setOnClickListener { onClick?.invoke(smbFile) }
        }
    }
}
