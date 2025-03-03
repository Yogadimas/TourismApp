package com.yogadimas.tourismapp.search

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yogadimas.tourismapp.core.ui.TourismAdapter
import com.yogadimas.tourismapp.core.ui.TourismSuggestionAdapter
import com.yogadimas.tourismapp.core.ui.model.TourismUiModel
import com.yogadimas.tourismapp.databinding.ActivitySearchBinding
import com.yogadimas.tourismapp.detail.DetailTourismActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.appcompat.R as appcompatR
import com.yogadimas.tourismapp.core.R as coreR

class SearchActivity : AppCompatActivity() {


    private lateinit var binding: ActivitySearchBinding
    private val activityContext = this@SearchActivity

    private lateinit var tourismAdapter: TourismAdapter
    private lateinit var tourismSuggestionAdapter: TourismSuggestionAdapter

    private val searchViewModel: SearchViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        setupSearch()
    }

    private fun setupView() {
        binding = ActivitySearchBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }
        setStatusBarColor(window)
        binding.apply {
            btnBack.setOnClickListener { finish() }
            btnReset.setOnClickListener { resetSearch() }
            searchView.setupWithSearchBar(searchBar)
            searchView.toolbar.navigationIcon = ContextCompat.getDrawable(
                activityContext,
                coreR.drawable.ic_back
            )
        }
        setupAdapters()
    }

    private fun setStatusBarColor(window: Window) {
        val color = getColorPrimary(window)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            window.statusBarColor = color
            val isLight = resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK ==
                    Configuration.UI_MODE_NIGHT_NO
            WindowInsetsControllerCompat(window, window.decorView).apply {
                isAppearanceLightStatusBars = !isLight
                isAppearanceLightNavigationBars = isLight
            }
        }
    }

    private fun getColorPrimary(window: Window): Int {
        val typedValue = TypedValue()
        val theme = window.context.theme
        theme.resolveAttribute(appcompatR.attr.colorPrimary, typedValue, true)
        val color = typedValue.data
        return color
    }

    private fun setupAdapters() {
        tourismAdapter = TourismAdapter().apply { onItemClick = { navigateToDetail(it) } }
        tourismSuggestionAdapter = TourismSuggestionAdapter { navigateToDetail(it) }
        binding.apply {
            rvTourism.setupRecyclerView(tourismAdapter)
            rvTourismSuggestion.setupRecyclerView(tourismSuggestionAdapter)
        }
    }

    private fun RecyclerView.setupRecyclerView(adapter: RecyclerView.Adapter<*>) {
        layoutManager = LinearLayoutManager(context)
        setHasFixedSize(true)
        this.adapter = adapter
    }


    private fun setupSearch() {
        binding.searchView.apply {
            setupWithSearchBar(binding.searchBar)
            handleSearchInput()
            observeSearchResults()
        }
        handleBackPressed()
    }

    private fun handleSearchInput() {
        binding.searchView.editText.apply {
            addTextChangedListener(object : SimpleTextWatcher() {
                override fun afterTextChanged(s: Editable?) {
                    s?.let {
                        if (it.isNotEmpty()) searchViewModel.queryChannel.value = it.toString()
                    }
                }
            })

            setOnEditorActionListener { textView, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchViewModel.queryChannel.value = textView.text.toString()
                    binding.searchView.hide()
                    true
                } else false
            }
        }
    }

    private fun observeSearchResults() {
        searchViewModel.searchResult.observe(this) { result ->
            binding.apply {
                val hasResults = result.isNotEmpty()
                rvTourism.visibility = if (hasResults) View.VISIBLE else View.GONE
                rvTourismSuggestion.visibility = if (hasResults) View.VISIBLE else View.GONE
            }
            tourismAdapter.submitList(result)
            tourismSuggestionAdapter.submitList(result)
        }
    }

    private fun resetSearch() {
        binding.apply {
            searchView.editText.text.clear()
            searchViewModel.queryChannel.value = ""
            rvTourism.isVisible = false
            rvTourismSuggestion.isVisible = false
        }
    }

    private fun handleBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.searchView.isShowing) binding.searchView.hide()
                else finish()
            }
        })
    }

    private fun navigateToDetail(selectedData: TourismUiModel) {
        startActivity(Intent(activityContext, DetailTourismActivity::class.java).apply {
            putExtra(DetailTourismActivity.EXTRA_DATA, selectedData)
        })
    }

    abstract class SimpleTextWatcher : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }
}
