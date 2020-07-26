package org.moeftc

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.*
import org.gradle.work.ChangeType
import org.gradle.work.ChangeType.*
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

@Suppress("UnstableApiUsage")
abstract class CreateDexFile : DefaultTask() {
    @get:Incremental
    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:InputFiles
    val inputDir: ConfigurableFileCollection = project.objects.fileCollection()


    @OutputDirectory
    val dexIntermediate = directoryProperty("dex/intermediate")

    @OutputDirectory
    val dexFinal = directoryProperty("dex/final")

    @TaskAction
    fun run(inputChanges: InputChanges) {
        val intermediateFiles = arrayListOf<File>()
        inputChanges.getFileChanges(inputDir).forEach {
            if (it.file.extension != "class") return@forEach
            if (it.changeType == ADDED || it.changeType == MODIFIED) {
                val file = it.file
                intermediateFiles.add(file)
            } else {
                val asFile = dexIntermediate.file(it.normalizedPath).get().asFile.path.substringBeforeLast(".") + ".dex"
                File(asFile).delete()
            }
        }
        val minApiLevel = extension.minApiLevel
        runD8(dexIntermediate, intermediateFiles, true, minApiLevel)
        val finalFiles = project.fileTree(dexIntermediate)
        runD8(dexFinal, finalFiles, false, minApiLevel)
    }


    private val extension: FastCodeExtension = project.extensions.getByType(FastCodeExtension::class.java)

}