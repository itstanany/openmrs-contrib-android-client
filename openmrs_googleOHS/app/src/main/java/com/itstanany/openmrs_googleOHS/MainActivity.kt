package com.itstanany.openmrs_googleOHS

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import androidx.fragment.app.commitNow
import com.google.android.fhir.datacapture.QuestionnaireFragment
import com.itstanany.openmrs_googleOHS.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
  private val TAG = "MainActivity"
  private lateinit var binding: ActivityMainBinding
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    }


}