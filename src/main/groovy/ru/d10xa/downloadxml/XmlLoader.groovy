package ru.d10xa.downloadxml

import groovy.transform.PackageScope
import groovy.util.logging.Log

import java.util.concurrent.ConcurrentHashMap

@Log
@PackageScope
class XmlLoader {

    private Map<Xml,Boolean> documentsMap = new ConcurrentHashMap<>();

    Xml rootDocument

    XmlLoader(Xml rootDocument) {
        this.rootDocument = rootDocument
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
        addIfNotExists rootDocument.getSchemaLocations()
        while (hasNotVisited()){
            def iterator = documentsMap.iterator()

            while (iterator.hasNext()) {
                Map.Entry<Xml,Boolean> e = iterator.next()
                e.value = true
                addIfNotExists e.key.getSchemaLocations()
            }
        }
    }

    private void addIfNotExists(List<Location> locationsList){
        for (Location location : locationsList) {
            def processed = location?.getProcessed()
            URL url = processed?.toURL()
            Xml doc = new Xml(rootDocument,url)
            if(location.isMalformed()){
                doc.rawUrl = location.raw
            }
            if (!documentsMap.containsKey(doc)){
                putNewXml(doc);
            }
        }
    }

    public Set<Xml> getInnerXml(){
        return documentsMap.keySet();
    }


}

