package com.eokoe.sagui.features.help

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.eokoe.sagui.R
import com.eokoe.sagui.features.base.view.RecyclerViewAdapter
import kotlinx.android.synthetic.main.item_help.view.*

/**
 * @author Pedro Silva
 * @since 12/10/17
 */
class HelpAdapter : RecyclerViewAdapter<HelpAdapter.Item, HelpAdapter.ItemViewHolder>() {

    var onItemClickListener: (ItemType) -> Unit = {}

    init {
        val items = ArrayList<Item>()
        items.add(Item("Dúvidas frequentes", ItemType.FAQ))
        items.add(Item("Contato", ItemType.CONTACT))
        items.add(Item("Sobre", ItemType.ABOUT))
        this.items = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ItemViewHolder(inflate(R.layout.item_help, parent))

    override fun onBindViewHolder(holder: HelpAdapter.ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            itemView.setOnClickListener {
                if (adapterPosition >= 0) {
                    onItemClickListener(getItem(adapterPosition).type)
                }
            }
        }

        fun bind(item: Item) {
            itemView.tvTitle.text = item.title
        }
    }

    class Item(val title: String, val type: ItemType)

    enum class ItemType {
        FAQ, CONTACT, ABOUT
    }
}