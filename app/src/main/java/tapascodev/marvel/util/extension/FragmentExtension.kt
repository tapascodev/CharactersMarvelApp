package tapascodev.marvel.util.extension

import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.displayToast(message: String?){
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}