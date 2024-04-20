package com.example.practik3

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.TextView
import android.widget.ImageView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class PhoneUsers : AppCompatActivity() {
    private lateinit var textUserPhoneUsers: TextView
    private lateinit var textPhonePhoneUsers: TextView
    private lateinit var textViewPhoneUsers: TextView
    private lateinit var imageViewPhoneUsers: ImageView
    private lateinit var editTextPhoneUsers: EditText
    private lateinit var btnFindUser: MaterialButton
    private lateinit var btnAddUser: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Определение макета в зависимости от ориентации
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_phone_users_land)
        } else {
            setContentView(R.layout.activity_phone_users)
        }

        // Инициализация компонентов
        textViewPhoneUsers = findViewById(R.id.textViewPhoneUsers)
        imageViewPhoneUsers = findViewById(R.id.imageViewPhoneUsers)
        editTextPhoneUsers = findViewById(R.id.editTextPhoneUsers)
        btnFindUser = findViewById(R.id.btnFindUser)
        textUserPhoneUsers = findViewById(R.id.textUserPhoneUsers)
        textPhonePhoneUsers = findViewById(R.id.textPhonePhoneUsers)
        btnAddUser = findViewById(R.id.btnAddUser)

        // Устанавливаем слушатели на кнопки
        setupListeners()

        // Загружаем пользователей из базы данных и обновляем RecyclerView
        loadUsersFromDatabase()
    }

    private fun setupListeners() {
        // Нажатие на кнопку "Добавить пользователя"
        btnAddUser.setOnClickListener {
            val intent = Intent(this, AddUserActivity::class.java)
            startActivity(intent)
        }

        // Нажатие на кнопку "Найти пользователя"
        btnFindUser.setOnClickListener {
            val userId = editTextPhoneUsers.text.toString()
            // Поиск пользователя в базе данных
            val dbHelper = UserDatabaseHelper(this)
            dbHelper.readUserById(userId) { user ->
                // Отображение информации о пользователе
                if (user != null) {
                    textUserPhoneUsers.text = "ФИО пользователя: ${user.name}"
                    textPhonePhoneUsers.text = "Модель телефона: ${user.phoneModel}"
                    val imageResId = resources.getIdentifier("phone_${user.id}", "drawable", packageName)
                    imageViewPhoneUsers.setImageResource(imageResId)
                } else {
                    textUserPhoneUsers.text = "Пользователь не найден"
                    textPhonePhoneUsers.text = ""
                    imageViewPhoneUsers.setImageResource(R.drawable.photo)
                }
            }
        }

        // Нажатие на изображение телефона
        imageViewPhoneUsers.setOnClickListener {
            val phoneModel = textPhonePhoneUsers.text.toString()
            Toast.makeText(this, phoneModel, Toast.LENGTH_SHORT).show()
        }

        // Нажатие на имя пользователя
        textUserPhoneUsers.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("User Info", textUserPhoneUsers.text)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(this, "Имя пользователя скопировано в буфер обмена", Toast.LENGTH_SHORT).show()
        }

        // Нажатие на модель телефона
        textPhonePhoneUsers.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Phone Info", textPhonePhoneUsers.text)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(this, "Модель телефона скопирована в буфер обмена", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUsersFromDatabase() {

        // Получаем список пользователей из базы данных
        val dbHelper = UserDatabaseHelper(this)
        dbHelper.insertInitialData()
        dbHelper.readAllUsers { users ->
            // Обновляем RecyclerView
            setupRecyclerView(users)
        }
    }

    private fun setupRecyclerView(users: List<User>) {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        val adapter = UserAdapter(users) { user ->
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("User Info", "${user.name}, ${user.phoneModel}")
            clipboard.setPrimaryClip(clip)

            Toast.makeText(this, "Данные скопированы в буфер обмена", Toast.LENGTH_SHORT).show()
            textUserPhoneUsers.text = "ФИО пользователя: ${user.name}"
            textPhonePhoneUsers.text = "Модель телефона: ${user.phoneModel}"
            val imageResId = resources.getIdentifier("phone_${user.id}", "drawable", packageName)
            imageViewPhoneUsers.setImageResource(imageResId)
        }
        recyclerView.adapter = adapter
    }
}