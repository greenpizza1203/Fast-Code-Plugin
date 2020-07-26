package org.moeftc

import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyResolutionListener
import org.gradle.api.artifacts.ResolvableDependencies
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.kotlin.dsl.maven

class ServiceDependencyResolver(var project: Project) : DependencyResolutionListener {
    override fun beforeResolve(resolvableDependencies: ResolvableDependencies) {
        project.repositories.mavenLocal()
        project.repositories.maven("https://jitpack.io")
        project.dependencies.add("implementation", Constants.serviceDependency)
        project.gradle.removeListener(this)
    }

    override fun afterResolve(resolvableDependencies: ResolvableDependencies) {}

}