package com.albrivas.fuelpump.core.testing

import android.content.Context
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.test.core.app.ApplicationProvider
import de.mannodermaus.junit5.compose.createComposeExtension
import org.junit.jupiter.api.extension.RegisterExtension

open class BaseTest {

    @OptIn(ExperimentalTestApi::class)
    @Suppress("JUnitMalformedDeclaration")
    @RegisterExtension
    @JvmField
    val extension = createComposeExtension()

    val testContext: Context = ApplicationProvider.getApplicationContext()
}
