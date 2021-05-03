package com.csci448.trentdouglas.trentdouglas_a4

import android.view.ScaleGestureDetector

class ScaleListener(private val boxDrawingView: BoxDrawingView) : ScaleGestureDetector.SimpleOnScaleGestureListener() {
    override fun onScale(detector: ScaleGestureDetector?): Boolean {
        boxDrawingView.scaleFactor *= detector?.scaleFactor ?: 1.0f
        //boxDrawingView.scaleFactor.coerceIn(0.1f, 10.0f) that didn't work...
        if(boxDrawingView.scaleFactor > 10.0f){
            boxDrawingView.scaleFactor = 10.0f
        }
        if(boxDrawingView.scaleFactor < 0.1f){
            boxDrawingView.scaleFactor = 0.1f
        }
        boxDrawingView.invalidate()
        return true
    }
}