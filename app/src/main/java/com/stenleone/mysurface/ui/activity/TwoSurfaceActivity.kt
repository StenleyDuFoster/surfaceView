package com.stenleone.mysurface.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Button
import androidx.activity.ComponentActivity
import com.stenleone.mysurface.R
import com.stenleone.mysurface.ui.surface.SurfaceThreadAndroid

class TwoSurfaceActivity : ComponentActivity() {

    val surfaceThreadList = arrayListOf<SurfaceThreadAndroid>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setupSurface(R.id.surface)
        setupSurface(R.id.surface2)
        setupClicks()
    }

    private fun setupClicks() {
        findViewById<Button>(R.id.buttonClear).setOnClickListener {
            surfaceThreadList.forEach {
                it.clearItems()
            }
        }
        findViewById<Button>(R.id.nextButton).setOnClickListener {
            startActivity(Intent(this, OpenGlActivity::class.java))
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupSurface(surfaceId: Int) {
        val surfaceView = findViewById<SurfaceView>(surfaceId)
        var surfaceThread: SurfaceThreadAndroid? = null

        surfaceView.setOnTouchListener { _, event ->
            if (event?.action == MotionEvent.ACTION_DOWN) {
                surfaceThread?.addItem(event.x.toInt(), event.y.toInt())
            }
            return@setOnTouchListener true
        }
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                surfaceThread = SurfaceThreadAndroid(
                    holder, BitmapFactory.decodeResource(
                        resources, R.drawable.android
                    )
                ).also { surfaceThreadList.add(it) }

                surfaceThread?.start()
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                surfaceThread?.updateSize(width, height)
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                surfaceThread?.quit()
                surfaceThreadList.remove(surfaceThread)
                surfaceThread = null
            }
        })
    }
}