package com.csci448.trentdouglas.trentdouglas_a4

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.ScaleGestureDetector
import android.view.View


class BoxDrawingView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private var BOUNCE_FACTOR = -1f
    val LOG_TAG = "BoxDrawingView: "
    private val scaleListener: ScaleGestureDetector = ScaleGestureDetector(
        context, ScaleListener(
            this
        )
    )
    private val list_of_dvds = arrayListOf(R.drawable.dvd_green, R.drawable.dvd_yellow, R.drawable.dvd_white, R.drawable.dvd_red)
    private var list_of_dvds_index = 0
    private var currentBox: Box? = null
    private val BUNDLE_SAVE_BOX_POINTS = "bundleSaveBoxPoints"
    private val BUNDLE_SAVE_PARENT_STATE = "parceable"
    private val boxen = mutableListOf<Box>()
    private var boxVelocity:PointF = PointF(0f, 0f)
    private var boxAcceleration:PointF = PointF(0f, 0f)
    private var priorTime = System.currentTimeMillis()
    private var ball = Ball(PointF(100f, 100f), 50f)
    private var box = Box(PointF(200f, 200f), PointF(400f, 400f))
    private var light = 0
    private var win:Boolean = false
    public var scaleFactor = 1.0f
    var rect:Rect = Rect(box.left.toInt(), box.top.toInt(), box.right.toInt(), box.bottom.toInt())
    var bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.dvd)



    val displayMetrics = DisplayMetrics()
    private val boxPaint = Paint().apply{
        color = Color.RED
        alpha = 45
    }
    private val greenBrush = Paint().apply{
        color = Color.GREEN
        alpha = 255
    }
    private val textBrush = Paint().apply{
        color = Color.BLACK
        textSize = 100F
    }
    private val boxBrush = Paint().apply{
        background = resources.getDrawable(R.drawable.ic_launcher_background)
        //color = Color.YELLOW
    }
    private val backgroundPaint = Paint().apply {
        color = Color.BLACK
    }

    public fun setAcceleration(acceleration: PointF){
        if(win){
            boxAcceleration.x = 0f
            boxAcceleration.y = 0f

        }
        else{
            boxAcceleration.x = acceleration.x/10000
            boxAcceleration.y = acceleration.y/10000
        }

    }

    public fun setLight(lightValue: Float){
        light = lightValue.toInt()
        //Log.d(LOG_TAG, "${light}")
    }



    override fun onDraw(canvas: Canvas) {

        canvas.scale(scaleFactor, scaleFactor)
        canvas.drawPaint(backgroundPaint)
//        boxen.forEach { box ->
//            canvas.drawRect(box.left, box.top, box.right, box.bottom, boxPaint)
//        }
        //canvas.drawCircle(ball.center.x, ball.center.y, ball.radius, greenBrush)


        rect.left = box.left.toInt()
        rect.right = box.right.toInt()
        rect.top = box.top.toInt()
        rect.bottom = box.bottom.toInt()
        if(list_of_dvds_index >= 4){
            list_of_dvds_index = 0
        }

        bitmap = BitmapFactory.decodeResource(context.resources, list_of_dvds[list_of_dvds_index])


        if(win){
            canvas.drawText("You did it! ", 500f, 500f, textBrush)
            canvas.drawText("Now Sit back and enjoy...", 180f, 600f, textBrush)

        }
        else{
            rect.left = box.left.toInt()
            rect.right = box.right.toInt()
            rect.top = box.top.toInt()
            rect.bottom = box.bottom.toInt()
            if(list_of_dvds_index >= 4){
                list_of_dvds_index = 0
            }
        }

            canvas.drawRect(box.left, box.top, box.right, box.bottom, boxBrush)
            canvas.drawBitmap(bitmap, null, rect, boxBrush)




        updateBoxPosition()
        checkForDark()
    }

    private fun checkForDark(){
        if(light < 100){
            backgroundPaint.color = Color.DKGRAY
            //Log.d(LOG_TAG, "dark mode")
        }
        else{
            backgroundPaint.color = Color.BLUE
            //Log.d(LOG_TAG, "light mode")
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




        if(box.start.x.toInt() == 0 && box.start.y.toInt() == 0){
            win = true
            boxVelocity.x = .6f
            boxVelocity.y = .5f
        }
        if(box.start.y.toInt() + 200 == height && box.end.x.toInt() - 200 == 0){
            win = true
            boxVelocity.x = .6f
            boxVelocity.y = .5f
        }
        if(box.end.x.toInt() == width && box.end.y.toInt() == height){
            win = true
            boxVelocity.x = .6f
            boxVelocity.y = .5f
        }
        if(box.start.x.toInt() + 200 == width && box.end.y.toInt() - 200 == 0){
            win = true
            boxVelocity.x = .6f
            boxVelocity.y = .5f
        }



        if(box.start.x <= 0f){
            box.start.x = 0f
            box.end.x = 200f
            boxVelocity.x *= BOUNCE_FACTOR
            list_of_dvds_index++

        }
        if(box.end.x >= width/scaleFactor) {
            box.end.x = (width/scaleFactor)
            box.start.x = (width/scaleFactor) - 200
            boxVelocity.x *= BOUNCE_FACTOR
            list_of_dvds_index++

        }
        if(box.start.y <= 0f) {
            box.start.y = 0f
            box.end.y = 200f
            boxVelocity.y *= BOUNCE_FACTOR
            list_of_dvds_index++

        }
        if(box.end.y >= height/scaleFactor) {
            box.end.y = (height/scaleFactor)
            box.start.y = (height/scaleFactor) - 200
            boxVelocity.y *= BOUNCE_FACTOR
            list_of_dvds_index++
        }



        invalidate()
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


    }



}