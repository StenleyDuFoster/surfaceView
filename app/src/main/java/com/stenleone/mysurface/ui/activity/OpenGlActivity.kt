package com.stenleone.mysurface.ui.activity

import android.os.Bundle
import android.opengl.GLSurfaceView

import android.widget.Toast
import androidx.activity.ComponentActivity
import com.stenleone.mysurface.ui.ext.supportES2
import com.stenleone.mysurface.ui.openGl.OpenGLCubeRender
import com.stenleone.mysurface.ui.openGl.OpenGLRenderer
import com.stenleone.mysurface.ui.openGl.OpenGLThreeAngleRenderer

class OpenGlActivity : ComponentActivity() {

    private lateinit var glSurfaceView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupSurface()

        setContentView(glSurfaceView)
    }

    private fun setupSurface() {
        if (!supportES2()) {
            Toast.makeText(this, "OpenGl ES 2.0 is not supported", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        glSurfaceView = GLSurfaceView(this)
        glSurfaceView.setEGLContextClientVersion(2)
        glSurfaceView.setRenderer(OpenGLCubeRender(this))
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }

}