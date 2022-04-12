package tapascodev.marvel.usecases.character.show

import android.os.Bundle
import com.bumptech.glide.Glide
import tapascodev.marvel.databinding.ActivityShowCharacterBinding
import tapascodev.marvel.model.domain.Character
import tapascodev.marvel.model.domain.Resource
import tapascodev.marvel.usecases.base.BaseActivity
import tapascodev.marvel.usecases.character.CharacterApi
import tapascodev.marvel.usecases.character.CharacterRepository
import tapascodev.marvel.usecases.character.CharacterViewModel
import tapascodev.marvel.util.extension.displayToast
import tapascodev.marvel.util.extension.visible

class ShowCharacterActivity : BaseActivity<ActivityShowCharacterBinding, CharacterViewModel, CharacterRepository>(
    ActivityShowCharacterBinding::inflate
) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = intent.extras
        val id = bundle!!.getInt("character_id")

        viewModel.getCharacter(id)

        binding.progressBar.visible(false)
        supportActionBar?.hide()

        viewModel.character.observe(this) {
            when(it)
            {
                is Resource.Success -> {
                    binding.progressBar.visible(false)
                    displayUI(it.value)
                }
                is Resource.Failure -> {
                    displayToast("Load Failure")
                }
                is Resource.Loading -> {
                    binding.progressBar.visible(true)
                }
            }
        }
    }

    private fun displayUI (character: Character)
    {
        val imageUrl = "${character.thumbnail}/landscape_xlarge.${character.thumbnailExt}"
        Glide.with(this).load(imageUrl).into(binding.IVCharacter)

        binding.nameCharacter.text = character.name
    }

    override fun getViewModel() = CharacterViewModel::class.java

    override fun getFragmentRepository() = CharacterRepository(marvelService.buildApi(CharacterApi::class.java))
}