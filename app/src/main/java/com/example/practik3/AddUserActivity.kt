package com.example.practik3

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class AddUserActivity : AppCompatActivity() {
    private lateinit var editTextId: EditText
    private lateinit var editTextName: EditText
    private lateinit var editTextPhoneModel: EditText
    private lateinit var btnSaveUser: Button
    private lateinit var dbHelper: UserDatabaseHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)

        editTextId = findViewById(R.id.editTextId)
        editTextName = findViewById(R.id.editTextName)
        editTextPhoneModel = findViewById(R.id.editTextPhoneModel)
        btnSaveUser = findViewById(R.id.btnSaveUser)

        dbHelper = UserDatabaseHelper(this)

        btnSaveUser.setOnClickListener {
            val id = editTextId.text.toString()
            val name = editTextName.text.toString()
            val phoneModel = editTextPhoneModel.text.toString()
            dbHelper.insertUser(id , name, phoneModel)
            finish()
            val intent = Intent(this, PhoneUsers::class.java)
            startActivity(intent)
        }
    }
}