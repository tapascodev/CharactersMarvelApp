package tapascodev.marvel.usecases.character

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import tapascodev.marvel.model.domain.CharacterResponse

interface CharacterApi {

    @GET("characters")
    suspend fun getCharacters(
        @Query("offset") offset: String,
        @Query("nameStartsWith") search: String? = null
    ): CharacterResponse

    @GET("characters")
    suspend fun getCharactersFlow(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("nameStartsWith") search: String? = null
    ): CharacterResponse

    @GET("characters")
    suspend fun getCharactersFlow2(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): CharacterResponse

    @GET("characters/{characterId}")
    suspend fun getCharacter(
        @Path("characterId") characterId: Int
    ): CharacterResponse
}