package com.yogadimas.tourismapp.core.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yogadimas.tourismapp.core.databinding.ItemRowSuggestionTourismBinding
import com.yogadimas.tourismapp.core.ui.model.TourismUiModel

class TourismSuggestionAdapter(
    private val onItemClick: (TourismUiModel) -> Unit,
) : ListAdapter<TourismUiModel, TourismSuggestionAdapter.SuggestionViewHolder>(DIFF_CALLBACK) {

    inner class SuggestionViewHolder(val binding: ItemRowSuggestionTourismBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: TourismUiModel) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(data.image)
                    .into(ivItemImage)
                tvItemTitle.text = data.name
                tvItemSubtitle.text = data.address
                root.setOnClickListener { onItemClick(data) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SuggestionViewHolder(
            ItemRowSuggestionTourismBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TourismUiModel>() {
            override fun areItemsTheSame(oldItem: TourismUiModel, newItem: TourismUiModel) =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: TourismUiModel, newItem: TourismUiModel) =
                oldItem == newItem
        }
    }
}