package com.taller.tp.foodie.ui.ui_adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ProductFetched
import kotlinx.android.synthetic.main.item_product.view.*

class ProductsAdapter(private val productsList: List<ProductFetched>) :
    RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_product,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = productsList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productsList[position]

        holder.name.text = product.name
        holder.description.text = product.description
        val price = product.price
        holder.price.text = "$ $price"
        holder.image.setImageURI(product.image)
    }

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.product_name
        var description: TextView = view.product_description
        var image: SimpleDraweeView = view.product_image
        var price: TextView = view.product_price
    }
}