package tapascodev.marvel.provider.services.marvel

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor
import java.math.BigInteger
import java.security.MessageDigest
import java.sql.Timestamp

class MarvelService {

    companion object{
        private const val BASE_URL = "https://gateway.marvel.com/v1/public/"
        val timeStamp = Timestamp(System.currentTimeMillis()).time.toString()
        const val API_KEY = "1ee5f3000d4f89a415deffc281b773d4"
        private const val PRIVATE_KEY = "e24e5a4cf2173e29085e1cafd1ff2458bc211378"
        const val limit = "20"

        fun hash(): String {
            val input = "$timeStamp$PRIVATE_KEY$API_KEY"
            val md = MessageDigest.getInstance("MD5")
            return BigInteger(1, md.digest(input.toByteArray())).toString(16)
        }
    }

    fun <Api> buildApi(
        api: Class<Api>
    ): Api {

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .addInterceptor {  chain -> return@addInterceptor addApiKeyToRequests(chain) }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(api)
    }

    private fun addApiKeyToRequests(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
        val original = chain.request().url
        val newUrl = original.newBuilder()
            .addQueryParameter("ts", timeStamp)
            .addQueryParameter("apikey", API_KEY)
            .addQueryParameter("hash", hash())
            .build()
        request.url(newUrl)
        return chain.proceed(request.build())
    }
}