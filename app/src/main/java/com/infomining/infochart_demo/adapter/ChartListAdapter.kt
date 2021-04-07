package com.infomining.infochart_demo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.infomining.infochart_demo.R
import com.infomining.infochart_demo.model.ChartListModel

class ChartListAdapter(
        private val list: MutableList<ChartListModel> = ArrayList(),
        private val listener: OnListItemClickListener
): RecyclerView.Adapter<ChartListAdapter.ItemViewHolder>() {

    interface OnListItemClickListener {
        fun onListItemClick(id: Int)
    }

    private lateinit var context: Context
    private lateinit var inflater: LayoutInflater

    fun setList(list: MutableList<ChartListModel>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    fun addList(item: ChartListModel) {
        this.list.add(item)
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var tvName: TextView = itemView.findViewById(R.id.tv_name)
        var tvDescription: TextView = itemView.findViewById(R.id.tv_description)
        var imgSymbol: ImageView = itemView.findViewById(R.id.img_symbol)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val pos = adapterPosition
            if(pos != RecyclerView.NO_POSITION) {
                listener.onListItemClick(list[pos].id)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        context = parent.context
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view = inflater.inflate(R.layout.item_list, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val data = list[position]

        holder.tvName.text = data.name
        holder.tvDescription.text = data.description
        holder.imgSymbol.setImageDrawable(data.icon)
    }

    override fun getItemCount(): Int {
        return list.size
    }


}