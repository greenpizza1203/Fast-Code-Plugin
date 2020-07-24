package org.moeftc;

import com.android.tools.r8.CompilationFailedException;
import com.android.tools.r8.D8;
import com.android.tools.r8.D8Command;
import com.android.tools.r8.OutputMode;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
public class CreateDexFile extends DefaultTask {
    @TaskAction
    public void run() throws IOException, CompilationFailedException {
//        Path inputDir = getProject().getBuildDir().toPath().resolve("tmp/kotlin-classes/debug");


        Path outputDir = getProject().getBuildDir().toPath().resolve("dex");
        //noinspection ResultOfMethodCallIgnored
        outputDir.toFile().mkdir();
        D8Command.Builder builder = D8Command.builder()
                .setMinApiLevel(23)
                .setDisableDesugaring(true)
                .setOutput(outputDir, OutputMode.DexIndexed);
        List<Path> fastcodepaths = (List<Path>) this.getProject().getExtensions().getExtraProperties().get("fastcodepaths");
        for (Path inputDir : fastcodepaths) {
            List<Path> files = Files.walk(inputDir)
                    .filter(path -> path.toString().endsWith(".class"))
                    .collect(Collectors.toList());
            builder.addProgramFiles(files);
        }
        D8Command command = builder.build();
        D8.run(command);
    }

}
