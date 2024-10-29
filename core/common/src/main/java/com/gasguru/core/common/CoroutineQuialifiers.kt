package com.gasguru.core.common

import javax.inject.Qualifier

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class MainScope

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class IoScope
