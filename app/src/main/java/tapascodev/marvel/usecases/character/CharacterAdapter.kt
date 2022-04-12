package tapascodev.marvel.usecases.character

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tapascodev.marvel.R
import tapascodev.marvel.model.domain.Character
import tapascodev.marvel.usecases.character.show.ShowCharacterActivity
import tapascodev.marvel.util.extension.openActivity

class CharacterAdapter(private val characters:ArrayList<Character>) : PagingDataAdapter<Character, CharacterViewHolder> (
    CHARACTER_COMPARATOR
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        return CharacterViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val item = getItem(position)

        if (item != null) {
            holder.render(item)
        }

        holder.itemView.setOnClickListener {
            it.context.openActivity(ShowCharacterActivity::class.java){
                if (item != null) {
                    putInt("character_id", item.id)
                }
            }
        }
    }

    companion object {
        private val CHARACTER_COMPARATOR = object: DiffUtil.ItemCallback<Character>() {
            override fun areItemsTheSame(oldItem: Character, newItem: Character): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Character, newItem: Character): Boolean =
                oldItem == newItem
        }
    }
}