package com.stenleone.mysurface.ui.openGl

import android.content.Context
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10

import android.opengl.GLES20.*
import com.stenleone.mysurface.R

import com.stenleone.mysurface.ui.ext.createProgram
import com.stenleone.mysurface.ui.ext.createShader
import android.opengl.GLES20.glUseProgram

import android.opengl.GLES20.GL_FRAGMENT_SHADER

import android.opengl.GLES20.GL_VERTEX_SHADER

class OpenGLThreeAngleRenderer(private val context: Context? = null) : GLSurfaceView.Renderer {

    private var programId = 0
    private var vertexData: FloatBuffer? = null
    private var uColorLocation = 0
    private var aPositionLocation = 0

    init {
        prepareData()
    }

    private fun prepareData() {
        val vertices = floatArrayOf(-0.5f, -0.2f, 0.0f, 0.2f, 0.5f, -0.2f)
        vertexData = ByteBuffer.allocateDirect(vertices.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        vertexData?.put(vertices)
    }

    private fun bindData() {
        uColorLocation = glGetUniformLocation(programId, "u_Color")
        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f)
        aPositionLocation = glGetAttribLocation(programId, "a_Position")
        vertexData!!.position(0)
        glVertexAttribPointer(aPositionLocation, 2, GL_FLOAT, false, 0, vertexData)
        glEnableVertexAttribArray(aPositionLocation)
    }

    override fun onDrawFrame(arg0: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)
        glDrawArrays(GL_TRIANGLES, 0, 3)
    }

    override fun onSurfaceChanged(arg0: GL10?, width: Int, height: Int) {
        glClearColor(0f, 0f, 0f, 1f)
        val vertexShaderId: Int? = context?.createShader(GL_VERTEX_SHADER, R.raw.vertex_shader)
        val fragmentShaderId: Int? = context?.createShader(GL_FRAGMENT_SHADER, R.raw.fragment_shader)

            programId = createProgram(vertexShaderId!!, fragmentShaderId!!)

        glUseProgram(programId)
        bindData()
    }

    override fun onSurfaceCreated(gl: GL10?, config: javax.microedition.khronos.egl.EGLConfig?) {
        glClearColor(0f, 0f, 0f, 1f)
        val vertexShaderId: Int? = context?.createShader(GL_VERTEX_SHADER, R.raw.vertex_shader)
        val fragmentShaderId: Int? = context?.createShader(GL_FRAGMENT_SHADER, R.raw.fragment_shader)

            programId = createProgram(vertexShaderId!!, fragmentShaderId!!)

        glUseProgram(programId)
        bindData()
    }
}