package com.itstanany.openmrs_googleOHS

import android.app.Application
import com.google.android.fhir.DatabaseErrorStrategy.RECREATE_AT_OPEN
import com.google.android.fhir.FhirEngineConfiguration
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.ServerConfiguration
import com.google.android.fhir.datacapture.DataCaptureConfig
import com.google.android.fhir.sync.remote.HttpLogger
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import com.google.android.fhir.sync.HttpAuthenticator

class FhirApplication : Application(), DataCaptureConfig.Provider {

  private val baseUrl = "https://us-central1-voithy-6c0e0.cloudfunctions.net/fhir-datastore-proxy/"
  private val dataStore by lazy { DemoDataStore(this) }

  override fun onCreate() {
    super.onCreate()
    FhirEngineProvider.init(
      FhirEngineConfiguration(
        enableEncryptionIfSupported = true,
        RECREATE_AT_OPEN,
        ServerConfiguration(
          baseUrl = baseUrl,
          httpLogger = HttpLogger(
            HttpLogger.Configuration(HttpLogger.Level.BODY)
          ) { Timber.tag("App-HttpLog").d(it) },
        )
      )
    )
  }

  override fun getDataCaptureConfig(): DataCaptureConfig {
    TODO("Not yet implemented")
  }
}