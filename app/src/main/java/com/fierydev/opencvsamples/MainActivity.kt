package com.fierydev.opencvsamples

import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var seekbarBrightness: SeekBar
    private lateinit var switchFilter: SwitchCompat

    private var imageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        seekbarBrightness = findViewById(R.id.seekbarBrightness)
        switchFilter = findViewById(R.id.switchFilter)

        // Initialize OpenCV
        OpenCVLoader.initDebug()

        switchFilter.setOnCheckedChangeListener { compoundButton, b ->
            updateImage()
        }

        seekbarBrightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateImage()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // empty
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // empty
            }
        })

        seekbarBrightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateImage()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // empty
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // empty
            }
        })
    }

    private fun updateImage() {
        // Calculate brightness based on SeekBar progress
        val brightness = mapSeekBarProgressToValue(seekbarBrightness.progress, -100.0, 0.0)

        // Apply brightness to the image
        processImage(brightness, switchFilter.isChecked)
    }


    private fun mapSeekBarProgressToValue(
        progress: Int,
        minValue: Double,
        maxValue: Double
    ): Double {
        val range = maxValue - minValue
        return minValue + (progress / 100.0) * range
    }

    // Button click handler to capture or select an image
    fun onCaptureOrSelectImageClick(view: View) {
        imagePickerResult.launch("image/*")
    }

    private val imagePickerResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                processImage()
            }
        }

    // Process the captured or selected image with OpenCV and display the result
    private fun processImage(
        brightness: Double = -30.0,
        isGrayScale: Boolean = false
    ) {
        // Step 1: Create a Mat object to hold the image data
        val imageMat = Mat()
        // Step 2: Convert the input imageBitmap to a Mat object (OpenCV format)
        Utils.bitmapToMat(imageBitmap, imageMat)
        // Step 3: Create a new Mat object named grayMat to hold the grayscale version of the image
        var grayMat = Mat()
        // Step 4: Convert the color image (BGR) to grayscale
        if (isGrayScale)
            Imgproc.cvtColor(imageMat, grayMat, Imgproc.COLOR_BGR2GRAY) //filter changes.
        else grayMat = imageMat
        // Step 5: Adjust the brightness of the grayscale image (optional)
        Core.add(grayMat, Scalar.all(brightness), grayMat) // Adjust brightness (optional)
        // Step 6: Create a new Bitmap named bwBitmap to hold the final filtered image
        val bwBitmap = Bitmap.createBitmap(grayMat.cols(), grayMat.rows(), Bitmap.Config.RGB_565)
        // Step 7: Convert the filtered grayscale Mat back to a Bitmap format
        Utils.matToBitmap(grayMat, bwBitmap)
        // Step 8: Set the filtered Bitmap (bwBitmap) to be displayed in the ImageView
        imageView.setImageBitmap(bwBitmap)
    }
}