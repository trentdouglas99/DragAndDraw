package com.csci448.trentdouglas.draganddraw

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.annotation.ColorRes

class BoxDrawingView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private var BOUNCE_FACTOR = -.75f
    val LOG_TAG = "BoxDrawingView: "
    private val scaleListener: ScaleGestureDetector = ScaleGestureDetector(context, ScaleListener(this))
    private var currentBox: Box? = null
    private val BUNDLE_SAVE_BOX_POINTS = "bundleSaveBoxPoints"
    private val BUNDLE_SAVE_PARENT_STATE = "parceable"
    private val boxen = mutableListOf<Box>()
    private var boxVelocity:PointF = PointF(0f,0f)
    private var boxAcceleration:PointF = PointF(0f, 0f)
    private var priorTime = System.currentTimeMillis()
    private var ball = Ball(PointF(100f,100f), 50f)
    private var box = Box(PointF(200f, 200f), PointF(300f, 300f) )
    private var light = 0
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
        boxAcceleration.x = acceleration.x/10000
        boxAcceleration.y = acceleration.y/10000
    }

    public fun setLight(lightValue:Float){
        light = lightValue.toInt()
        Log.d(LOG_TAG, "${light}")
    }



    override fun onDraw(canvas: Canvas) {

        canvas.scale(scaleFactor, scaleFactor)
        canvas.drawPaint(backgroundPaint)
//        boxen.forEach { box ->
//            canvas.drawRect(box.left, box.top, box.right, box.bottom, boxPaint)
//        }
        //canvas.drawCircle(ball.center.x, ball.center.y, ball.radius, greenBrush)
        canvas.drawRect(box.left, box.top, box.right, box.bottom, greenBrush)
        updateBoxPosition()
        checkForDark()
    }

    private fun checkForDark(){
        if(light < 20000){
            backgroundPaint.color = Color.BLACK
            Log.d(LOG_TAG,"dark mode")
        }
        else{
            backgroundPaint.color = Color.WHITE
            Log.d(LOG_TAG,"light mode")
        }
        invalidate()
    }

    private fun updateBoxPosition(){

        //Log.d(LOG_TAG, "acceleration x: ${ballAcceleration.x}, acceleration y: ${ballAcceleration.y}")
        var currentTime = System.currentTimeMillis()
        var elapsedTime = currentTime - priorTime
        priorTime = currentTime
        boxVelocity.x += 0-(boxAcceleration.x * elapsedTime)
        boxVelocity.y += boxAcceleration.y * elapsedTime
        box.start.x += boxVelocity.x * elapsedTime
        box.start.y += boxVelocity.y * elapsedTime
        box.end.x += boxVelocity.x * elapsedTime
        box.end.y += boxVelocity.y * elapsedTime

        if(box.start.x <= 0f){
            box.start.x = 0f
            boxVelocity.x *= BOUNCE_FACTOR
        }
        if(box.end.x >= width/scaleFactor) {
            box.end.x = (width/scaleFactor)
            boxVelocity.x *= BOUNCE_FACTOR
        }
        if(box.start.y <= 0f) {
            box.start.y = 0f
            boxVelocity.y *= BOUNCE_FACTOR
        }
        if(box.end.y >= height/scaleFactor) {
            box.end.y = (height/scaleFactor)
            boxVelocity.y *= BOUNCE_FACTOR
        }



        invalidate()
    }





//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        scaleListener.onTouchEvent(event)
//        if(!scaleListener.isInProgress) {
//            val current = PointF(event.x/scaleFactor, event.y/scaleFactor)
//            var action = ""
//            when (event.action){
//                MotionEvent.ACTION_DOWN -> {
//                    action = "ACTION_DOWN"
//                    currentBox = Box(current).also{
//                        boxen.add(it)
//                    }
//                }
//                MotionEvent.ACTION_MOVE -> {
//                    action = "ACTION_MOVE"
//                    updateCurrentBox(current)
//                }
//                MotionEvent.ACTION_UP ->{
//                    action = "ACTION_UP"
//                    updateCurrentBox(current)
//                    currentBox = null
//                }
//                MotionEvent.ACTION_CANCEL->{
//                    action = "ACTION_CANCEL"
//                    currentBox = null
//                }
//            }
//            Log.d(LOG_TAG, "$$action at x=${current.x}, y=$current.y}")
//        }
//
//        return true
//    }


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
//        val inState = state as Bundle
//        val parentState: Parcelable? = inState.getParcelable(BUNDLE_SAVE_PARENT_STATE)
//        super.onRestoreInstanceState(parentState)
//        val boxPoints: List<Float> = inState.getFloatArray(BUNDLE_SAVE_BOX_POINTS)?.toList() ?: listOf()
//
//        var box:Box
//        var start:PointF
//        var end:PointF
//        for (i in 0..boxPoints.size-4 step 4){
//            start = PointF(boxPoints[i], boxPoints[i+1])
//            end = PointF(boxPoints[i+2], boxPoints[i+3])
//            box = Box(start)
//            box.end = end
//            boxen.add(box)
//        }

    }



}