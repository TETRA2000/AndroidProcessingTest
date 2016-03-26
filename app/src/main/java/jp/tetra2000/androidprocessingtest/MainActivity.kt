package jp.tetra2000.androidprocessingtest

import android.app.Activity
import android.os.Bundle

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fragmentManager = fragmentManager
        val fragment = Sketch()
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }

    private fun setupCamera() {
    }
}
