package org.moeftc

import com.android.tools.r8.D8
import com.android.tools.r8.D8Command
import com.android.tools.r8.OutputMode
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.nio.file.Files
import kotlin.streams.toList

open class CreateDexFile : DefaultTask() {
    @TaskAction
    fun run() {
        val inputDir = project.buildDir.toPath().resolve("tmp/kotlin-classes/debug")
        val outputDir = project.buildDir.toPath().resolve("dex")
        outputDir.toFile().mkdir()
        val builder = D8Command.builder()
                .setMinApiLevel(23)
                .setDisableDesugaring(true)
                .setOutput(outputDir, OutputMode.DexIndexed)
        val files = Files.walk(inputDir)
                .filter { it.toString().endsWith(".class") }
                .toList()
        builder.addProgramFiles(files)
        val command = builder.build()
        D8.run(command)
    }

}
