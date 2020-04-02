package com.vast.gradle.plugin


import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec

class SpringEnv implements Plugin<Project> {
    static class Extension {
        File baseDir
        Class<? extends JavaExec> runTask = JavaExec.class
    }

    @Override
    void apply(Project project) {
        def extension = project.extensions.create('springenv', Extension)

        if (!extension.baseDir)
            extension.baseDir = project.file('/src/main/resources/')

        def configurations = new FileNameFinder().getFileNames(extension.baseDir.toString(), 'application-*')
        configurations.each {
            def file = new File(it).name
            def ext = file.take(file.lastIndexOf('.'))
            def name = (ext - 'application-').split('-').collect { it.capitalize() }.join('')

            project.tasks.create("bootRun${name}", JavaExec.class) {
                group = 'Application'
                dependsOn = ['build']

                doFirst() {
                    main = project.getProperty('mainClassName')
                    classpath = project.sourceSets.main.runtimeClasspath
                    systemProperty 'spring.profiles.active', name.toLowerCase()
                }
            }
        }

    }
}
