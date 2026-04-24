package com.gasguru.core.testing

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

/**
 * Multiplatform coroutine test helper. Replaces [CoroutinesTestRuleExtension] (JUnit5)
 * in commonTest source sets.
 *
 * Usage with kotlin.test:
 * ```
 * class MyTest {
 *     private val coroutineTestHelper = CoroutineTestHelper()
 *
 *     @BeforeTest
 *     fun setup() { coroutineTestHelper.setup() }
 *
 *     @AfterTest
 *     fun tearDown() { coroutineTestHelper.tearDown() }
 * }
 * ```
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CoroutineTestHelper(
    val testDispatcher: TestDispatcher = StandardTestDispatcher(),
) {
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    fun tearDown() {
        Dispatchers.resetMain()
    }
}
