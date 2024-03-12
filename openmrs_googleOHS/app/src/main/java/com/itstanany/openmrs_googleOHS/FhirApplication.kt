package com.itstanany.openmrs_googleOHS

import android.app.Application
import android.content.Context
import com.google.android.fhir.BuildConfig
import com.google.android.fhir.DatabaseErrorStrategy
import com.google.android.fhir.DatabaseErrorStrategy.RECREATE_AT_OPEN
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineConfiguration
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.ServerConfiguration
import com.google.android.fhir.datacapture.DataCaptureConfig
import com.google.android.fhir.datacapture.XFhirQueryResolver
import com.google.android.fhir.search.search
import com.google.android.fhir.sync.Authenticator
import com.google.android.fhir.sync.remote.HttpLogger
import kotlinx.coroutines.runBlocking
import timber.log.Timber
//
//import android.app.Application
//import android.content.Context
//import com.google.android.fhir.DatabaseErrorStrategy.RECREATE_AT_OPEN
//import com.google.android.fhir.FhirEngineConfiguration
//import com.google.android.fhir.FhirEngineProvider
//import com.google.android.fhir.ServerConfiguration
//import com.google.android.fhir.datacapture.DataCaptureConfig
//import com.google.android.fhir.sync.remote.HttpLogger
//import kotlinx.coroutines.runBlocking
//import timber.log.Timber
//import com.google.android.fhir.sync.HttpAuthenticator


class FhirApplication : Application(), DataCaptureConfig.Provider {
  // Only initiate the FhirEngine when used for the first time, not when the app is created.
  private val fhirEngine: FhirEngine by lazy { constructFhirEngine() }

  private var dataCaptureConfig: DataCaptureConfig? = null

  private val dataStore by lazy { DemoDataStore(this) }

  private val baseUrl = "https://us-central1-openmrs-fhir-demo.cloudfunctions.net/fhir-datastore-proxy/"

  override fun onCreate() {
    super.onCreate()

    FhirEngineProvider.init(
      FhirEngineConfiguration(
        enableEncryptionIfSupported = true,
        DatabaseErrorStrategy.RECREATE_AT_OPEN,
        ServerConfiguration(
          baseUrl,
          httpLogger =
          HttpLogger(
            HttpLogger.Configuration(
              if (BuildConfig.DEBUG) HttpLogger.Level.BODY else HttpLogger.Level.BASIC
            )
          ) { Timber.tag("App-HttpLog").d(it) },
          authenticator =
          object : Authenticator {
            override fun getAccessToken(): String = runBlocking {
              return@runBlocking dataStore.getUserIdToken() ?: ""
            }
          }
        )
      )
    )

    dataCaptureConfig =
      DataCaptureConfig().apply {
        urlResolver = ReferenceUrlResolver(this@FhirApplication as Context)
        xFhirQueryResolver = XFhirQueryResolver { fhirEngine.search(it) }
      }
  }

  private fun constructFhirEngine(): FhirEngine {
    return FhirEngineProvider.getInstance(this)
  }

  companion object {
    fun fhirEngine(context: Context) = (context.applicationContext as FhirApplication).fhirEngine

    fun dataStore(context: Context) = (context.applicationContext as FhirApplication).dataStore
  }

  override fun getDataCaptureConfig(): DataCaptureConfig = dataCaptureConfig ?: DataCaptureConfig()
}
