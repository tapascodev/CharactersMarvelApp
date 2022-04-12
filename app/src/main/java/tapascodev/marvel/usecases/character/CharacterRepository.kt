package tapascodev.marvel.usecases.character

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import tapascodev.marvel.model.domain.Character
import tapascodev.marvel.usecases.base.BaseRepository
import tapascodev.marvel.usecases.character.paging.CharacterPagingSource

class CharacterRepository (
    private  val api: CharacterApi
): BaseRepository() {

    suspend fun getCharacters (offset: Int, search: String ? = null) = safeApiCall { api.getCharacters(offset.toString(), search).data.results.map { it.toCharacter() } }

    suspend fun getCharacter(id: Int) = safeApiCall { api.getCharacter(id).data.results.map { it.toCharacter() }.first()}

    fun getCharacterByFlow(query: String) : Flow<PagingData<Character>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { CharacterPagingSource(api, query) }
        ).flow
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 30
    }
}
