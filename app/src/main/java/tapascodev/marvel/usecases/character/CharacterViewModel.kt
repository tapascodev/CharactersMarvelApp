package tapascodev.marvel.usecases.character

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import tapascodev.marvel.model.domain.Character
import tapascodev.marvel.model.domain.Resource


class CharacterViewModel (
    private val repository: CharacterRepository
): ViewModel (){

    private var currentQueryValue: String? = null

    private var currentSearchResult: Flow<PagingData<Character>>? = null

    fun searchRepo(queryString: String): Flow<PagingData<Character>> {
        val lastResult = currentSearchResult
        if (queryString == currentQueryValue && lastResult != null) {
            return lastResult
        }
        currentQueryValue = queryString
        val newResult: Flow<PagingData<Character>> = repository.getCharacterByFlow(queryString)
            .cachedIn(viewModelScope)
        currentSearchResult = newResult
        return newResult
    }

    private val _characters: MutableLiveData<Resource<List<Character>>> = MutableLiveData()

    private val _character: MutableLiveData<Resource<Character>> = MutableLiveData()

    var filterTextAll = MutableLiveData<String>()

    val characters: LiveData<Resource<List<Character>>>
        get() = _characters

    val character: LiveData<Resource<Character>>
        get() = _character

    fun getCharacters (offset: Int, search: String ? = null) = viewModelScope.launch{
        _characters.value = Resource.Loading
        _characters.value = repository.getCharacters(offset, search)
    }

    fun getCharacter(id: Int) = viewModelScope.launch {
        _character.value = Resource.Loading
        _character.value = repository.getCharacter(id)
    }
}

private const val VISIBLE_THRESHOLD = 5
private const val LAST_SEARCH_QUERY: String = "last_search_query"
private const val DEFAULT_QUERY = "Android"