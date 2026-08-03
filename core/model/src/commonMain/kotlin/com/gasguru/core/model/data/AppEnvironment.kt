package com.gasguru.core.model.data

// Bound in Koin by the same modules that already pick the RemoteDataSource
// (mockModule / prod data source modules), so commonMain code can read the
// active environment via get<AppEnvironment>() without any new build wiring.
enum class AppEnvironment {
    Prod,
    Mock,
    ;

    val isMock: Boolean
        get() = this == Mock
}
