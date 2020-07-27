package org.moeftc

import com.android.tools.r8.D8
import com.android.tools.r8.D8Command
import com.android.tools.r8.OutputMode
import org.gradle.api.Task
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.file.RegularFile
import java.io.File
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path

fun Task.directoryProperty(path: String): DirectoryProperty {
    val dir = project.layout.buildDirectory.dir(path)
    @Suppress("UnstableApiUsage")
    return project.objects.directoryProperty().apply { set(dir) }
}

fun OutputStream.write(file: RegularFile) {
    Files.copy(file.asPath, this)
}

fun runD8(outputFile: DirectoryProperty, files: Iterable<File>, intermediate: Boolean, minApiLevel: Int) {
    val outputMode = if (intermediate) OutputMode.DexFilePerClassFile else OutputMode.DexIndexed
    val command = D8Command.builder()
            .setMinApiLevel(minApiLevel)
            .setDisableDesugaring(true)
            .addProgramFiles(files.map { it.toPath() })
            .setOutput(outputFile.get().asPath, outputMode)
            .setIntermediate(intermediate)
            .build()
    D8.run(command)
}

val FileSystemLocation.asPath: Path
    get() = asFile.toPath()