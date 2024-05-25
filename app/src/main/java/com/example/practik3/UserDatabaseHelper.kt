package com.example.practik3

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Database
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

// Создание сущности User
@Entity
class User(
    val name: String,
    val phoneModel: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)

// Создание DAO
@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): Flow<List<User>>

    @Query("SELECT * FROM user WHERE id = :userId")
    fun getUserById(userId: String): Flow<User?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User): Long
}

// Создание абстрактного класса базы данных
@Database(entities = [User::class], version = 3)
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
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// Класс UserDatabaseHelper
class UserDatabaseHelper(context: Context) : ViewModel() {
    private val db: AppDatabase = AppDatabase.getDatabase(context)
    val users = MutableStateFlow<List<User>>(emptyList())

    suspend fun insertUser(name: String, phoneModel: String): Long {
        return db.userDao().insert(User(name, phoneModel))
    }

    suspend fun readUserById(userId: String): Flow<User?> {
        return db.userDao().getUserById(userId)
    }

    suspend fun insertInitialData() = withContext(Dispatchers.IO)  {
        val users = listOf(
            User("Михайлов Иван Алексеевич", "HONOR Magic V2"),
            User("Ковалева Анна Сергеевна", "Samsung Galaxy S24"),
            User("Сергеев Семён Иванович", "Xiaomi Redmi 13C"),
            User("Афанасьева Дарья Данииловна", "Apple iPhone 15 Pro Max"),
            User("Богданова Полина Данииловна", "Apple iPhone 14"),
            User("Покровский Руслан Александрович", "Apple iPhone 15 Pro"),
            User("Назаров Артём Дмитриевич", "HUAWEI P60 Pro"),
            User("Николаева Арина Дмитриевна", "Samsung Galaxy Z Flip5"),
            User("Лебедев Максим Глебович", "Samsung Galaxy S21"),
            User("Хромова Виктория Ильинична", "Xiaomi Redmi Note 12"),
        )
        for (user in users) {
            db.userDao().insert(
                User(
                    user.name,
                    user.phoneModel
                )
            )// Вставляем каждого пользователя в базу данных

        }
    }

    fun readAllUsers(): Flow<List<User>> {
        return db.userDao().getAll()
    }
}