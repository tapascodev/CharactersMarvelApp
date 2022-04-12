package tapascodev.marvel.usecases.character

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tapascodev.marvel.R
import tapascodev.marvel.model.domain.Character

class CharacterViewHolder (private val view: View): RecyclerView.ViewHolder(view){

    val name = view.findViewById<TextView>(R.id.tvCharacterName)
    private val image: ImageView = view.findViewById(R.id.roundedImageView)

    fun render(character: Character){
        name.text = character.name
        val imageUrl = "${character.thumbnail}/portrait_medium.${character.thumbnailExt}"
        Glide.with(view.context).load(imageUrl).into(image)
    }

    companion object {
        fun create(parent: ViewGroup): CharacterViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_character, parent, false)
            return CharacterViewHolder(view)
        }
    }
}