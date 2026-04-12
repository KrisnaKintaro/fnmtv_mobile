package kelompok3.fnmtv.fnmtvmobile.Controller.Administrator.Admin

import android.content.Context
import kelompok3.fnmtv.fnmtvmobile.Database.Migration.DatabaseHelper
import kelompok3.fnmtv.fnmtvmobile.Database.Model.User

class UserController(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun getAllUsers(): List<User> {
        val userList = mutableListOf<User>()
        val db = dbHelper.readableDatabase

        // Tarik data sekalian kita urutin dari user terbaru
        val cursor = db.rawQuery("SELECT * FROM users ORDER BY id DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val user = User(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")), // FIX: id_user jadi id
                    username = cursor.getString(cursor.getColumnIndexOrThrow("username")),
                    email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                    role = cursor.getString(cursor.getColumnIndexOrThrow("role")),
                    status = cursor.getString(cursor.getColumnIndexOrThrow("status")), // FIX: Tarik kolom status
                    password = ""
                )
                userList.add(user)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return userList
    }
}