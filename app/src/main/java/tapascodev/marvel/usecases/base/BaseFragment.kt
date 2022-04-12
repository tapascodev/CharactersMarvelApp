package tapascodev.marvel.usecases.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.viewbinding.ViewBinding
import tapascodev.marvel.provider.services.marvel.MarvelService
import java.lang.IllegalArgumentException

abstract class BaseFragment<VB: ViewBinding, VM: ViewModel, R: BaseRepository > (
    private val bindingInflater: (inflater: LayoutInflater) -> VB
) : Fragment(), LifecycleObserver{

    private var _binding: VB? = null

    protected lateinit var viewModel : VM

    protected val marvelService = MarvelService()

    val binding: VB
        get() = _binding as VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater.invoke(inflater)

        if(_binding == null)
            throw IllegalArgumentException("Binding cannot be null")

        //build view model
        val factory = ViewModelFactory(getFragmentRepository())
        viewModel = ViewModelProvider(this, factory)[getViewModel()]

        return binding.root
    }

    abstract fun getViewModel () : Class<VM>

    abstract fun getFragmentRepository(): R
}