package com.yogadimas.tourismapp.home

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Window
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.yogadimas.tourismapp.R
import com.yogadimas.tourismapp.core.data.Resource
import com.yogadimas.tourismapp.core.ui.TourismAdapter
import com.yogadimas.tourismapp.core.ui.model.TourismUiModel
import com.yogadimas.tourismapp.databinding.ActivityHomeBinding
import com.yogadimas.tourismapp.detail.DetailTourismActivity
import com.yogadimas.tourismapp.search.SearchActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue
import androidx.appcompat.R as appcompatR

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val activityContext = this@HomeActivity

    private val homeViewModel: HomeViewModel by viewModel()

    private lateinit var tourismAdapter: TourismAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        setupAdapter()
        setupLoadTourismData()
    }

    private fun setupView() {
        binding = ActivityHomeBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }
        setStatusBarColor(window)
    }

    private fun setStatusBarColor(window: Window) {
        val typedValue = TypedValue()
        val theme = window.context.theme
        theme.resolveAttribute(appcompatR.attr.colorPrimary, typedValue, true)
        val color = typedValue.data
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

    private fun setupAdapter() {
        tourismAdapter = TourismAdapter().apply { onItemClick = { navigateToDetailActivity(it) } }
        with(binding.rvTourism) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = tourismAdapter
        }
    }

    private fun setupLoadTourismData() {
        homeViewModel.getTourism()
        observeTourism()
    }

    private fun observeTourism() {
        homeViewModel.tourism.observe(activityContext) { tourism ->
            when (tourism) {
                is Resource.Loading -> onLoadingState()
                is Resource.Success -> onSuccessState(tourism.data ?: emptyList())
                is Resource.Error -> onErrorState()
            }
        }
    }

    private fun onLoadingState() {
        showLoading(true)
        setupToolbar(false)
        showDefaultView(false)
        showEmptyView(false)
        showErrorView(false)
    }

    private fun onSuccessState(data: List<TourismUiModel>) {
        showLoading(false)
        setupToolbar(true)
        tourismAdapter.submitList(data)
        if (data.isNotEmpty()) showDefaultView(true) else showEmptyView(true)
    }

    private fun onErrorState() {
        showLoading(false)
        showErrorView(true)
    }

    private fun showLoading(isVisible: Boolean) {
        binding.progressBar.isVisible = isVisible
    }

    private fun showEmptyView(isVisible: Boolean) {
        binding.viewHandle.viewEmptyData.root.isVisible = isVisible
    }

    private fun showErrorView(isVisible: Boolean) {
        binding.viewHandle.viewErrorLoadData.apply {
            root.isVisible = isVisible
            btnRefresh.setOnClickListener { homeViewModel.getTourism() }
        }
    }

    private fun showDefaultView(isVisible: Boolean) {
        binding.rvTourism.isVisible = isVisible
    }

    private fun setupToolbar(isMenuVisible: Boolean) = binding.toolbar.apply {
        listOf(
            R.id.action_favorite to { navigateToFavoriteModule() },
            R.id.action_search to { navigateToSearchActivity() }
        ).forEach { (id, action) ->
            menu.findItem(id).apply {
                isVisible = isMenuVisible
                setOnMenuItemClickListener {
                    action()
                    true
                }
            }
        }
    }

    private fun navigateToFavoriteModule() {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(URI_FAVORITE)))
    }

    private fun navigateToSearchActivity() {
        startActivity(Intent(activityContext, SearchActivity::class.java))
    }

    private fun navigateToDetailActivity(selectedData: TourismUiModel) {
        val intent = Intent(activityContext, DetailTourismActivity::class.java)
        intent.putExtra(DetailTourismActivity.EXTRA_DATA, selectedData)
        startActivity(intent)
    }

    companion object {
        private const val URI_FAVORITE = "tourismapp://favorite"
    }

}