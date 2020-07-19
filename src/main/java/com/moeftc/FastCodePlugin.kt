package com.moeftc

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import kotlin.reflect.KClass

class FastCodePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.apply {
            register(FireCode::class) {
                it.dependsOn("CreateDexFile")
            }
            register(CreateDexFile::class) {
                it.dependsOn("compileDebugKotlin");
            }

        }
    }

}

private fun <T : Task> TaskContainer.register(kClass: KClass<T>, closure: (T) -> Unit) {
    this.register(kClass.simpleName!!, kClass.java, closure)
}
