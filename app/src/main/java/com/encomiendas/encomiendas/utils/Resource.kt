package com.encomiendas.encomiendas.utils

data class Resource<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(message: String?, data: T?): Resource<T> = Resource(status = Status.SUCCESS, data = data, message = message)

        fun <T> error(message: String, data: T? = null): Resource<T> = Resource(status = Status.ERROR, data = data, message = message)

        fun <T> loading(data: T? = null): Resource<T> = Resource(status = Status.LOADING, data = data, message = null)
    }
}