package org.moeftc;

import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.DependencyResolutionListener;
import org.gradle.api.artifacts.ResolvableDependencies;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FastCodePlugin implements Plugin<Project> {
    final static String javaPath = "intermediates/javac/debug/compileDebugJavaWithJavac/classes";
    final static String kotlinPath = "tmp/kotlin-classes/debug";


    public void apply(Project project) {
        addService(project);
        project.getTasks().register("fastCode", FastCode.class, fastCode -> fastCode.dependsOn("createDexFile"));
        FastCodeExtension data = project.getExtensions().create("fastcode", FastCodeExtension.class);

        project.getTasks().register("createDexFile", CreateDexFile.class, createDexFile -> {
            String language = data.language;
            Path buildDir = project.getBuildDir().toPath();
            List<Path> locations = new ArrayList<>();

            switch (language) {
                case "kotlin":
                    createDexFile.dependsOn("compileDebugKotlin");
                    locations.add(buildDir.resolve(kotlinPath));
                    break;
                case "java":
                    createDexFile.dependsOn("compileDebugJavaWithJavac");
                    locations.add(buildDir.resolve(javaPath));
                    break;
                case "both":
                    createDexFile.dependsOn("compileDebugJavaWithJavac");
                    createDexFile.dependsOn("compileDebugKotlin");
                    locations.add(buildDir.resolve(kotlinPath));
                    locations.add(buildDir.resolve(javaPath));
                    break;
                default:
                    throw new GradleException("fastcode.language cannot be '" + language + "'\nValid options are: 'java', 'kotlin', 'both'");
            }
            project.getExtensions().getExtraProperties().set("fastcodepaths", locations);
        });

    }

    private void addService(Project project) {
        //noinspection NullableProblems
        project.getGradle().addListener(new ServiceDependencyResolver(project));
    }


}






