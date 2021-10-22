package com.example.love.other.animation

import android.view.View

fun View.animateImageView(){
    this.animate().apply {
        duration = 1000
        alpha(.5f)
        scaleXBy(.5f)
        scaleYBy(.5f)
        rotationYBy(360f)
        translationYBy(200f)
    }.withEndAction {
        this.animate().apply {
            duration = 1000
            alpha(1f)
            scaleXBy(-.5f)
            scaleYBy(-.5f)
            rotationXBy(360f)
            translationYBy(-200f)
        }
    }.start()
}