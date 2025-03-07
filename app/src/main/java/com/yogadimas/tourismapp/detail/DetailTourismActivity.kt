package com.yogadimas.tourismapp.detail

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Window
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat.getParcelableExtra
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.bumptech.glide.Glide
import com.yogadimas.tourismapp.R
import com.yogadimas.tourismapp.core.ui.model.TourismUiModel
import com.yogadimas.tourismapp.databinding.ActivityDetailTourismBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.appcompat.R as appCompatR
import com.yogadimas.tourismapp.core.R as coreR

class DetailTourismActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailTourismBinding
    private val activityContext = this@DetailTourismActivity

    private val detailTourismViewModel: DetailTourismViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        val detailTourism = getParcelableExtra(intent, EXTRA_DATA, TourismUiModel::class.java)
        showDetailTourism(detailTourism)
    }


    private fun setupView() {
        binding = ActivityDetailTourismBinding.inflate(layoutInflater)
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
        theme.resolveAttribute(appCompatR.attr.colorPrimary, typedValue, true)
        val color = typedValue.data
        val isLight = resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK ==
                Configuration.UI_MODE_NIGHT_NO
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = isLight
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            window.statusBarColor = color
        }


    }


    private fun showDetailTourism(detailTourism: TourismUiModel?) {
        detailTourism?.let {
            setupCollapsingToolbar(detailTourism)
            binding.ivDetailImage.loadImage(it.image)
            setupFavorite(it)
            binding.contentDetailTourism.tvDetailDescription.text = detailTourism.description
        }
    }

    private fun setupCollapsingToolbar(detailTourism: TourismUiModel) {
        binding.toolbar.apply toolbar@{
            setSupportActionBar(this@toolbar)
            setNavigationOnClickListener { finish() }
        }

        binding.toolbarCollapsing.apply {
            title = detailTourism.name
            val boldTypeface = ResourcesCompat.getFont(activityContext, coreR.font.comfortaa_bold)
            val regularTypeface =
                ResourcesCompat.getFont(activityContext, coreR.font.comfortaa_bold)
            setExpandedTitleTypeface(boldTypeface)
            setCollapsedTitleTypeface(regularTypeface)
        }
    }

    private fun ImageView.loadImage(url: String) {
        Glide.with(activityContext)
            .load(url)
            .into(this)
    }

    private fun setupFavorite(data: TourismUiModel) {
        var statusFavorite = data.isFavorite
        setStatusFavorite(statusFavorite)
        binding.fab.setOnClickListener {
            statusFavorite = !statusFavorite
            setFavorite(data, statusFavorite)
        }
    }

    private fun setFavorite(data: TourismUiModel, statusFavorite: Boolean) {
        detailTourismViewModel.setFavoriteTourism(data, statusFavorite)
        setStatusFavorite(statusFavorite)
    }

    private fun setStatusFavorite(statusFavorite: Boolean) {
        binding.fab.setImageDrawable(
            ContextCompat.getDrawable(
                activityContext,
                if (statusFavorite) R.drawable.ic_favorite_checked else R.drawable.ic_favorite_normal
            )
        )
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }
}