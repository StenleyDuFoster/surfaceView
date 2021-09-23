package com.stenleone.mysurface.ui.ext

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20.*
import android.opengl.GLUtils


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

    // создание объекта текстуры
    // создание объекта текстуры
    val textureIds = IntArray(1)


    //создаем пустой массив из одного элемента
    //в этот массив OpenGL ES запишет свободный номер текстуры,
    // получаем свободное имя текстуры, которое будет записано в names[0]


    //создаем пустой массив из одного элемента
    //в этот массив OpenGL ES запишет свободный номер текстуры,
    // получаем свободное имя текстуры, которое будет записано в names[0]
    glGenTextures(1, textureIds, 0)
    if (textureIds[0] == 0) {
        return 0
    }

    //This flag is turned on by default and should be turned off if you need a non-scaled version of the bitmap.

    //This flag is turned on by default and should be turned off if you need a non-scaled version of the bitmap.
    val options = BitmapFactory.Options()
    options.inScaled = false
    // получение Bitmap
    // получение Bitmap
    val bitmap = BitmapFactory.decodeResource(
        this.getResources(), resourceId, options
    )

    if (bitmap == null) {
        glDeleteTextures(1, textureIds, 0)
        return 0
    }

    //glActiveTexture — select active texture unit

    //glActiveTexture — select active texture unit
    glActiveTexture(GL_TEXTURE0)
    //делаем текстуру с именем textureIds[0] текущей
    //делаем текстуру с именем textureIds[0] текущей
    glBindTexture(GL_TEXTURE_2D, textureIds[0])

    //учитываем прозрачность текстуры

    //учитываем прозрачность текстуры
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
    glEnable(GL_BLEND)
//включаем фильтры
    //включаем фильтры
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
    //переписываем Bitmap в память видеокарты
    //переписываем Bitmap в память видеокарты
    GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)
    // удаляем Bitmap из памяти, т.к. картинка уже переписана в видеопамять
    // удаляем Bitmap из памяти, т.к. картинка уже переписана в видеопамять
    bitmap.recycle()

    // сброс приязки объекта текстуры к блоку текстуры

    // сброс приязки объекта текстуры к блоку текстуры
    glBindTexture(GL_TEXTURE_2D, 0)
    return textureIds[0]

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