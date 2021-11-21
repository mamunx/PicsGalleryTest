package com.defendroid.picsgallery.ui.main.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.defendroid.picsgallery.R
import com.defendroid.picsgallery.data.api.ApiHelper
import com.defendroid.picsgallery.data.api.ApiServiceImpl
import com.defendroid.picsgallery.data.model.Photo
import com.defendroid.picsgallery.ui.base.ItemClickListener
import com.defendroid.picsgallery.ui.base.ViewModelFactory
import com.defendroid.picsgallery.ui.main.adapter.PhotoAdapter
import com.defendroid.picsgallery.ui.main.viewmodel.PhotoViewModel
import com.defendroid.picsgallery.utils.AppConstants.KEY_SELECTED_PHOTO
import com.defendroid.picsgallery.utils.Status
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ItemClickListener {
    private lateinit var viewModel: PhotoViewModel
    private lateinit var adapter: PhotoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupUI()
        setupViewModel()
        setupObserver()
    }

    private fun setupUI() {

        setSupportActionBar(toolbar)

        recyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = PhotoAdapter(arrayListOf(), this)
        recyclerView.adapter = adapter
    }

    private fun setupObserver() {

        viewModel.getPhotoList().observe(this, {
            when (it.status) {
                Status.SUCCESS -> {
                    progressBar.visibility = View.GONE
                    it.data?.let { movies ->
                        renderList(movies)
                    }
                    recyclerView.visibility = View.VISIBLE
                }
                Status.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
                Status.ERROR -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun renderList(movies: List<Photo>) {
        adapter.addData(movies)
        adapter.notifyDataSetChanged()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(ApiServiceImpl()))
        ).get(PhotoViewModel::class.java)
    }

    override fun onItemClicked(item: Any?) {
        if (item != null && item is Photo) {
            val intent = Intent(this, PhotoDetailsActivity::class.java)
            intent.putExtra(KEY_SELECTED_PHOTO, item)
            startActivity(intent)
        }
    }
}