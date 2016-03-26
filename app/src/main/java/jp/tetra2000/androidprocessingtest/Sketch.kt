package jp.tetra2000.androidprocessingtest

import processing.core.PApplet

class Sketch : PApplet() {
    override fun settings() {

    }

    override fun setup() {

    }

    override fun draw() {
        if (mousePressed) {
            ellipse(mouseX.toFloat(), mouseY.toFloat(), 50f, 50f)
        }
    }
}
