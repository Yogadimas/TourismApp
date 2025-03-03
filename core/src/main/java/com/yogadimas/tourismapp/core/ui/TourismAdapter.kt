package com.yogadimas.tourismapp.core.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yogadimas.tourismapp.core.databinding.ItemRowTourismBinding
import com.yogadimas.tourismapp.core.ui.model.TourismUiModel

class TourismAdapter : ListAdapter<TourismUiModel, TourismAdapter.ListViewHolder>(DIFF_CALLBACK) {

    var onItemClick: ((TourismUiModel) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ListViewHolder(
            ItemRowTourismBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }

    inner class ListViewHolder(private var binding: ItemRowTourismBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: TourismUiModel) {
            Glide.with(itemView.context)
                .load(data.image)
                .into(binding.ivItemImage)
            binding.tvItemTitle.text = data.name
            binding.tvItemSubtitle.text = data.address
        }

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(getItem(bindingAdapterPosition))
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<TourismUiModel> =
            object : DiffUtil.ItemCallback<TourismUiModel>() {
                override fun areItemsTheSame(
                    oldItem: TourismUiModel,
                    newItem: TourismUiModel,
                ): Boolean {
                    return oldItem.tourismId == newItem.tourismId
                }

                override fun areContentsTheSame(
                    oldItem: TourismUiModel,
                    newItem: TourismUiModel,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}