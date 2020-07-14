package com.vast.gradle.plugin


import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec

import java.nio.file.Paths

class SpringEnv implements Plugin<Project> {
    static class Extension {
        File baseDir
        Class<? extends JavaExec> runTask = JavaExec.class
    }

    @Override
    void apply(Project project) {
        def extension = project.extensions.create('springenv', Extension)

        if (!extension.baseDir)
            extension.baseDir = project.file(Paths.get('src', 'main', 'resources'))

        if (!extension.baseDir.exists()) {
            System.err.println(extension.baseDir.toString() + ' is not exist')
            return
        }

        def configurations = new FileNameFinder().getFileNames(extension.baseDir.toString(), 'application-*')
        configurations.each {
            def file = new File(it).name
            def ext = file.take(file.lastIndexOf('.'))
            def name = (ext - 'application-').split('-').collect { it.capitalize() }.join('')

            project.tasks.create("bootRun${name}", JavaExec.class) {
                group = 'Application'
                dependsOn = ['jar']

                doFirst() {
                    main = project.getProperty('mainClassName')
                    classpath = project.sourceSets.main.runtimeClasspath
                    systemProperty 'spring.profiles.active', name.toLowerCase()
                }
            }
        }
    }
}
