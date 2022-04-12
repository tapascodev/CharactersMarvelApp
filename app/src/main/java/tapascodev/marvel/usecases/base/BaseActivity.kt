package tapascodev.marvel.usecases.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import tapascodev.marvel.provider.services.marvel.MarvelService
import java.lang.IllegalArgumentException

abstract class BaseActivity<VB: ViewBinding, VM: ViewModel, R:BaseRepository> (
    private val bindingInflater: (inflater: LayoutInflater) -> VB
): AppCompatActivity() {

    private var _binding: VB? = null

    protected lateinit var viewModel : VM

    protected val marvelService = MarvelService()

    val binding: VB
        get() = _binding as VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = bindingInflater.invoke(layoutInflater)

        if(_binding == null)
            throw IllegalArgumentException("Binding cannot be null")

        //build view model
        val factory = ViewModelFactory(getFragmentRepository())
        viewModel = ViewModelProvider(this, factory)[getViewModel()]

        setContentView(_binding!!.root)
    }

    abstract fun getViewModel () : Class<VM>

    abstract fun getFragmentRepository(): R
}