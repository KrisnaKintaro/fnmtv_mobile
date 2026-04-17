package kelompok3.fnmtv.fnmtvmobile.Controller.Administrator.Admin

import android.content.ContentValues
import android.content.Context
import kelompok3.fnmtv.fnmtvmobile.Database.Migration.DatabaseHelper
import kelompok3.fnmtv.fnmtvmobile.Database.Model.User

class UserController(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun getAllUsers(): List<User> {
        val userList = mutableListOf<User>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users ORDER BY id DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val user = User(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    username = cursor.getString(cursor.getColumnIndexOrThrow("username")),
                    email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                    role = cursor.getString(cursor.getColumnIndexOrThrow("role")),
                    status = cursor.getString(cursor.getColumnIndexOrThrow("status")),
                    password = cursor.getString(cursor.getColumnIndexOrThrow("password"))
                )
                userList.add(user)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return userList
    }

    fun tambahUser(user: User): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("username", user.username)
            put("email", user.email)
            put("role", user.role)
            put("status", user.status)
            put("password", user.password) // Password dummy untuk awal
        }
        val result = db.insert("users", null, values)
        db.close()
        return result != -1L
    }

    fun editUser(user: User): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("username", user.username)
            put("email", user.email)
            put("role", user.role)
            put("status", user.status)
        }
        val result = db.update("users", values, "id = ?", arrayOf(user.id.toString()))
        db.close()
        return result > 0
    }

    fun hapusUser(userId: Int): Boolean {
        val db = dbHelper.writableDatabase
        val result = db.delete("users", "id = ?", arrayOf(userId.toString()))
        db.close()
        return result != -1
    }
}