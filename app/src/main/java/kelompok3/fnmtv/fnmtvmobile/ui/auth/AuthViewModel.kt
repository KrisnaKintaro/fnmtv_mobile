package kelompok3.fnmtv.fnmtvmobile.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kelompok3.fnmtv.fnmtvmobile.data.api.ApiClient
import kelompok3.fnmtv.fnmtvmobile.data.model.auth.AuthResponse
import kelompok3.fnmtv.fnmtvmobile.data.repository.AuthRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthRepository(application)

    // Menyimpan respon hasil hit API (Bisa sukses, bisa gagal)
    private val _loginResult = MutableLiveData<Response<AuthResponse>?>()
    val loginResult: LiveData<Response<AuthResponse>?> get() = _loginResult

    // Menyimpan kondisi apakah internet sedang berjalan atau tidak (Buat animasi loading/disabled button)
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun prosesLogin(email: String, pas: String) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                ApiClient.getApiService(getApplication()).getCsrfCookie()
                val hasilApi = repository.login(email, pas)

                _loginResult.value = hasilApi
            } catch (e: Exception) {
                _loginResult.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
}