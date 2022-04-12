package tapascodev.marvel.usecases.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tapascodev.marvel.usecases.character.CharacterRepository
import tapascodev.marvel.usecases.character.CharacterViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory (
    private val repository: BaseRepository
) : ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(CharacterViewModel::class.java) -> CharacterViewModel(repository as CharacterRepository) as T
            else -> throw IllegalArgumentException("ViewModelClass not found")
        }
    }
}