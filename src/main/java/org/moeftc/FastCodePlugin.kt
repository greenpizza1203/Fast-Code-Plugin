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
    //    final static String javaPath = "intermediates/javac/debug/compileDebugJavaWithJavac/classes";
    //    final static String kotlinPath = "tmp/kotlin-classes/debug";
    override fun apply(project: Project) {
        addService(project)
        project.tasks.register(FastCode::class) { fastCode: FastCode -> fastCode.dependsOn("createDexFile") }
        val data = project.extensions.create("fastcode", FastCodeExtension::class.java)
//        project.tasks.register(CreateDexFile::class) { createDexFile: CreateDexFile ->
//            addDependencies(project, createDexFile, data.language)
//            createDexFile.
//            createDexFile.dexFinal.set(project.buildDir.toPath().resolve("dex/final").toFile())
//
////            println(project.tasks.getAt("compileDebugKotlin").outputs.files.files)
//            fillCompilePaths(project, createDexFile)
//        }
        project.tasks.register<CreateDexFile>("createDexFile") {
            addDependencies(this, data.language)
//            this..set(project.layout.buildDirectory.dir("dex/dexIntermediate"))
//            this.dexIntermediate.set(project.layout.buildDirectory.dir("dex/dexIntermediate"))
//            this.dexFinal.set(project.layout.buildDirectory.dir("dex/dexFinal"))
//            createDexFile.
//            createDexFile.dexFinal.set(project.buildDir.toPath().resolve("dex/final").toFile())
//
////            println(project.tasks.getAt("compileDebugKotlin").outputs.files.files)
            fillCompilePaths(project, this)
        }
    }

    private fun addService(project: Project) {
        project.gradle.addListener(ServiceDependencyResolver(project))
    }

    private fun addDependencies(task: Task, language: String?) {
//        Set<File> compilePaths;
        when (language) {
            "kotlin" -> {
                task.dependsOn("compileDebugKotlin")

                //            task.getInputs().fi
//            compilePaths = getFiles(project, "compileDebugKotlin");
            }
            "java" -> {
                task.dependsOn("compileDebugJavaWithJavac")

//            compilePaths = getFiles(project, "compileDebugJavaWithJavac");
            }
            "both" -> {
                task.dependsOn("compileDebugKotlin")
                task.dependsOn("compileDebugJavaWithJavac")

//            compilePaths = getFiles(project, "compileDebugJavaWithJavac");
//            compilePaths.addAll(getFiles(project, "compileDebugKotlin"));
            }
            else -> {
                throw GradleException("fastcode.language cannot be '$language'\nValid options are: 'java', 'kotlin', 'both'")
            }
        }
        //        return compilePaths;
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