package com.aminivan.mapslearn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.core.view.GravityCompat
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val image_menu = findViewById(R.id.imageMenu) as ImageView
        val navview = findViewById(R.id.navigationView) as NavigationView
        val navcon = Navigation.findNavController(this,R.id.navHostFragment)

        navview.setItemIconTintList(null)
        image_menu.setOnClickListener() {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        NavigationUI.setupWithNavController(navigationView, navcon)

    }
}
