package tapascodev.marvel.usecases.character

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import tapascodev.marvel.databinding.FragmentCharacterBinding
import tapascodev.marvel.usecases.base.BaseFragment
import tapascodev.marvel.usecases.character.paging.CharactersLoadStateAdapter
import tapascodev.marvel.usecases.home.HomeActivity
import tapascodev.marvel.util.extension.displayToast
import tapascodev.marvel.util.extension.visible


class CharacterFragment : BaseFragment<FragmentCharacterBinding, CharacterViewModel, CharacterRepository>(
    FragmentCharacterBinding::inflate
) {
    private  lateinit var layoutManager: GridLayoutManager
    private var offset: Int = 0
    private val adapter: CharacterAdapter = CharacterAdapter(arrayListOf())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        (activity as HomeActivity).supportActionBar?.title = "Characters"
        binding.progressBarCharacter.visible(false)

        initAdapter()
        loadStateAdapter()
        binding.retryButton.setOnClickListener { adapter.retry() }

        val query = savedInstanceState?.getString(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
        search(query)
        initSearch()

        //TEXT LISTENER
        binding.simpleSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query != null){
                    binding.recycleCharacters.scrollToPosition(0)
                    updateRepoListFromInput()
                    binding.simpleSearchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                return true
            }
        })

        binding.simpleSearchView.setOnCloseListener {
            search(DEFAULT_QUERY)
            false
        }
        /*recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            var loading = false
            var previousTotal = 20

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                val visibleItemCount = recyclerView.childCount;
                val totalItemCount = layoutManager.itemCount;
                val firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (visibleItemCount + firstVisibleItem)
                    >= totalItemCount) {
                    offset += 20;
                    viewModel.getCharacters(offset)
                    loading = true;
                }
            }
        })

        binding.apply {

            //OBSERVE
            viewModel.characters.observe(viewLifecycleOwner, Observer {
                when (it) {
                    is Resource.Success -> {
                        progressBarCharacter.visible(false)

                        //if(simpleSearchView.query.isNullOrEmpty())
                            //characterAdapter.updateListCharacters(it.value as ArrayList<Character>)
                        //else
                            //characterAdapter.setData(it.value as ArrayList<Character>)

                        //characterAdapter.submitData(viewLifecycleOwner, it.value)
                    }
                    is Resource.Failure -> {
                        displayToast("Load Failure")
                    }
                    is Resource.Loading -> {
                        progressBarCharacter.visible(true)
                    }
                }
            })

            //TEXT LISTENER
            simpleSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if(query != null){
                        recycleCharacters.scrollToPosition(0)
                        viewModel.getCharacters(0, query)
                        simpleSearchView.clearFocus()
                    }
                    return true
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    return true
                }
            })
        }*/
    }

    private fun loadStateAdapter(){
        adapter.addLoadStateListener { loadState ->
            val listEmpty = loadState.source.refresh is LoadState.NotLoading && adapter.itemCount == 0
            showEmptyList(listEmpty)

            // Only show the list if refresh succeeds.
            binding.recycleCharacters.isVisible = loadState.source.refresh is LoadState.NotLoading
            // Show loading spinner during initial load or refresh.
            binding.progressBarCharacter.isVisible = loadState.source.refresh is LoadState.Loading
            // Show the retry state if initial load or refresh fails.
            binding.retryButton.isVisible = loadState.source.refresh is LoadState.Error

            // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                displayToast("\uD83D\uDE28 Wooops ${it.error}")
            }
        }
    }

    private fun initAdapter(){
        val recycler = binding.recycleCharacters
        layoutManager = GridLayoutManager(context, 2)
        recycler.layoutManager = layoutManager
        recycler.setHasFixedSize(true)
        recycler.adapter = adapter.withLoadStateHeaderAndFooter(
            footer = CharactersLoadStateAdapter { adapter.retry() },
            header = CharactersLoadStateAdapter { adapter.retry() },
        )
    }

    private fun initSearch() {

        // Scroll to top when the list is refreshed from network.
        lifecycleScope.launch {
            adapter.loadStateFlow
                // Only emit when REFRESH LoadState for RemoteMediator changes.
                .distinctUntilChangedBy { it.refresh }
                // Only react to cases where Remote REFRESH completes i.e., NotLoading.
                .filter { it.refresh is LoadState.NotLoading }
                .collect { binding.recycleCharacters.scrollToPosition(0) }
        }
    }

    private var searchJob: Job? = null

    private fun search(query: String) {
        // Make sure we cancel the previous job before creating a new one
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.searchRepo(query).collectLatest {
                adapter.submitData(it)
            }
        }
    }

    override fun getViewModel() = CharacterViewModel::class.java

    override fun getFragmentRepository() = CharacterRepository(marvelService.buildApi(CharacterApi::class.java))


    private fun updateRepoListFromInput() {
        binding.simpleSearchView.query.trim().let {
            if (it.isNotEmpty()) {
                search(it.toString())
            }
        }
    }

    private fun showEmptyList(show: Boolean) {
        if (show) {
            binding.emptyList.visibility = View.VISIBLE
            binding.recycleCharacters.visibility = View.GONE
        } else {
            binding.emptyList.visibility = View.GONE
            binding.recycleCharacters.visibility = View.VISIBLE
        }
    }

    companion object {
        private const val LAST_SEARCH_QUERY: String = "last_search_query"
        private const val DEFAULT_QUERY = ""
    }
}
