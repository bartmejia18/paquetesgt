package com.encomiendas.encomiendas.ui.packages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.encomiendas.encomiendas.data.model.Tracking
import com.encomiendas.encomiendas.data.repository.TrackingRepository
import com.encomiendas.encomiendas.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PackagesViewModel @Inject constructor(
    private val trackingRepository: TrackingRepository
) : ViewModel() {

    private val _code = MutableLiveData<String>()
    val code: LiveData<String> = _code

    private val _description = MutableLiveData<String>()
    val description: LiveData<String> = _description

    fun codeChanged(code: String) {
        _code.value = code
    }

    fun descriptionChanged(description: String) {
        _description.value = description
    }

    private val _tracking = MutableLiveData<Resource<Tracking>?>()
    val tracking: LiveData<Resource<Tracking>?> = _tracking

    fun register(
        tracking: Tracking
    ) {

        viewModelScope.launch {
            _tracking.postValue(Resource.loading(null))
            trackingRepository.registration(
                tracking
            ).let {
                if (it.isSuccessful) {
                    _tracking.postValue(
                        Resource.success(
                            it.message(),
                            it.body()
                        ))
                } else {
                    _tracking.postValue(Resource.error(it.errorBody().toString()))
                }
            }
        }
    }

    fun cleanTracking() {
        _tracking.value = null
    }
}