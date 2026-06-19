package com.gasguru.core.common

// El target jvm() existe solo para ejecutar tests sin emulador; no es artefacto de producción.
// En un JVM de tests no hay app instalada, así que se devuelve un valor estable.
actual fun getAppVersion(): String = "0.0.0 (0)"
