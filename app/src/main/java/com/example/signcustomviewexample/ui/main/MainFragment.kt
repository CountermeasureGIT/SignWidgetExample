package com.example.signcustomviewexample.ui.main

import android.graphics.Bitmap
import android.os.Bundle
import androidx.core.graphics.scale
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.signcustomviewexample.R
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment(R.layout.main_fragment) {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by viewModels()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        clearButton.setOnClickListener {
            drawerView.clearDrawer()
        }
        saveButton.setOnClickListener {
            signImageView.setImageBitmap(
                drawerView.getBitmap(Bitmap.Config.ALPHA_8).scale(300, 300)
            )
        }
    }

}
