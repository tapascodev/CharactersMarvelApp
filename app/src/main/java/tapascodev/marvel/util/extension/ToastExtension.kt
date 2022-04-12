package tapascodev.marvel.util.extension

import android.content.Context
import android.widget.Toast

fun Toast.show(context: Context, message: String){
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}