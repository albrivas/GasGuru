package com.gasguru.core.model.data

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class AppEnvironmentTest {

    @Test
    fun `GIVEN Mock environment WHEN isMock THEN returns true`() {
        AppEnvironment.Mock.isMock shouldBe true
    }

    @Test
    fun `GIVEN Prod environment WHEN isMock THEN returns false`() {
        AppEnvironment.Prod.isMock shouldBe false
    }
}
