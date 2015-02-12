package ru.d10xa.downloadxml

import org.gradle.api.Project
import org.gradle.util.Configurable
import org.gradle.util.ConfigureUtil

class DownloadXmlExtension implements Configurable<DownloadXmlExtension>{

    private Project project

    public DownloadXmlExtension(Project project){
        this.project = project
    }

    @Override
    DownloadXmlExtension configure(Closure closure) {
        def action = ConfigureUtil.configure(closure, new DownloadXmlAction())
        try {
            action.execute(project);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return this;
    }
}