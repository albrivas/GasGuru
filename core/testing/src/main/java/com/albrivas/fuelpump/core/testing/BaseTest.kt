package com.albrivas.fuelpump.core.testing

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import de.mannodermaus.junit5.compose.createComposeExtension

open class BaseTest {

    @OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)
    @JvmField
    val extension = createComposeExtension()

    val testContext: Context = ApplicationProvider.getApplicationContext()
}
