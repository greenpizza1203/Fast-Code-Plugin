package org.moeftc;

import org.gradle.api.Project;
import org.gradle.api.artifacts.DependencyResolutionListener;
import org.gradle.api.artifacts.ResolvableDependencies;

public class ServiceDependencyResolver implements DependencyResolutionListener {
    Project project;

    public ServiceDependencyResolver(Project project) {
        this.project = project;
    }



    @Override
    public void beforeResolve(ResolvableDependencies resolvableDependencies) {
        project.getRepositories().maven(mavenArtifactRepository -> mavenArtifactRepository.setUrl("https://jitpack.io"));
        project.getDependencies().add("implementation", Constants.serviceDependency);
        project.getGradle().removeListener(this);
    }

    @Override
    public void afterResolve(ResolvableDependencies resolvableDependencies) {
    }
}
