package com.akrivonos.app_kotlin

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.*
import com.akrivonos.app_kotlin.Api.RetrofitSearchDownload
import com.akrivonos.app_kotlin.Models.Rsp

class MainActivity : AppCompatActivity() {

    private lateinit var searchResultTextView: TextView
    private lateinit var searchRequestEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var progressBar: ProgressBar
    private val startSearch = View.OnClickListener {
        val searchText = searchRequestEditText.text.toString()
        if (!TextUtils.isEmpty(searchText)) {
            progressBar.visibility = View.VISIBLE
            searchResultTextView.visibility = View.GONE
            searchButton.isClickable = false
            RetrofitSearchDownload.startDownloadPictures(searchText)
        } else {
            Toast.makeText(this@MainActivity, getString(R.string.empty_field), Toast.LENGTH_SHORT).show()
        }
    }
    private val observerPhotosSearch = Observer<Rsp> { photosSearchClass ->
        if (photosSearchClass != null) {
            progressBar.visibility = View.GONE
            searchResultTextView.visibility = View.VISIBLE
            searchButton.isClickable = true
            searchResultTextView.text = ""
            val photos = photosSearchClass.photos.photo
            for (photo in photos) {
                val photoUrl = getPhotoUrl(photo)

                val string = SpannableString(photoUrl)
                string.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        startActivity(Intent(this@MainActivity, LinkContentActivity::class.java).putExtra(SPAN_URL, photoUrl))
                    }
                }, 0, photoUrl.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                searchResultTextView.append(string)
                searchResultTextView.append("\n")
            }
        } else {
            Toast.makeText(this@MainActivity, getString(R.string.error_download), Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.GONE
            searchButton.isClickable = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressBar = findViewById(R.id.progressBar)
        searchRequestEditText = findViewById(R.id.search_request)
        searchButton = findViewById(R.id.search_button)
        searchResultTextView = findViewById(R.id.search_result)
        searchButton.setOnClickListener(startSearch)

        searchResultTextView.movementMethod = LinkMovementMethod.getInstance()

        val liveData: LiveData<Rsp> = RetrofitSearchDownload.dataBinder
        liveData.observe(this, observerPhotosSearch)
    }

    private fun getPhotoUrl(photo: Rsp.Photo): String {
        val farm = photo.farm
        val server = photo.server
        val id = photo.id
        val secret = photo.secret
        return "https://farm$farm.staticflickr.com/$server/$id" + "_$secret.jpg"
    }

    companion object {
        const val SPAN_URL = "span_url"
    }
}
