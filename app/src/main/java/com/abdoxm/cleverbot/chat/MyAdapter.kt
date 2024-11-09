package com.abdoxm.cleverbot.chat


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abdoxm.cleverbot.R
import com.abdoxm.cleverbot.chat.Constants.BOT_ID
import com.abdoxm.cleverbot.chat.Constants.USER_ID
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.msglist.view.*


class MyAdapter(private val listener: RecycleViewInterface) : RecyclerView.Adapter<MyAdapter.MessageViewHolder>() {

    var messagesList = mutableListOf<Message>()


    @Suppress("DEPRECATION")
    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnLongClickListener{
        init {


            itemView.setOnLongClickListener(this)

        }
            override fun onLongClick(p0: View?): Boolean {

                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    listener.onItemLongClick(position)
                    return true
                }
                return false

            }

        }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {

        return MessageViewHolder(

            LayoutInflater.from(parent.context).inflate(R.layout.msglist, parent, false)

        )


    }

    override fun getItemCount(): Int {
        return messagesList.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val currentItem = messagesList[position]

        when (currentItem.id) {
            USER_ID -> {
                holder.itemView.userText.apply {
                    text = currentItem.message
                    visibility = View.VISIBLE
                }
                holder.itemView.userTime.apply {
                    text = currentItem.time
                    visibility = View.VISIBLE
                }
                holder.itemView.userImg.apply {
                    setImageResource(R.drawable.user_filled_svgrepo_com)
                    visibility = View.VISIBLE
                }
                holder.itemView.botText.visibility = View.GONE
                holder.itemView.botTime.visibility = View.GONE
                holder.itemView.botImg.visibility = View.GONE
            }
            BOT_ID -> {

                holder.itemView.botText.apply {
                    text = "Typing ..."
                    visibility = View.VISIBLE
                }

                holder.itemView.botTime.apply {
                    text = currentItem.time
                    visibility = View.VISIBLE
                }
                holder.itemView.botImg.apply {
                    setImageResource(R.drawable.bot)
                    visibility = View.VISIBLE
                }


                holder.itemView.botText.apply {
                    text = currentItem.message
                    visibility = View.VISIBLE
                }


                holder.itemView.userText.visibility = View.GONE
                holder.itemView.userTime.visibility = View.GONE
                holder.itemView.userImg.visibility = View.GONE
            }
        }

    }

    fun insertMessage(message: Message) {
        this.messagesList.add(message)
        notifyItemInserted(messagesList.size)
    }
    fun removeMessage(position: Int) {
        this.messagesList.remove(messagesList[position])
        notifyItemRemoved(position)
        messagesList.remove(messagesList[position])
    }
    fun removeMessage(message: Message) {
        this.messagesList.remove(message)
        notifyItemRemoved(messagesList.size)
    }
    fun getString(id: Int): String {
        return messagesList[id].message[0].toString()
    }
}

