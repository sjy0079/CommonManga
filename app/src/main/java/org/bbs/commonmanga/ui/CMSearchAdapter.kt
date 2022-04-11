package org.bbs.commonmanga.ui

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class CMSearchAdapter : RecyclerView.Adapter<CMSearchVH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CMSearchVH {
        val itemView = LinearLayout(parent.context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            addView(CardView(context).apply {
                radius = 16F
                elevation = 16F
                addView(AppCompatImageView(context).apply {

                })
            })
        }
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: CMSearchVH, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }
}

class CMSearchVH(view: View, val cover: ImageView, val titleTv: TextView, val authorTv: TextView) :
    RecyclerView.ViewHolder(view)