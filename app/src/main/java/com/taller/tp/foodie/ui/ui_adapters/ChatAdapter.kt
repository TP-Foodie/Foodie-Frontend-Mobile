package com.taller.tp.foodie.ui.ui_adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ChatMessage
import kotlinx.android.synthetic.main.item_chat_message_sent.view.*
import java.util.*

class ChatAdapter(private val listaMensajes: List<ChatMessage>, private val my_uid: String) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    companion object {
        const val MESSAGE_SENT = 0
        const val MESSAGE_RECEIVED = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view: View = if (viewType == MESSAGE_SENT) {
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_chat_message_sent,
                parent,
                false
            )
        } else {
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_chat_message_received,
                parent,
                false
            )
        }
        return ChatViewHolder(view)
    }

    override fun getItemCount(): Int = listaMensajes.size

    override fun getItemViewType(position: Int): Int {
        if (listaMensajes[position].uid_sender == my_uid)
            return MESSAGE_SENT
        return MESSAGE_RECEIVED
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val mensaje = listaMensajes[position]

        // format for message time
        val cal = Calendar.getInstance()
        cal.timeInMillis = mensaje.timestamp
        val messageTime =
            cal.get(Calendar.HOUR_OF_DAY).toString() + ":" + cal.get(Calendar.MINUTE).toString()

        holder.messageTime.text = messageTime
        holder.message.text = mensaje.message

        // get previous message data -> for showing or not margin between messages
        if (position < itemCount - 1) {
            val dataMensajePrevio = listaMensajes[position + 1]
            if (dataMensajePrevio.uid_sender != mensaje.uid_sender) {
                holder.margin.visibility = View.VISIBLE
            }
        }
    }

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var message: TextView = view.message
        var messageTime: TextView = view.message_time
        var margin: TextView = view.margin
    }
}