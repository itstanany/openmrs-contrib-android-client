package com.itstanany.openmrs_googleOHS

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class AddPatientViewModel(
  application: Application,
  private val state: SavedStateHandle,
) : AndroidViewModel(application) {

  private var questionnaireJson: String? = null
  val questionnaire: String
    get() = getQuestionnaireJson()

  private fun getQuestionnaireJson(): String {
    questionnaireJson?.let {
      return it
    }
    questionnaireJson = readFileFromAssets(state[AddPatientFragment.QUESTIONNAIRE_FILE_PATH_KEY]!!)
    return questionnaireJson!!
  }

  private fun readFileFromAssets(fileName: String): String {
    return getApplication<Application>().assets.open(fileName).bufferedReader().use {
      it.readText()
    }
  }
}