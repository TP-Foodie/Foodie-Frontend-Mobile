package com.taller.tp.foodie.ui.ui_adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.OrderedProduct
import com.taller.tp.foodie.model.ProductFetched
import com.taller.tp.foodie.ui.ClickListener
import kotlinx.android.synthetic.main.item_product.view.*
import java.lang.ref.WeakReference

class ProductsAdapter(
    private val productsList: List<ProductFetched>,
    private val orderedProducts: HashMap<String, OrderedProduct>,
    val listener: WeakReference<ClickListener>
) :
    RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_product,
                parent,
                false
            ), listener
        )
    }

    override fun getItemCount(): Int = productsList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productsList[position]
        val orderedProduct = orderedProducts[product.id]

        holder.name.text = product.name
        holder.description.text = product.description
        val price = product.price
        holder.price.text = "$ $price"
        holder.image.setImageURI(product.image)

        if (orderedProduct?.quantity == 0) {
            holder.quantity.visibility = View.INVISIBLE
            holder.btnDecrementQuantity.visibility = View.INVISIBLE
        } else {
            holder.quantity.visibility = View.VISIBLE
            holder.btnDecrementQuantity.visibility = View.VISIBLE
            holder.quantity.text = orderedProduct?.quantity.toString()
        }
    }

    class ProductViewHolder(view: View, listener: WeakReference<ClickListener>) :
        RecyclerView.ViewHolder(view) {
        var name: TextView = view.product_name
        var description: TextView = view.product_description
        var image: SimpleDraweeView = view.product_image
        var price: TextView = view.product_price
        var quantity: TextView = view.product_quantity

        private val btnIncrementQuantity: TextView = view.btn_increment_quantity
        val btnDecrementQuantity: TextView = view.btn_decrement_quantity
        var listener: WeakReference<ClickListener>? = null

        init {
            this.listener = listener

            btnIncrementQuantity.setOnClickListener {
                this.listener?.get()?.onIncrementQuantity(adapterPosition)
            }

            btnDecrementQuantity.setOnClickListener {
                this.listener?.get()?.onDecrementQuantity(adapterPosition)
            }
        }
    }
}