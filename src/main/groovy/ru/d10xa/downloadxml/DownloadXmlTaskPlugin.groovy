package ru.d10xa.downloadxml

import org.gradle.api.Plugin
import org.gradle.api.Project

class DownloadXmlTaskPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.getExtensions().create("downloadXml",DownloadXmlExtension.class,project)
    }
}
