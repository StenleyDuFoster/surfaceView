package com.stenleone.mysurface.ui.ext

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLES20

import android.opengl.GLUtils

import android.graphics.BitmapFactory

import android.graphics.Bitmap
import java.lang.RuntimeException


fun createProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {
    val programId = glCreateProgram()
    if (programId == 0) {
        return 0
    }
    glAttachShader(programId, vertexShaderId)
    glAttachShader(programId, fragmentShaderId)
    glLinkProgram(programId)
    val linkStatus = IntArray(1)
    glGetProgramiv(programId, GL_LINK_STATUS, linkStatus, 0)
    if (linkStatus[0] == 0) {
        glDeleteProgram(programId)
        return 0
    }
    return programId
}

fun Context.createShader(type: Int, shaderRawId: Int): Int {
    val shaderText: String? = this.readTextFromRaw(shaderRawId)
    return createShader(type, shaderText)
}

fun Context.loadTexture(resourceId: Int): Int {
    val textureHandle = IntArray(1)
    glGenTextures(1, textureHandle, 0)
    if (textureHandle[0] != 0) {
        val options = BitmapFactory.Options()
        options.inScaled = false // No pre-scaling

        // Read in the resource
        val bitmap = BitmapFactory.decodeResource(this.resources, resourceId, options)

        // Bind to the texture in OpenGL
        glBindTexture(GL_TEXTURE_2D, textureHandle[0])

        // Set filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)

        // Recycle the bitmap, since its data has been loaded into OpenGL.
        bitmap.recycle()
    }
    if (textureHandle[0] == 0) {
        throw RuntimeException("Error loading texture.")
    }
    return textureHandle[0]
}

fun createShader(type: Int, shaderText: String?): Int {
    val shaderId = glCreateShader(type)
    if (shaderId == 0) {
        return 0
    }
    glShaderSource(shaderId, shaderText)
    glCompileShader(shaderId)
    val compileStatus = IntArray(1)
    glGetShaderiv(shaderId, GL_COMPILE_STATUS, compileStatus, 0)
    if (compileStatus[0] == 0) {
        glDeleteShader(shaderId)
        return 0
    }
    return shaderId
}