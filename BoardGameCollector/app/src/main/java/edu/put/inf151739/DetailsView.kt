package edu.put.inf151739

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.squareup.picasso.Picasso
import java.io.File

class DetailsView : AppCompatActivity() {
    private lateinit var mGetContent: ActivityResultLauncher<String>
    private lateinit var photoContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_view)

        val gameId = intent.getStringExtra("gameId")
        val gameTitle = intent.getStringExtra("gameTitle")
        val gameYear = intent.getStringExtra("gameYear")
        val gamePhoto = intent.getStringExtra("gamePhoto")

        val gameIdTextView = findViewById<TextView>(R.id.game_id_text)
        val gameTitleTextView = findViewById<TextView>(R.id.game_title_text)
        val gameYearTextView = findViewById<TextView>(R.id.game_year_text)
        val gamePhotoImageView = findViewById<ImageView>(R.id.game_photo)

        gameIdTextView.text = gameId
        gameTitleTextView.text = gameTitle
        gameYearTextView.text = gameYear
        Picasso.get().load(gamePhoto).into(gamePhotoImageView)

        photoContainer = findViewById(R.id.photo_container)
        val button: Button = findViewById(R.id.take_photo_button)
        val tempImageUri = initTempUri()

        val resultLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            val addedImageView = ImageView(this)
            addedImageView.setImageBitmap(scaleBitmap(getCapturedImage(tempImageUri), 200))
            photoContainer.addView(addedImageView)
        }

        button.setOnClickListener {
            resultLauncher.launch(tempImageUri)
        }

        val buttonGallery: Button = findViewById(R.id.add_photo_button)
        mGetContent = registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
            if (result != null) {
                val bitmap = when {
                    Build.VERSION.SDK_INT < 28 -> MediaStore.Images.Media.getBitmap(
                        this.contentResolver,
                        result
                    )
                    else -> {
                        val source = ImageDecoder.createSource(this.contentResolver, result)
                        ImageDecoder.decodeBitmap(source)
                    }
                }
                val addedImageView = ImageView(this)
                addedImageView.setImageBitmap(scaleBitmap(bitmap, 200))
                photoContainer.addView(addedImageView)
            }
        }

        buttonGallery.setOnClickListener {
            mGetContent.launch("image/*")
        }
    }

    private fun initTempUri(): Uri {
        val tempImagesDir = File(applicationContext.filesDir, getString(R.string.temp_images_dir))
        tempImagesDir.mkdir()
        val tempImage = File(tempImagesDir, getString(R.string.temp_image))
        return FileProvider.getUriForFile(
            applicationContext,
            getString(R.string.authorities),
            tempImage
        )
    }

    private fun scaleBitmap(bitmap: Bitmap, width: Int): Bitmap {
        val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
        val scaledHeight = (width / aspectRatio).toInt()
        return Bitmap.createScaledBitmap(bitmap, width, scaledHeight, true)
    }

    private fun getCapturedImage(selectedPhotoUri: Uri): Bitmap {
        val bitmap = when {
            Build.VERSION.SDK_INT < 28 -> MediaStore.Images.Media.getBitmap(
                this.contentResolver,
                selectedPhotoUri
            )
            else -> {
                val source = ImageDecoder.createSource(this.contentResolver, selectedPhotoUri)
                ImageDecoder.decodeBitmap(source)
            }
        }
        return scaleBitmap(bitmap, 250)
    }
}
