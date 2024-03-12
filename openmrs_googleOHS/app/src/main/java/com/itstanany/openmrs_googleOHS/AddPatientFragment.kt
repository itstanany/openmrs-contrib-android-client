package com.itstanany.openmrs_googleOHS

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.commit
import com.google.android.fhir.datacapture.QuestionnaireFragment
import org.hl7.fhir.r4.model.QuestionnaireResponse

class AddPatientFragment : Fragment(R.layout.add_patient_fragment) {

  private val viewModel: AddPatientViewModel by viewModels()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setHasOptionsMenu(true)
    updateArguments()
    if (savedInstanceState == null) {
      addQuestionnaireFragment()
    }
    observePatientSaveAction()
    childFragmentManager.setFragmentResultListener(
      QuestionnaireFragment.SUBMIT_REQUEST_KEY,
      viewLifecycleOwner,
      { _, _ ->
        onSubmitAction()
      }
    )
  }

  private fun observePatientSaveAction() {
    viewModel.isPatientSaved.observe(viewLifecycleOwner) {
      if(!it) {
        Toast.makeText(requireContext(), "Inputs Are Missing.", Toast.LENGTH_SHORT).show()
        return@observe
      }
      Toast.makeText(requireContext(), "Patient is saved.", Toast.LENGTH_SHORT).show()
      // todo: nav up or clear input fields
    }
  }

  private fun onSubmitAction() {
    val questionnaireFragment = childFragmentManager
      .findFragmentByTag(QUESTIONNAIRE_FRAGMENT_TAG) as QuestionnaireFragment
    savePatient(questionnaireFragment.getQuestionnaireResponse())
  }

  private fun savePatient(questionnaireResponse: QuestionnaireResponse) {
    viewModel.savePatient(questionnaireResponse)
  }

  private fun addQuestionnaireFragment() {
    childFragmentManager.commit {
      add(
        R.id.add_patient_container,
        QuestionnaireFragment.builder().setQuestionnaire(viewModel.questionnaire).build(),
        QUESTIONNAIRE_FRAGMENT_TAG)

    }
  }

  private fun updateArguments() {
    val args = arguments ?: Bundle()
    args.putString(QUESTIONNAIRE_FILE_PATH_KEY, "new-patient-registration-paginated.json")
    arguments = args
  }

  companion object {
    const val QUESTIONNAIRE_FILE_PATH_KEY = "questionnaire-file-path-key"
    const val QUESTIONNAIRE_FRAGMENT_TAG = "questionnaire-fragment-tag"
  }
//  override fun onCreate(savedInstanceState: Bundle?) {
//    super.onCreate(savedInstanceState)
//
//    // TODO: Use the ViewModel
//  }
//
//  override fun onCreateView(
//    inflater: LayoutInflater, container: ViewGroup?,
//    savedInstanceState: Bundle?
//  ): View {
//    return inflater.inflate(R.layout.fragment_add_patient, container, false)
//  }
}