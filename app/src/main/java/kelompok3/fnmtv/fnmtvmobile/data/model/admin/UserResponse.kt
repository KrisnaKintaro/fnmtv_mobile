package kelompok3.fnmtv.fnmtvmobile.data.model.admin

import kelompok3.fnmtv.fnmtvmobile.data.model.User

data class UserResponse(
    val status: String,
    val data: List<User>
)