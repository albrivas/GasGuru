package com.gasguru.build_logic.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureDetekt(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        apply(plugin = "io.gitlab.arturbosch.detekt")

        dependencies {
            add("detektPlugins", getLibrary("detekt.formatting"))
        }
    }
}