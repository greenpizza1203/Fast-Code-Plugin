package com.moeftc

import com.android.tools.r8.CompilationFailedException
import com.android.tools.r8.D8
import com.android.tools.r8.D8Command
import com.android.tools.r8.OutputMode
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

open class CreateDexFile : DefaultTask() {
    @TaskAction
    @Throws(CompilationFailedException::class, IOException::class)
    fun run() {
        val inputDir = project.buildDir.toPath().resolve("tmp/kotlin-classes/debug")
        val outputDir = project.buildDir.toPath().resolve("dex")
        val builder = D8Command.builder()
                .setMinApiLevel(23)
                .setDisableDesugaring(true)
                .setOutput(outputDir, OutputMode.DexIndexed)
        val files = Files.walk(inputDir)
                .filter { file: Path -> file.endsWith(".class") }
                .collect(Collectors.toList())
        builder.addProgramFiles(files)
        val command = builder.build()
        D8.run(command)
    }
}