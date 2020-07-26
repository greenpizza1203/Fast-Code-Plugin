package org.moeftc

import io.github.classgraph.ClassGraph
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.IOException
import java.io.OutputStream
import java.net.InetAddress
import java.net.Socket
import java.net.UnknownHostException
import java.nio.file.Files

private const val packageToScan = "org.firstinspires.ftc.teamcode"
private const val prefix = "com.qualcomm.robotcore.eventloop.opmode."
private const val disabledAnnotation = prefix + "Disabled"
private val annotations = arrayOf(prefix + "Autonomous", prefix + "TeleOp")
val signature = byteArrayOf(3, 6, 5)

abstract class FastCode : DefaultTask() {

    @Option(option = "ip", description = "Ip address of phone/control hub")
    fun setIp(ip: String) {
        this.ip = ip
    }

    private var ip: String? = null


    @TaskAction
    fun fire() {
        if (ip == null) {
            errorOut("""
            Must pass in a IP address
            (Example) gradlew fastcode --ip=192.168.49.1
            """)
        }
        val dexFile = project.layout.buildDirectory.file("dex/final/classes.dex")
        val opModeBytes = scanOpModes()

//        System.err.println("Attempting to resolve ip")
        val name = InetAddress.getByName(ip)

//        System.err.println("IP resolve, attempting socket connection")
        try {
            Socket(name, 42069).use { socket ->
//                System.err.println("socket connected")
                val stream = socket.getOutputStream()
//                System.err.println("got output stream")
                stream.write(signature)
//                System.err.println("wrote signature")
                stream.writeInt(opModeBytes.size)
//                System.err.println("wrote size")
                stream.write(opModeBytes)
//                System.err.println("wrote string")
                stream.write(dexFile.get())
                //System.err.println("wrote file")
            }
        } catch (e: IOException) {
            errorOut("""
            Unable to connect to device on IP address: $ip
            Make sure you can access the robot server by visiting http://$ip:8080 on your browser
            ${e.message}
            """)
        }
    }

    private fun errorOut(message: String) {
        throw GradleException(message.trimIndent())
    }

    //        List<Path> fastcodepaths = (List<Path>) this.getProject().getExtensions().getExtraProperties().get("fastcodepaths");
//        System.out.println(fastcodepaths);
    private fun scanOpModes(): ByteArray {

        val scanner = ClassGraph()
                .overrideClasspath(project.tasks.getAt("createDexFile").inputs.files.files)
                .acceptPackages(packageToScan)
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
                    opModes.append(opModeName)
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
    }
}