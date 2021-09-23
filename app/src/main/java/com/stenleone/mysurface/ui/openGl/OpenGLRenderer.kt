package com.stenleone.mysurface.ui.openGl

import android.content.Context
import android.opengl.ETC1Util.loadTexture
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import com.stenleone.mysurface.R
import com.stenleone.mysurface.ui.ext.createProgram
import com.stenleone.mysurface.ui.ext.createShader
import com.stenleone.mysurface.ui.ext.loadTexture
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10


class OpenGLRenderer(private val context: Context? = null) : GLSurfaceView.Renderer {

    companion object {
        const val TIME = 1000
    }

    private val POSITION_COUNT = 3
    private val TEXTURE_COUNT = 2
    private val STRIDE = (POSITION_COUNT + TEXTURE_COUNT) * 4

    private var vertexData: FloatBuffer? = null
    private var uColorLocation = 0
    private var aPositionLocation = 0
    private var uMatrixLocation = 0
    private var programId = 0

    private var aTextureLocation = 0
    private var uTextureUnitLocation = 0

    private val mProjectionMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private val mMatrix = FloatArray(16)


    var centerX = 0f
    var centerY = 0f
    var centerZ = 0f

    var upX = 0f
    var upY = 0f
    var upZ = 0f

    var texture: Int = 0

    init {
        prepareData()
    }

    override fun onSurfaceCreated(gl: GL10?, config: javax.microedition.khronos.egl.EGLConfig?) {
        glClearColor(0f, 0f, 0f, 1f)
        glEnable(GL_DEPTH_TEST)
        val vertexShaderId: Int? = context?.createShader(GL_VERTEX_SHADER, R.raw.vertex_shader)
        val fragmentShaderId: Int? = context?.createShader(GL_FRAGMENT_SHADER, R.raw.fragment_shader)
        programId = createProgram(vertexShaderId!!, fragmentShaderId!!)
        glUseProgram(programId)
        getLocations()
        createViewMatrix()
        prepareData()
        bindData()
        bindMatrix()
    }

    override fun onSurfaceChanged(arg0: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        createProjectionMatrix(width, height)
        bindMatrix()
    }

    private fun prepareData() {
        val s = 0.4f
        val d = 0.9f
        val l = 3f
        val vertices = floatArrayOf( // первый треугольник
            -1f, -2f, 3f,
            2 * s, -s, d, 0f, s, d,  // второй треугольник
            -2 * s, -s, -d,
            2 * s, -s, -d, 0f, s, -d,  // третий треугольник
            d, -s, -2 * s,
            d, -s, 2 * s,
            d, s, 0f,  // четвертый треугольник
            -d, -s, -2 * s,
            -d, -s, 2 * s,
            -d, s, 0f,  // ось X
            -l, 0f, 0f,
            l, 0f, 0f, 0f, -l, 0f, 0f, l, 0f, 0f, 0f, -l, 0f, 0f, l,  // up-вектор
            centerX, centerY, centerZ,
            centerX + upX, centerY + upY, centerZ + upZ
        )
        vertexData = ByteBuffer
            .allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexData?.put(vertices)

        texture = context?.loadTexture(R.drawable.vvv)!!
    }

    private fun getLocations() {
        aPositionLocation = glGetAttribLocation(programId, "a_Position")
        aTextureLocation = glGetAttribLocation(programId, "a_Texture")
        uTextureUnitLocation = glGetUniformLocation(programId, "u_TextureUnit")
        uMatrixLocation = glGetUniformLocation(programId, "u_Matrix")
    }

    private fun bindData() {
        // координаты
        aPositionLocation = glGetAttribLocation(programId, "a_Position")
        vertexData!!.position(0)
        glVertexAttribPointer(
            aPositionLocation, POSITION_COUNT, GL_FLOAT,
            false, 0, vertexData
        )
        glEnableVertexAttribArray(aPositionLocation)

        // цвет
        uColorLocation = glGetUniformLocation(programId, "u_Color")

        // матрица
        uMatrixLocation = glGetUniformLocation(programId, "u_Matrix")

        // текстуры
        vertexData?.position(POSITION_COUNT)
        glVertexAttribPointer(
            aTextureLocation, TEXTURE_COUNT, GL_FLOAT,
            false, STRIDE, vertexData
        )
        glEnableVertexAttribArray(aTextureLocation)
    }

    private fun createProjectionMatrix(width: Int, height: Int) {
        var ratio = 1f
        var left = -1f
        var right = 1f
        var bottom = -1f
        var top = 1f
        val near = 2f
        val far = 8f
        if (width > height) {
            ratio = width.toFloat() / height
            left *= ratio
            right *= ratio
        } else {
            ratio = height.toFloat() / width
            bottom *= ratio
            top *= ratio
        }
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far)
    }

    private fun createViewMatrix() {
        val time: Float = (SystemClock.uptimeMillis() % TIME).toFloat() / TIME
        val angle = 1

        // точка положения камеры

        // точка положения камеры
        val eyeX = (Math.cos(angle.toDouble()) * 4f).toFloat()
        val eyeY = 1f
        val eyeZ = 4f

        // точка направления камеры

        // точка направления камеры
        val centerX = 0f
        val centerY = 0f
        val centerZ = 0f

        // up-вектор

        // up-вектор
        val upX = 0f
        val upY = 1f
        val upZ = 0f

        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
    }


    private fun bindMatrix() {
        Matrix.multiplyMM(mMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0)
        glUniformMatrix4fv(uMatrixLocation, 1, false, mMatrix, 0)
    }

    override fun onDrawFrame(arg0: GL10?) {
        createViewMatrix();
        bindMatrix();
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        // треугольники
        glBindTexture(GL_TEXTURE_2D, texture)
        glDrawArrays(GL_TRIANGLE_STRIP, 3, 6)

        // оси
        glLineWidth(1f)
        glUniform4f(uColorLocation, 0.0f, 1.0f, 1.0f, 1.0f)
        glDrawArrays(GL_LINES, 12, 2)
        glUniform4f(uColorLocation, 1.0f, 0.0f, 1.0f, 1.0f)
        glDrawArrays(GL_LINES, 14, 2)
        glUniform4f(uColorLocation, 1.0f, 0.5f, 0.0f, 1.0f)
        glDrawArrays(GL_LINES, 16, 2)

        // up-вектор
        glLineWidth(3f)
        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f)
        glDrawArrays(GL_LINES, 18, 2)
    }
}