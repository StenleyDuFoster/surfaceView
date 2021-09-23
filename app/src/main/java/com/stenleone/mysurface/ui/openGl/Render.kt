package com.stenleone.mysurface.ui.openGl

import android.content.Context
import android.opengl.EGLConfig
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


class Render(private var context: Context?) : GLSurfaceView.Renderer {

    private val POSITION_COUNT = 3
    private val TEXTURE_COUNT = 2
    private val STRIDE = (POSITION_COUNT
            + TEXTURE_COUNT) * 4

    private var vertexData: FloatBuffer? = null

    private var aPositionLocation = 0
    private var aTextureLocation = 0
    private var uTextureUnitLocation = 0
    private var uMatrixLocation = 0

    private var programId = 0

    private val mProjectionMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private val mMatrix = FloatArray(16)
    private val mModelMatrix = FloatArray(16)

    private var texture = 0
    private var texture1 = 0
    private var texture2 = 0
    private val TIME = 10000L


    fun OpenGLRenderer(context: Context?) {
        this.context = context
    }

    override fun onSurfaceCreated(arg0: GL10?, arg1: javax.microedition.khronos.egl.EGLConfig?) {
        glClearColor(0f, 0f, 0f, 1f)
        glEnable(GL_DEPTH_TEST)
        createAndUseProgram()
        getLocations()
        prepareData()
        bindData()
        createViewMatrix()
    }

    override fun onSurfaceChanged(arg0: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        createProjectionMatrix(width, height)
        bindMatrix()
    }

    private fun prepareData() {
        val vertices = floatArrayOf(
            -2f,
            4f,
            0f,
            0f,
            0f,
            -2f,
            0f,
            0f,
            0f,
            0.5f,
            2f,
            4f,
            0f,
            0.5f,
            0f,
            2f,
            0f,
            0f,
            0.5f,
            0.5f,
            -2f,
            0f,
            0f,
            0.5f,
            0f,
            -2f,
            -1f,
            2f,
            0.5f,
            0.5f,
            2f,
            0f,
            0f,
            1f,
            0f,
            2f,
            -1f,
            2f,
            1f,
            0.5f,
            -1f,
            1f,
            0.5f,
            0f,
            0.5f,
            -1f,
            -1f,
            0.5f,
            0f,
            1f,
            1f,
            1f,
            0.5f,
            0.5f,
            0.5f,
            1f,
            -1f,
            0.5f,
            0.5f,
            1f
        )
        vertexData = ByteBuffer
            .allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexData?.put(vertices)
        texture = context?.loadTexture(R.drawable.earth)!!
        texture1 = context?.loadTexture(R.drawable.earth)!!
        texture2 = context?.loadTexture(R.drawable.earth)!!
    }

    private fun createAndUseProgram() {
        val vertexShaderId: Int? = context?.createShader(GL_VERTEX_SHADER, R.raw.vertex_shader)
        val fragmentShaderId: Int? = context?.createShader(GL_FRAGMENT_SHADER, R.raw.fragment_shader)
        programId = createProgram(vertexShaderId!!, fragmentShaderId!!)
        glUseProgram(programId)
    }

    private fun getLocations() {
        aPositionLocation = glGetAttribLocation(programId, "a_Position")
        aTextureLocation = glGetAttribLocation(programId, "a_Texture")
        uTextureUnitLocation = glGetUniformLocation(programId, "u_TextureUnit")
        uMatrixLocation = glGetUniformLocation(programId, "u_Matrix")
    }

    private fun bindData() {
        // координаты вершин
        vertexData?.position(0)
        glVertexAttribPointer(
            aPositionLocation, POSITION_COUNT, GL_FLOAT,
            false, STRIDE, vertexData
        )
        glEnableVertexAttribArray(aPositionLocation)


        // координаты текстур
        vertexData?.position(POSITION_COUNT)
        glVertexAttribPointer(
            aTextureLocation, TEXTURE_COUNT, GL_FLOAT,
            false, STRIDE, vertexData
        )
        glEnableVertexAttribArray(aTextureLocation)


        // помещаем текстуру в target 2D юнита 0
//        glActiveTexture(GL_TEXTURE0);
//        glBindTexture(GL_TEXTURE_2D, texture);


        // помещаем текстуру1 в target 2D юнита 0
//        glActiveTexture(GL_TEXTURE0);
//        glBindTexture(GL_TEXTURE_2D, texture1);

        // помещаем текстуру2 в target 2D юнита 0

        //glActiveTexture(GL_TEXTURE0);
        //glBindTexture(GL_TEXTURE_2D, texture2);

        // юнит текстуры
        //glUniform1i(uTextureUnitLocation, 0);
    }

    private fun createProjectionMatrix(width: Int, height: Int) {
        var ratio = 1f
        var left = -0.5f
        var right = 0.5f
        var bottom = -0.5f
        var top = 0.5f
        val near = 2f
        val far = 12f
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
        // точка полоения камеры
        val eyeX = 0f
        val eyeY = 2f
        val eyeZ = 7f

        // точка направления камеры
        val centerX = 0f
        val centerY = 1f
        val centerZ = 0f

        // up-вектор
        val upX = 0f
        val upY = 1f
        val upZ = 0f
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
    }


    private fun bindMatrix() {
        Matrix.multiplyMM(mMatrix, 0, mViewMatrix, 0, mModelMatrix, 0)
        Matrix.multiplyMM(mMatrix, 0, mProjectionMatrix, 0, mMatrix, 0)
        glUniformMatrix4fv(uMatrixLocation, 1, false, mMatrix, 0)
    }

    override fun onDrawFrame(arg0: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

//сбрасываем model матрицу
        Matrix.setIdentityM(mModelMatrix, 0)
        bindMatrix()
        glBindTexture(GL_TEXTURE_2D, texture)
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
        glBindTexture(GL_TEXTURE_2D, texture1)
        glDrawArrays(GL_TRIANGLE_STRIP, 4, 4)
        glBindTexture(GL_TEXTURE_2D, texture2)
        Matrix.setIdentityM(mModelMatrix, 0)
        setModelMatrix()
        bindMatrix()
        glDrawArrays(GL_TRIANGLE_STRIP, 8, 4)
    }


    private fun setModelMatrix() {
        Matrix.translateM(mModelMatrix, 0, 0f, -0.5f, 0f)
        //В переменной angle угол будет меняться  от 0 до 360 каждые 10 секунд.
        val angle = (-(SystemClock.uptimeMillis() % TIME)).toFloat() / TIME * 360
        //void rotateM (float[] m,  int mOffset, float a,float x, float y, float z)
        //Rotates matrix m in place by angle a (in degrees) around the axis (x, y, z).
        Matrix.rotateM(mModelMatrix, 0, angle, 0f, 0f, 1f)
        Matrix.translateM(mModelMatrix, 0, -0.8f, 0f, 0f)
    }


}