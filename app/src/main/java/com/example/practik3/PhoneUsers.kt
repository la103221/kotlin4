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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PhoneUsers : AppCompatActivity() {
    private lateinit var editTextPhoneUsers: EditText
    private lateinit var btnFindUser: FloatingActionButton
    private lateinit var btnAddUser: FloatingActionButton



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_phone_users)

        // Инициализация компонентов
        editTextPhoneUsers = findViewById(R.id.editTextPhoneUsers)
        btnFindUser = findViewById(R.id.btnFindUser)
        btnAddUser = findViewById(R.id.btnAddUser)

        // Устанавливаем слушатели на кнопки
        setupListeners()

        // Загружаем пользователей из базы данных и обновляем RecyclerView
        loadUsersFromDatabase()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun setupListeners() {
        // Нажатие на кнопку "Добавить пользователя"
        btnAddUser?.setOnClickListener {
            val intent = Intent(this, AddUserActivity::class.java)
            startActivity(intent)
        }

        // Нажатие на кнопку "Найти пользователя"
        btnFindUser.setOnClickListener {
            val userId = editTextPhoneUsers.text.toString()
            // Поиск пользователя в базе данных
            val dbHelper = UserDatabaseHelper(this)
            CoroutineScope(Dispatchers.IO).launch {
                dbHelper.readUserById(userId)
            }
        }

        // Нажатие на изображение телефона
//        imageViewPhoneUsers.setOnClickListener {
//            val phoneModel = textPhonePhoneUsers.text.toString()
//            Toast.makeText(this, phoneModel, Toast.LENGTH_SHORT).show()
//        }

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadUsersFromDatabase() {
        // Получаем список пользователей из базы данных
        val dbHelper = UserDatabaseHelper(this)
        lifecycleScope.launch {
            dbHelper.insertInitialData()
            val users = dbHelper.readAllUsers()
            withContext(Dispatchers.IO) {
                users.collect {
                    withContext(Dispatchers.Main) {
                        setupRecyclerView(it)
                    }
                }
            }
        }
    }

private fun setupRecyclerView(users: List<User>) {
    val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
    val layoutManager = LinearLayoutManager(this)
    recyclerView.layoutManager = layoutManager

    val adapter = UserAdapter(this, users) { user ->
        lifecycleScope.launch {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("User Info", "${user.name}, ${user.phoneModel}")
            clipboard.setPrimaryClip(clip)
        }
        Toast.makeText(this, "Данные скопированы в буфер обмена", Toast.LENGTH_SHORT).show()
    }
    recyclerView.adapter = adapter
}
}