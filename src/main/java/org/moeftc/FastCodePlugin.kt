package org.moeftc

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.register
import java.io.File
import java.util.function.Consumer
import kotlin.reflect.KClass

class FastCodePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        addService(project)
        project.tasks.register(FastCode::class) { fastCode: FastCode -> fastCode.dependsOn("createDexFile") }
        val data = project.extensions.create("fastcode", FastCodeExtension::class.java)
        project.tasks.register<CreateDexFile>("createDexFile") {
            addDependencies(this, data.language)
            fillCompilePaths(project, this)
        }
    }

    private fun addService(project: Project) {
        project.gradle.addListener(ServiceDependencyResolver(project))
    }

    private fun addDependencies(task: Task, language: String?) {
        when (language) {
            "kotlin" -> task.dependsOn("compileDebugKotlin")
            "java" -> task.dependsOn("compileDebugJavaWithJavac")
            "both" -> {
                task.dependsOn("compileDebugKotlin")
                task.dependsOn("compileDebugJavaWithJavac")
            }
            else -> throw GradleException("fastcode.language cannot be '$language'\nValid options are: 'java', 'kotlin', 'both'")
        }
    }

    private fun fillCompilePaths(project: Project, task: CreateDexFile) {
//        Set<File> compilePaths = new HashSet<>();
        task.dependsOn.forEach { dep ->
            if (dep !is String) return@forEach
            val files = project.tasks[dep].outputs.files
            task.inputDir.from(files)
        }
        //        return project.getTasks().getAt("compileDebugJavaWithJavac").getOutputs().getFiles().getFiles();
//        FastCodePlugin.compilePaths = compilePaths;
    }

    private fun <T : Task> TaskContainer.register(kClass: KClass<T>, action: (T) -> Unit) {
        this.register(kClass.simpleName!!.decapitalize(), kClass.java, action)
    }
}