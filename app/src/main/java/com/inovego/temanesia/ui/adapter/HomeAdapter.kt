package com.inovego.temanesia.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.inovego.temanesia.data.model.FeatureItem
import com.inovego.temanesia.databinding.ItemListFeatureLinearBinding
import com.inovego.temanesia.utils.loadImageFromUrl

class HomeAdapter(private val onClick: (FeatureItem) -> Unit) :
    ListAdapter<FeatureItem, HomeAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListFeatureLinearBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val itemPosition = getItem(position)
        holder.bind(itemPosition)
    }

    inner class MyViewHolder(val binding: ItemListFeatureLinearBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(FeatureItem: FeatureItem) {
            binding.apply {
                ivFeatures.loadImageFromUrl(FeatureItem.urlPosterImg)
                tvPillFeatures.text = FeatureItem.jenisKegiatan
                tvPillLembaga.text = FeatureItem.penyelenggara
                tvFeaturesTitle.text = FeatureItem.nama
                tvFeaturesDescriptionSingkat.text = FeatureItem.ringkasan
                tvDate.text = FeatureItem.date.toString()
            }
            binding.root.setOnClickListener {
                onClick(FeatureItem)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<FeatureItem> =
            object : DiffUtil.ItemCallback<FeatureItem>() {
                override fun areItemsTheSame(
                    oldFeatureItem: FeatureItem,
                    newFeatureItem: FeatureItem,
                ): Boolean {
                    return oldFeatureItem.deskripsi == newFeatureItem.deskripsi
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(
                    oldFeatureItem: FeatureItem,
                    newFeatureItem: FeatureItem,
                ): Boolean {
                    return oldFeatureItem == newFeatureItem
                }
            }
    }
}


