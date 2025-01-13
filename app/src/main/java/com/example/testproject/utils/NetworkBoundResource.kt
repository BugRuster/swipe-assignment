package com.example.testproject.utils

import kotlinx.coroutines.flow.*

suspend inline fun <ResultType> networkBoundResource(
    crossinline query: suspend () -> ResultType,
    crossinline fetch: suspend () -> ResultType,
    crossinline saveFetchResult: suspend (ResultType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true }
) = flow {
    emit(Resource.Loading())

    val data = query()

    if (shouldFetch(data)) {
        emit(Resource.Loading(data))
        try {
            saveFetchResult(fetch())
            emit(Resource.Success(query()))
        } catch (throwable: Throwable) {
            emit(Resource.Error(throwable.message ?: "Network error", data))
        }
    } else {
        emit(Resource.Success(data))
    }
}