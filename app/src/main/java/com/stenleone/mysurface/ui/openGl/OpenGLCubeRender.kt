package com.stenleone.mysurface.ui.openGl

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.stenleone.mysurface.ui.openGl.data.Cube
import com.stenleone.mysurface.ui.openGl.data.CubeObject
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class OpenGLCubeRender(private val context: Context) : GLSurfaceView.Renderer {

    private var programId: Int = 0

    init {
        programId = GLES20.glCreateProgram()
    }

    private val cubeObj = Cube()

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        GLES20.glClearColor(1f, 1f, 1f, 0f)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        cubeObj.draw(gl)
    }
}