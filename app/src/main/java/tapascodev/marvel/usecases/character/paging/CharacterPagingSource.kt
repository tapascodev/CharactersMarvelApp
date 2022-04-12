package tapascodev.marvel.usecases.character.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException
import tapascodev.marvel.model.domain.Character
import tapascodev.marvel.usecases.character.CharacterApi
import java.io.IOException


private const val MARVEL_STARTING_PAGE_INDEX = 0
private const val STARTING_OFFSET = 0
private const val LOAD_SIZE = 20

class CharacterPagingSource (
    private val api: CharacterApi,
    private val query: String
) : PagingSource<Int, Character> () {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> {
        val position = params.key ?: STARTING_OFFSET
        val apiQuery = query

        return try {
            val response = if(query != ""){
                api.getCharactersFlow(offset = position, limit = params.loadSize, query)
            } else {
                api.getCharactersFlow2(offset = position, limit = params.loadSize)
            }

            val characters = response.data.results.map { it.toCharacter() }

            LoadResult.Page(
                data = characters,
                prevKey = if (position == STARTING_OFFSET) null else position - LOAD_SIZE,
                nextKey = if (characters.isEmpty()) null else position + LOAD_SIZE
            )
        } catch (exception : IOException) {

            Log.i("CHARACTER EXCEPTION", exception.toString())
            return LoadResult.Error(exception)
        } catch (exception : HttpException) {
            return LoadResult.Error(exception)
        }


    }

    // The refresh key is used for subsequent refresh calls to PagingSource.load after the initial load
    override fun getRefreshKey(state: PagingState<Int, Character>): Int? {
        return state.anchorPosition?.let {
            // We need to get the previous key (or next key if previous is null) of the page
            // that was closest to the most recently accessed index.
            // Anchor position is the most recently accessed index
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }


}