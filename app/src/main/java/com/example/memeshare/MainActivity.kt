
package com.example.memeshare

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target


class MainActivity : AppCompatActivity() {
    var ImageUrl: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try {
            loadMeme()
        } catch (e: Exception) {
            println(e)
        }
    }

    class MySingleton constructor(context: Context) {
        val requestQueue: RequestQueue by lazy {
            Volley.newRequestQueue(context.applicationContext)
        }
        fun <T> addToRequestQueue(req: Request<T>) {
            requestQueue.add(req)
        }

        companion object {
            @Volatile
            var INSTANCE: MySingleton? = null
            fun getInstance(context: Context) =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: MySingleton(context).also {
                        INSTANCE = it
                    }
                }
        }

    }

    private fun loadMeme() {
        val PBar = findViewById<ProgressBar>(R.id.PBar)
        val url = "https://meme-api.herokuapp.com/gimme"
        val MemeImage = findViewById<ImageView>(R.id.meme)
//        val queue = MySingleton.getInstance(this.applicationContext).requestQueue
        PBar.visibility = View.VISIBLE
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                ImageUrl = response.getString("url")

                Glide.with(this).load(ImageUrl).listener(object: RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        PBar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        PBar.visibility = View.GONE
                        return false
                    }
                }).into(MemeImage)
            },
            { error ->
                Toast.makeText(this,"Connect to Network !!! ", Toast.LENGTH_LONG).show()
            }
        )
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }


    fun nextmeme(view: View) {
        loadMeme()
    }


    fun sharememe(view: View) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_MIME_TYPES,  "$ImageUrl");
        val chooser = Intent.createChooser(intent, "Share this meme using ")
        startActivity(chooser)
    }


}

