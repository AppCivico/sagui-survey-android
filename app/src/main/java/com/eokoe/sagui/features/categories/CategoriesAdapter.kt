package com.eokoe.sagui.features.categories

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.features.base.view.RecyclerViewAdapter
import kotlinx.android.synthetic.main.item_category.view.*

/**
 * @author Pedro Silva
 * @since 16/08/17
 */
class CategoriesAdapter : RecyclerViewAdapter<Category, RecyclerView.ViewHolder>() {

    private val HEADER_VIEW_TYPE = 0
    private val ITEM_VIEW_TYPE = 1

    var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_VIEW_TYPE) ItemViewHolder(inflate(R.layout.item_category, parent))
        else TextViewHolder(inflate(R.layout.item_header, parent), R.id.title, R.string.choose_category)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == ITEM_VIEW_TYPE) {
            (holder as ItemViewHolder).bind(getItem(position))
        }
    }

    override fun getItemCount() = super.getItemCount() + 1

    override fun getItem(position: Int) = super.getItem(position - 1)

    override fun getItemViewType(position: Int): Int {
        return if (position > 0) ITEM_VIEW_TYPE
        else HEADER_VIEW_TYPE
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(category: Category) {
            itemView.tvCategoryName.text = category.title
            itemView.setOnClickListener {
                onItemClickListener?.onClick(category)
            }
        }
    }

    interface OnItemClickListener {
        fun onClick(category: Category)
    }
}