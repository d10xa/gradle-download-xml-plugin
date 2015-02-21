package ru.d10xa.downloadxml

import groovy.transform.PackageScope
import groovy.util.logging.Log

import java.util.concurrent.ConcurrentHashMap

@Log
@PackageScope
class XmlLoader {

    private Map<Xml,Boolean> documentsMap = new ConcurrentHashMap<>();

    Xml rootDocument
    BasicAuth basicAuth

    XmlLoader(URL url,BasicAuth basicAuth) {
        rootDocument = new Xml(url,basicAuth)
        this.basicAuth = basicAuth
        extractSchemaLocations()
    }

    private boolean hasNotVisited(){
        boolean result = false;
        documentsMap.each {
            if(it.value.equals(false)){
                result = true;
            }
        }
        return result;
    }

    void putNewXml(Xml document){
        documentsMap.put(document,false)
    }

    void extractSchemaLocations(){
        addIfNotExists(rootDocument.getSchemaLocations())
        while (hasNotVisited()){
            def iterator = documentsMap.iterator()

            while (iterator.hasNext()) {
                Map.Entry<Xml,Boolean> e = iterator.next()
                e.value = true
                addIfNotExists e.key.getSchemaLocations()
            }
        }
    }

    private void addIfNotExists(List<String> locationsList){
        for (String str : locationsList) {
            URL url = str.toURL()
            Xml doc = new Xml(url,basicAuth)

            if (!documentsMap.containsKey(doc)){
                putNewXml(doc);
            }
        }
    }

    public Set<Xml> getInnerXml(){
        return documentsMap.keySet();
    }


}

