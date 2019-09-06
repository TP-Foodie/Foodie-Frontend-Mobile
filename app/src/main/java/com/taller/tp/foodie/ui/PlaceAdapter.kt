package com.taller.tp.foodie.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.Place
import kotlinx.android.synthetic.main.place_list_item.view.*

class PlaceAdapter(
    val places: ArrayList<Place>,
    val placeChoice: Context,
    val onClickListener: (Place) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(placeChoice).inflate(R.layout.place_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return places.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(places.get(position), onClickListener)
    }

}

class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(place: Place, clickListener: (Place) -> Unit) {
        itemView.place_item_name.text = place.name
        itemView.setOnClickListener { clickListener(place)}
    }
}
