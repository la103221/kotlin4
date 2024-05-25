package com.example.practik3

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserAdapter(
    private val context: Context,
    private val users: List<User>,
    private val onItemClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewUserName: TextView = itemView.findViewById(R.id.textViewUserName)
        private val textViewPhoneModel: TextView = itemView.findViewById(R.id.textViewPhoneModel)
        private val imageViewPhoneModel: ImageView = itemView.findViewById(R.id.imageViewPhoneUsers)

        fun bind(user: User) {
            val imageResId = context.resources.getIdentifier(
                "phone_${user.id % 9}",
                "drawable",
                context.packageName
            )
            imageViewPhoneModel.setImageResource(imageResId)
            textViewUserName.text = user.name
            textViewPhoneModel.text = user.phoneModel

            // Присваиваем обработчик нажатия на элемент списка
            itemView.setOnClickListener {
                onItemClick(user)
            }
        }
    }
}