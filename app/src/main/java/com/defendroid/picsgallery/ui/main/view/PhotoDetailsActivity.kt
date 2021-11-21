package com.defendroid.picsgallery.ui.main.view

import android.content.ClipData
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.defendroid.picsgallery.R
import com.defendroid.picsgallery.data.model.Photo
import com.defendroid.picsgallery.utils.AppConstants
import com.defendroid.picsgallery.utils.AppConstants.KEY_SELECTED_PHOTO
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_photo_details.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class PhotoDetailsActivity : AppCompatActivity() {

    private var photo: Photo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_details)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        photo = intent.extras?.getParcelable(KEY_SELECTED_PHOTO)

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

                CoroutineScope(Dispatchers.IO).launch {
                    val bitmap = Glide.with(this@PhotoDetailsActivity)
                        .asBitmap()
                        .load(photo?.download_url) // sample image
                        .placeholder(android.R.drawable.progress_indeterminate_horizontal) // need placeholder to avoid issue like glide annotations
                        .error(android.R.drawable.stat_notify_error) // need error to avoid issue like glide annotations
                        .submit()
                        .get()

                    saveBitmap(bitmap)
                }
                true
            }

            R.id.menu_share -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val bitmap = Glide.with(this@PhotoDetailsActivity)
                        .asBitmap()
                        .load(photo?.download_url) // sample image
                        .placeholder(android.R.drawable.progress_indeterminate_horizontal) // need placeholder to avoid issue like glide annotations
                        .error(android.R.drawable.stat_notify_error) // need error to avoid issue like glide annotations
                        .submit()
                        .get()

                    val uri = shareImage(bitmap)

                    if (uri == null)
                        Snackbar.make(
                            tv_author_name,
                            getString(R.string.something_wrong_message),
                            Snackbar.LENGTH_LONG
                        ).show()
                    else {
                        val intentShareFile = Intent(Intent.ACTION_SEND)

                        val mimeType = "image/*"
                        val mimeTypeArray = arrayOf(mimeType)
                        intentShareFile.type = mimeType

                        // Add the uri as a ClipData
                        intentShareFile.clipData = ClipData(
                            getString(R.string.share_image_title),
                            mimeTypeArray,
                            ClipData.Item(uri)
                        )

                        // EXTRA_STREAM is kept for compatibility with old applications
                        intentShareFile.putExtra(Intent.EXTRA_STREAM, uri)
                        intentShareFile.putExtra(
                            Intent.EXTRA_SUBJECT,
                            "Sharing File..."
                        )
                        intentShareFile.putExtra(Intent.EXTRA_TEXT, "Image by ${photo?.author}")
                        intentShareFile.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        startActivity(Intent.createChooser(intentShareFile, "Share File"))

                    }
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveBitmap(image: Bitmap): String? {
        var savedImagePath: String? = null
        val imageFileName = "Picsum_${photo?.id}_${photo?.author?.replace(" ", "")}" + ".jpg"
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .toString() + "/PicsGallery"
        )
        var success = true
        if (!storageDir.exists()) {
            success = storageDir.mkdirs()
        }
        if (success) {
            val imageFile = File(storageDir, imageFileName)
            savedImagePath = imageFile.absolutePath
            try {
                val fOut: OutputStream = FileOutputStream(imageFile)
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                fOut.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            addPhotoToGallery(savedImagePath)
        }
        return savedImagePath
    }

    private fun addPhotoToGallery(imagePath: String?) {
        imagePath?.let { path ->
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val f = File(path)
            val contentUri: Uri = Uri.fromFile(f)
            mediaScanIntent.data = contentUri
            sendBroadcast(mediaScanIntent)
            Snackbar.make(
                tv_author_name,
                getString(R.string.image_saved),
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun shareImage(image: Bitmap): Uri? {
        var uri: Uri? = null
        val imageFileName = "Picsum_${photo?.id}_${photo?.author?.replace(" ", "")}" + ".jpg"
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .toString() + "/PicsGallery"
        )
        var success = true
        if (!storageDir.exists()) {
            success = storageDir.mkdirs()
        }
        if (success) {
            val imageFile = File(storageDir, imageFileName)
            try {
                val fOut: OutputStream = FileOutputStream(imageFile)
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                fOut.close()
                uri = FileProvider.getUriForFile(
                    this,
                    AppConstants.AUTHORITY,
                    imageFile
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return uri
    }
}