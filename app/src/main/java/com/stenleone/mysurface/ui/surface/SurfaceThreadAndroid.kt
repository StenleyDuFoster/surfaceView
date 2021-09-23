package com.stenleone.mysurface.ui.surface

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.view.SurfaceHolder
import com.stenleone.mysurface.ui.surface.data.DrawingItem
import kotlin.math.roundToLong

class SurfaceThreadAndroid(private val surfaceHolder: SurfaceHolder, private val icon: Bitmap) : HandlerThread("DrawingThread"), Handler.Callback {

    companion object {
        private const val MSG_ADD = 0
        private const val MSG_MOVE = 1
        private const val MSG_CLEAR = 2
    }

    private var drawingWidth = 0
    private var drawingHeight = 0
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var handler: Handler? = null
    private val locations = ArrayList<DrawingItem>()

    override fun onLooperPrepared() {
        handler = Handler(looper, this)
        handler?.sendEmptyMessage(MSG_MOVE)
    }

    override fun quit(): Boolean {
        handler?.removeCallbacksAndMessages(null)
        return super.quit()
    }

    override fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            MSG_ADD -> {
                val newItem = DrawingItem(msg.arg1, msg.arg2, Math.random().roundToLong() == 0L, Math.random().roundToLong() == 0L)
                locations.add(newItem)
            }
            MSG_CLEAR -> locations.clear()
            MSG_MOVE -> {
                val canvas = surfaceHolder.lockCanvas()
                canvas?.drawColor(Color.BLACK)
                locations.forEach { item ->
                    moveItem(item)
                    canvas?.drawBitmap(icon, item.x.toFloat(), item.y.toFloat(), paint)
                }
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas)
                }
            }
        }
        handler?.sendEmptyMessage(MSG_MOVE)
        return true
    }

    private fun moveItem(item: DrawingItem) {
        item.x += if (item.horizontal) 5 else -5
        if (item.x >= drawingWidth - icon.width) {
            item.horizontal = false
        } else if (item.x <= 0) {
            item.horizontal = true
        }
        item.y += if (item.vertical) 5 else -5
        if (item.y >= drawingHeight - icon.height) {
            item.vertical = false
        } else if (item.y <= 0) {
            item.vertical = true
        }
    }

    fun updateSize(width: Int, height: Int) {
        drawingWidth = width
        drawingHeight = height
    }

    fun addItem(x: Int, y: Int) {
        val msg = Message.obtain(handler, MSG_ADD, x, y)
        handler?.sendMessage(msg)
    }

    fun clearItems() {
        handler?.sendEmptyMessage(MSG_CLEAR)
    }
}