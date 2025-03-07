package com.yogadimas.tourismapp.favorite

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Window
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.R
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.yogadimas.tourismapp.core.ui.TourismAdapter
import com.yogadimas.tourismapp.core.ui.model.TourismUiModel
import com.yogadimas.tourismapp.detail.DetailTourismActivity
import com.yogadimas.tourismapp.favorite.databinding.ActivityFavoriteBinding
import com.yogadimas.tourismapp.favorite.di.favoriteModule
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules

class FavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteBinding
    private val activityContext = this@FavoriteActivity

    private val favoriteViewModel: FavoriteViewModel by viewModel()

    private lateinit var tourismAdapter: TourismAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        loadKoinModules(favoriteModule)
        setupAdapter()
        setupLoadTourismData()
    }

    private fun setupView() {
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }
        setStatusBarColor(window)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setStatusBarColor(window: Window) {
        val typedValue = TypedValue()
        val theme = window.context.theme
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
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
        favoriteViewModel.getFavoriteTourism()
        observeTourism()
    }

    private fun observeTourism() {
        favoriteViewModel.favoriteTourism.observe(activityContext) { tourism ->
            onSuccessState(tourism ?: emptyList())
        }
    }


    private fun onSuccessState(data: List<TourismUiModel>) {
        tourismAdapter.submitList(data)
        if (data.isNotEmpty()) showDefaultView() else showEmptyView()
    }

    private fun showEmptyView() {
        binding.viewHandle.viewEmptyData.root.isVisible = true
    }

    private fun showDefaultView() {
        binding.rvTourism.isVisible = true
    }


    private fun navigateToDetailActivity(selectedData: TourismUiModel) {
        val intent = Intent(activityContext, DetailTourismActivity::class.java)
        intent.putExtra(DetailTourismActivity.EXTRA_DATA, selectedData)
        startActivity(intent)
    }
}