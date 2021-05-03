package com.csci448.trentdouglas.draganddraw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View

class BoxDrawingView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private var BOUNCE_FACTOR = -.75f
    val LOG_TAG = "BoxDrawingView: "
    private val scaleListener: ScaleGestureDetector = ScaleGestureDetector(context, ScaleListener(this))
    private var currentBox: Box? = null
    private val BUNDLE_SAVE_BOX_POINTS = "bundleSaveBoxPoints"
    private val BUNDLE_SAVE_PARENT_STATE = "parceable"
    private val boxen = mutableListOf<Box>()
    private var ballVelocity:PointF = PointF(0f,0f)
    private var ballAcceleration:PointF = PointF(0f, 0f)
    private var priorTime = System.currentTimeMillis()
    private var ball = Ball(PointF(100f,100f), 50f)
    public var scaleFactor = 1.0f
    val displayMetrics = DisplayMetrics()
    private val boxPaint = Paint().apply{
        color = Color.RED
        alpha = 45
    }
    private val greenBrush = Paint().apply{
        color = Color.GREEN
        alpha = 255
    }
    private val backgroundPaint = Paint().apply {
        color = Color.BLACK
    }

    public fun setAcceleration(acceleration: PointF){
        ballAcceleration.x = acceleration.x/10000
        ballAcceleration.y = acceleration.y/10000
    }


    override fun onDraw(canvas: Canvas) {

        canvas.scale(scaleFactor, scaleFactor)
        canvas.drawPaint(backgroundPaint)
        boxen.forEach { box ->
            canvas.drawRect(box.left, box.top, box.right, box.bottom, boxPaint)
        }
        canvas.drawCircle(ball.center.x, ball.center.y, ball.radius, greenBrush)
        updateBallPosition()
    }

    private fun updateBallPosition(){

        Log.d(LOG_TAG, "acceleration x: ${ballAcceleration.x}, acceleration y: ${ballAcceleration.y}")
        var currentTime = System.currentTimeMillis()
        var elapsedTime = currentTime - priorTime
        priorTime = currentTime
        ballVelocity.x += 0-(ballAcceleration.x * elapsedTime)
        ballVelocity.y += ballAcceleration.y * elapsedTime
        ball.center.x += ballVelocity.x * elapsedTime
        ball.center.y += ballVelocity.y * elapsedTime

        if(ball.center.x - ball.radius <= 0f){
            ball.center.x = 0f + ball.radius
            ballVelocity.x *= BOUNCE_FACTOR
        }
        if(ball.center.x + ball.radius >= width/scaleFactor) {
            ball.center.x = (width/scaleFactor).toFloat()-ball.radius
            ballVelocity.x *= BOUNCE_FACTOR
        }
        if(ball.center.y - ball.radius <= 0f) {
            ball.center.y = 0f + ball.radius
            ballVelocity.y *= BOUNCE_FACTOR
        }
        if(ball.center.y + ball.radius >= height/scaleFactor) {
            ball.center.y = (height/scaleFactor).toFloat()-ball.radius
            ballVelocity.y *= BOUNCE_FACTOR
        }



        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleListener.onTouchEvent(event)
        if(!scaleListener.isInProgress) {
            val current = PointF(event.x/scaleFactor, event.y/scaleFactor)
            var action = ""
            when (event.action){
                MotionEvent.ACTION_DOWN -> {
                    action = "ACTION_DOWN"
                    currentBox = Box(current).also{
                        boxen.add(it)
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    action = "ACTION_MOVE"
                    updateCurrentBox(current)
                }
                MotionEvent.ACTION_UP ->{
                    action = "ACTION_UP"
                    updateCurrentBox(current)
                    currentBox = null
                }
                MotionEvent.ACTION_CANCEL->{
                    action = "ACTION_CANCEL"
                    currentBox = null
                }
            }
            Log.d(LOG_TAG, "$$action at x=${current.x}, y=$current.y}")
        }

        return true
    }


    private fun updateCurrentBox(current: PointF){
        currentBox?.let{
            it.end = current
            invalidate()
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        Log.d(LOG_TAG, "onSaveInstanceState() called")
        val outState = Bundle()
        val parentState = super.onSaveInstanceState()
        outState.putParcelable(BUNDLE_SAVE_PARENT_STATE, parentState)
        var boxPoints = mutableListOf<Float>()
        boxen.forEach { box ->
            boxPoints.add(box.left)
            boxPoints.add(box.top)
            boxPoints.add(box.right)
            boxPoints.add(box.bottom)
        }

        outState.putFloatArray(BUNDLE_SAVE_BOX_POINTS, boxPoints.toFloatArray())

        return outState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if(state == null) {
            super.onRestoreInstanceState(state)
            return
        }
        val inState = state as Bundle
        val parentState: Parcelable? = inState.getParcelable(BUNDLE_SAVE_PARENT_STATE)
        super.onRestoreInstanceState(parentState)
        val boxPoints: List<Float> = inState.getFloatArray(BUNDLE_SAVE_BOX_POINTS)?.toList() ?: listOf()

        var box:Box
        var start:PointF
        var end:PointF
        for (i in 0..boxPoints.size-4 step 4){
            start = PointF(boxPoints[i], boxPoints[i+1])
            end = PointF(boxPoints[i+2], boxPoints[i+3])
            box = Box(start)
            box.end = end
            boxen.add(box)
        }

    }



}