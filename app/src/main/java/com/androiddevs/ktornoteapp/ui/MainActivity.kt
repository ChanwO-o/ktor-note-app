package com.androiddevs.ktornoteapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.androiddevs.ktornoteapp.R
import dagger.hilt.android.AndroidEntryPoint

// need this annotation because ViewModels are injected into the fragments
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}