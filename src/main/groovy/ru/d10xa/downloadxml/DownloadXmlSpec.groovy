package ru.d10xa.downloadxml

public interface DownloadXmlSpec {
    void src(Object src)
    void dest(Object dest)
    void namespaceToFile(Map map)
    void username(String username)
    void password(String password)
}