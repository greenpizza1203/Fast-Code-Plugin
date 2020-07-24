package org.moeftc;

import io.github.classgraph.*;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FastCode extends DefaultTask {
    final String packageToScan = "org.firstinspires.ftc.teamcode";
    final String prefix = "com.qualcomm.robotcore.eventloop.opmode.";
    final String disabledAnnotation = prefix + "Disabled";
    final String[] annotations = new String[]{prefix + "Autonomous", prefix + "TeleOp"};
    private String ip;

    @Option(option = "ip", description = "Ip address of phone/control hub")
    void setIp(final String ip) {
        this.ip = ip;
    }


    @TaskAction
    public void fire() {
        byte[] signature = new byte[]{3, 6, 5};
        Path dexFile = getProject().getBuildDir().toPath().resolve("dex/classes.dex");


        byte[] opModeBytes = getOpModes();
        if (ip == null) {
            throw new GradleException("Must pass in a IP address\n" +
                    "(Example) gradlew fastcode --ip=192.168.49.1");
        }

        System.err.println("Attempting to resolve ip");
        InetAddress name;
        try {
            name = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        }
        System.err.println("IP resolve, attempting socket connection");
        try (Socket socket = new Socket(name, 42069)) {
            System.err.println("socket connected");

            OutputStream stream = socket.getOutputStream();
            System.err.println("got output stream");
            stream.write(signature);
            System.err.println("wrote signature");

            writeInt(stream, opModeBytes.length);
            System.err.println("wrote size");

            stream.write(opModeBytes);
            System.err.println("wrote string");

            Files.copy(dexFile, stream);
            System.err.println("wrote file");

        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
            throw new GradleException("Unable to connect to device on IP address: " + ip + "\nMake sure you can access the robot server by visiting http://" + ip + ":8080 on your browser");
        }

    }

    private byte[] getOpModes() {
        List<Path> fastcodepaths = (List<Path>) this.getProject().getExtensions().getExtraProperties().get("fastcodepaths");
        System.out.println(fastcodepaths);
        ClassGraph scanner = new ClassGraph()
                .overrideClasspath(fastcodepaths)
                .acceptPackages(packageToScan)
                .enableAnnotationInfo();
        StringBuilder opModes = new StringBuilder();
        try (ScanResult scanResult = scanner.scan()) {
            for (String annotation : annotations) {
                ClassInfoList annotatedClasses = scanResult
                        .getClassesWithAnnotation(annotation)
                        .filter(
                                it -> !it.hasAnnotation(disabledAnnotation)
                        );
                for (ClassInfo clazz : annotatedClasses) {
                    AnnotationInfo annotationInfo = clazz.getAnnotationInfo(annotation);
                    String opModeName = (String) annotationInfo.getParameterValues().getValue("name");
                    if (opModeName == null || opModeName.isEmpty()) opModeName = clazz.getSimpleName();
                    opModes.append(opModeName);
                    opModes.append('/');
                    opModes.append(clazz.getName());
                    opModes.append('/');
                }
                opModes.append("\n");
            }
        }


        return opModes.toString().getBytes();
    }

    static class OpModeMetaAndClass {

    }


    private void writeInt(OutputStream outputStream, int num) throws IOException {
        outputStream.write(num >>> 24);
        outputStream.write(num >>> 16);
        outputStream.write(num >>> 8);
        outputStream.write(num);
    }
}
