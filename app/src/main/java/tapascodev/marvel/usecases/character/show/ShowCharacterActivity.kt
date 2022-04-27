package tapascodev.marvel.usecases.character.show

import android.graphics.drawable.Drawable
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
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

        setToolbar()

        binding.toolbar.setNavigationOnClickListener {
            this.finish()
        }

        binding.progressBar.visible(false)

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
        val imageUrl = "${character.thumbnail}/portrait_incredible.${character.thumbnailExt}"

        Glide.with(this)
            .load(imageUrl)
            .centerCrop()
            .into(object : CustomTarget<Drawable?>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                }
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable?>?
                ) {
                    binding.layoutShow.background = resource
                }
            })

        binding.nameCharacter.text = character.name
        binding.descriptionCharacter.text = character.description
    }

    private fun setToolbar()
    {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = null
    }

    override fun getViewModel() = CharacterViewModel::class.java

    override fun getFragmentRepository() = CharacterRepository(marvelService.buildApi(CharacterApi::class.java))
}