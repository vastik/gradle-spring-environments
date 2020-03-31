package com.vast.gradle.plugin


import org.gradle.api.Plugin
import org.gradle.api.Project
import org.springframework.boot.gradle.tasks.run.BootRun

class SpringEnvironmentPlugin implements Plugin<Project> {

    static class Extension {
        String basedir = './src/main/resources/'
    }

    @Override
    void apply(Project project) {
        def extension = project.extensions.create('springenv', Extension)
        def configurations = new FileNameFinder().getFileNames(extension.basedir, 'application-*')
        configurations.each {
            def file = new File(it).name
            def ext = file.take(file.lastIndexOf('.'))
            def name = (ext - 'application-').split('-').collect { it.capitalize() }.join('')

            project.tasks.create("bootRun${name}", BootRun.class) {
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
