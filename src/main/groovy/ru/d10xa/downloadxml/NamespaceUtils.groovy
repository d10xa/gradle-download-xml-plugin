package ru.d10xa.downloadxml

class NamespaceUtils {
    static String namespaceToFileName(String namespace) {
        try {
            validateNamespace(namespace)
            return processValidNamespace(namespace)
        } catch (Exception e) {
            return processInvalidNamespace(namespace)
        }
    }

    static def validateNamespace(String namespace) {
        if(namespace==null||namespace.size()==0){
            throw new IllegalArgumentException("namespace must not be null or empty")
        }
        if(!namespace.startsWith('http://')){
            throw new IllegalArgumentException("namespace must begin with http://")
        }
    }

    private static String processValidNamespace(String namespace) {
        def t = namespace.tokenize('/')
        t = t[2..t.size() - 1]
        return t.join('-') + '.xml'
    }

    private static String processInvalidNamespace(String namespace) {
        namespace? namespace.replaceAll("\\W+", '-') + '.xml':'_.xml'
    }

}
