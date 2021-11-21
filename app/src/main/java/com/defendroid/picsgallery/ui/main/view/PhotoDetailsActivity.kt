package com.defendroid.picsgallery.ui.main.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.defendroid.picsgallery.R
import com.defendroid.picsgallery.data.model.Photo
import com.defendroid.picsgallery.utils.AppConstants.KEY_SELECTED_PHOTO
import kotlinx.android.synthetic.main.activity_photo_details.*


class PhotoDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_details)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val photo: Photo? = intent.extras?.getParcelable(KEY_SELECTED_PHOTO)

        photo?.let {

            Glide.with(iv_photo.context)
                .load(it.download_url)
                .error(R.drawable.ic_error)
                .into(iv_photo)

            val author = String.format(getString(R.string.author_string), it.author)
            tv_author_name.text = author
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            R.id.menu_save -> {
                // todo
                true
            }

            R.id.menu_share -> {
                // todo
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}