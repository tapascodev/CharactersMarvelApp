package tapascodev.marvel.util.extension

import android.view.View
import tapascodev.marvel.model.domain.Resource

fun View.visible(isVisible: Boolean){
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun View.enable(enable: Boolean){
    isEnabled = enable
    alpha = if(enable) 1f else 0.5f
}