package org.moeftc

import io.github.classgraph.ClassGraph
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.OutputStream
import java.net.Socket
import java.nio.file.Files

const val packageToScan = "org.firstinspires.ftc.teamcode"
const val prefix = "com.qualcomm.robotcore.eventloop.opmode."
const val disabledAnnotation = prefix + "Disabled"
val annotations = arrayOf(prefix + "Autonomous", prefix + "TeleOp")

open class FireCode : DefaultTask() {
    @get:Input
    @set:Option(option = "ip", description = "Ip address of phone/control hub")
    var ip: String? = null

    @TaskAction
    fun fire() {
        val signature = byteArrayOf(3, 6, 5)
        val dexFile = project.buildDir.toPath().resolve("dex/classes.dex")
        val opModeBytes: ByteArray = getOpModes()
        Socket(ip, 42069).getOutputStream().use { stream ->
            stream.write(signature)
            stream.writeInt(opModeBytes.size)
            stream.write(opModeBytes)
            Files.copy(dexFile, stream)
        }
    }

    private fun getOpModes(): ByteArray {
        val classPackageRoot = this.project.buildDir.resolve("tmp/kotlin-classes/debug")
        val scanner = ClassGraph()
                .overrideClasspath(classPackageRoot)
                .whitelistPackages(packageToScan)
                .enableAnnotationInfo()
        val opModes = StringBuilder()
        scanner.scan().use { scanResult ->
            for (annotation in annotations) {
                val annotatedClasses = scanResult
                        .getClassesWithAnnotation(annotation)
                        .filterNot { it.hasAnnotation(disabledAnnotation) }
                for (clazz in annotatedClasses) {
                    val annotationInfo = clazz.getAnnotationInfo(annotation)
                    var opModeName = annotationInfo.parameterValues.getValue("name") as String?
                    if (opModeName.isNullOrBlank()) opModeName = clazz.simpleName
                    opModes.append(opModeName!!)
                    opModes.append('/')
                    opModes.append(clazz.name)
                    opModes.append('/')
                }
                opModes.append("\n")
            }
        }
        return opModes.toString().toByteArray()
    }

    private fun OutputStream.writeInt(num: Int) {

        write(num ushr 24)
        write(num ushr 16)
        write(num ushr 8)
        write(num)
        print(num)
    }
}
