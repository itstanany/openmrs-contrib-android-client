package com.itstanany.openmrs_googleOHS

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.datacapture.mapping.ResourceMapper
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.QuestionnaireResponseValidator
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import java.util.UUID

class AddPatientViewModel(application: Application, private val state: SavedStateHandle) :
  AndroidViewModel(application) {

  private var questionnaireJson: String? = null
  val questionnaire: String
    get() = getQuestionnaireJson()

  val isPatientSaved = MutableLiveData<Boolean>()

  private fun getQuestionnaireJson(): String {
    questionnaireJson?.let {
      return it
    }
    questionnaireJson = readFileFromAssets(state[AddPatientFragment.QUESTIONNAIRE_FILE_PATH_KEY]!!)
    return questionnaireJson!!
  }

  private val questionnaireResource: Questionnaire
    get() =
      FhirContext.forCached(FhirVersionEnum.R4).newJsonParser().parseResource(questionnaire)
  as Questionnaire

  private val fhirEngine: FhirEngine = FhirApplication.fhirEngine(application.applicationContext)

 private fun readFileFromAssets(filename: String): String {
    return getApplication<Application>().assets.open(filename).bufferedReader().use {
      it.readText()
    }
  }

  fun savePatient(questionnaireResponse: QuestionnaireResponse) {
    viewModelScope.launch {
      if(isQuestionnaireInValid(questionnaireResponse)) {
        isPatientSaved.value = false
        return@launch
      }
      val entry = ResourceMapper.extract(questionnaireResource, questionnaireResponse).entryFirstRep
      if (entry.resource !is Patient) {
        return@launch
      }
      val patient = entry.resource as Patient
      patient.id = generateUUid()
      fhirEngine.create(patient)
      isPatientSaved.value = true
    }
  }

  private fun isQuestionnaireInValid(questionnaireResponse: QuestionnaireResponse): Boolean {
    return QuestionnaireResponseValidator.validateQuestionnaireResponse(
      questionnaireResource,
      questionnaireResponse,
      getApplication()
    )
      .values
      .flatten()
      .any { it is Invalid }
  }

  private fun generateUUid(): String {
    return UUID.randomUUID().toString()
  }
}