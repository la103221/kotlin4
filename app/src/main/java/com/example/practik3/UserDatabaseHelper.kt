package com.example.practik3

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Database
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Entity
import androidx.room.PrimaryKey

// Создание сущности User
@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val name: String,
    val phoneModel: String
)

// Создание DAO
@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAll(): List<User>

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: String): User?

    @Query("INSERT INTO users (id, name, phoneModel) VALUES (:id, :name, :phoneModel)")
    fun insert(id: String, name: String, phoneModel: String)
}

// Создание абстрактного класса базы данных
@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app-database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// Класс UserDatabaseHelper
class UserDatabaseHelper(context: Context) {
    private val db: AppDatabase = AppDatabase.getDatabase(context)

    fun insertUser(id: String, name: String, phoneModel: String) {
        db.userDao().insert(id, name, phoneModel)
    }
    fun readUserById(userId: String, callback: (User?) -> Unit) {
        Thread {
            val user = db.userDao().getUserById(userId)
            callback(user)
        }.start()
    }
    fun insertInitialData() {
        val users = listOf(
            User("1", "Михайлов Иван Алексеевич", "HONOR Magic V2"),
            User("2", "Ковалева Анна Сергеевна", "Samsung Galaxy S24"),
            User("3", "Сергеев Семён Иванович", "Xiaomi Redmi 13C"),
            User("4", "Афанасьева Дарья Данииловна", "Apple iPhone 15 Pro Max"),
            User("5", "Богданова Полина Данииловна", "Apple iPhone 14"),
            User("6", "Покровский Руслан Александрович", "Apple iPhone 15 Pro"),
            User("7", "Назаров Артём Дмитриевич", "HUAWEI P60 Pro"),
            User("8", "Николаева Арина Дмитриевна", "Samsung Galaxy Z Flip5"),
            User("9", "Лебедев Максим Глебович", "Samsung Galaxy S21"),
            User("10", "Хромова Виктория Ильинична", "Xiaomi Redmi Note 12"),
        )
        for (user in users) {
            db.userDao().insert(user.id, user.name, user.phoneModel) // Вставляем каждого пользователя в базу данных
        }
    }

    fun readAllUsers(callback: (List<User>) -> Unit) {
        val users = db.userDao().getAll()
        callback(users)
    }
}