package org.moeftc

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import kotlin.reflect.KClass

class FastCodePlugin : Plugin<Project> {
//    private lateinit var project: Project

    override fun apply(project: Project) {
//        if (project.name == "FtcRobotController") {
//            this.project = project
//        project.gradle.addListener(this)
        project.tasks.apply {
            register(FireCode::class) {
                it.dependsOn("CreateDexFile")
            }
            register(CreateDexFile::class) {
                it.dependsOn("compileDebugKotlin");
            }

        }
    }
    private fun <T : Task> TaskContainer.register(kClass: KClass<T>, closure: (T) -> Unit) {
        this.register(kClass.simpleName!!, kClass.java, closure)
    }
}



////    override fun apply(project:Project) {
//
//    //    }
//    override fun beforeResolve(dependencies: ResolvableDependencies) {
//        val implDeps = project.configurations.getByName("implementation").dependencies
////        println(implDeps.forEach { println(it.toString()) })
////        println "adding maven lib:" // this one works fine:
//
////        val serviceDep = project.dependencies.create("com.github.greenpizza1203:fast-code-service:master-SNAPSHOT")
////        implDeps.add(serviceDep)
////        println "adding local project(\":mySecondLib\")"  // this one doesn't:
////        project.dependencies.add 'compile', project.dependencies.project(':mySecondLib')
//        project.gradle.removeListener(this)
//    }
//
//    override fun afterResolve(dependencies: ResolvableDependencies) {
//    }




