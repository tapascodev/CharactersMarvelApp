package tapascodev.marvel.usecases.base

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import tapascodev.marvel.model.domain.Resource

abstract class BaseRepository {
    suspend fun <T> safeApiCall (
        apiCall : suspend  () -> T
    ): Resource<T> {
        return withContext(Dispatchers.IO) {
            try {
                Resource.Success(apiCall.invoke())
            } catch (throwable : Throwable) {
                when(throwable){
                    is HttpException -> {
                        Resource.Failure(true, throwable.code(), throwable.response()?.errorBody())
                    }
                    else -> Resource.Failure(true, null, null)
                }
            }
        }
    }
}