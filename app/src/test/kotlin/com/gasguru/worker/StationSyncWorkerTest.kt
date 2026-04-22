package com.gasguru.worker

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.updateAll
import androidx.work.ListenableWorker.Result
import androidx.work.WorkerParameters
import com.gasguru.core.analytics.AnalyticsHelper
import com.gasguru.core.analytics.NoOpAnalyticsHelper
import com.gasguru.core.data.repository.stations.FuelStationRepository
import com.gasguru.core.domain.fuelstation.GetFuelStationUseCase
import com.gasguru.core.testing.CoroutinesTestExtension
import com.gasguru.feature.widget.ui.FavoriteStationsWidget
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

@ExtendWith(CoroutinesTestExtension::class)
class StationSyncWorkerTest {

    private val context = mockk<Context>(relaxed = true)
    private val workerParameters = mockk<WorkerParameters>(relaxed = true)
    private val fuelStationRepository = mockk<FuelStationRepository>()
    private val getFuelStationUseCase = GetFuelStationUseCase(repository = fuelStationRepository)

    @BeforeEach
    fun setUp() {
        startKoin {
            modules(
                module {
                    single { getFuelStationUseCase }
                    single<AnalyticsHelper> { NoOpAnalyticsHelper() }
                }
            )
        }
        mockkStatic(GlanceAppWidget::updateAll)
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    @DisplayName(
        """
        GIVEN use case succeeds
        WHEN doWork is called
        THEN returns Result.success
    """
    )
    fun returnsSuccessWhenUseCaseSucceeds() = runTest {
        coEvery { fuelStationRepository.addAllStations() } just Runs
        coEvery { any<FavoriteStationsWidget>().updateAll(any()) } just Runs

        val worker = StationSyncWorker(context, workerParameters)
        val result = worker.doWork()

        assertEquals(Result.success(), result)
    }

    @Test
    @DisplayName(
        """
        GIVEN use case throws an exception
        WHEN doWork is called
        THEN returns Result.retry
    """
    )
    fun returnsRetryWhenUseCaseFails() = runTest {
        coEvery { fuelStationRepository.addAllStations() } throws RuntimeException("network error")

        val worker = StationSyncWorker(context, workerParameters)
        val result = worker.doWork()

        assertEquals(Result.retry(), result)
    }
}
