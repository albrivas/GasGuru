package com.gasguru.core.testing

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.test.core.app.ApplicationProvider
import de.mannodermaus.junit5.compose.createComposeExtension
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.StringResource
import org.junit.jupiter.api.extension.RegisterExtension
import org.jetbrains.compose.resources.getString as cmpGetString

open class BaseTest {

    @OptIn(ExperimentalTestApi::class)
    @Suppress("JUnitMalformedDeclaration")
    @RegisterExtension
    @JvmField
    val extension = createComposeExtension()

    val testContext: Context = ApplicationProvider.getApplicationContext()

    fun getStringResource(@StringRes id: Int, vararg formatArgs: Any) =
        testContext.getString(id, *formatArgs)

    fun getCmpString(resource: StringResource): String = runBlocking { cmpGetString(resource) }

    fun getCmpString(resource: StringResource, vararg formatArgs: Any): String =
        runBlocking { cmpGetString(resource, *formatArgs) }
}
