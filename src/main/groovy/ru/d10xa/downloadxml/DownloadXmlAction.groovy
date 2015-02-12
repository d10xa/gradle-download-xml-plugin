package ru.d10xa.downloadxml
import org.gradle.api.Project

import java.lang.reflect.Array

class DownloadXmlAction implements DownloadXmlSpec {

    private List<URL> sources = new ArrayList<URL>(1)
    private File dest
    private Map<String,String> namespaceToFile = [:]
    
    public void execute(Project project){
        def xmls = getAllXmls()
        xmls.each {
            def ns = it.targetNamespace
            if(namespaceToFile.containsKey(ns)){
                save(it)
            } else {
                println "namespace not mapped ${it.targetNamespace}"
            }
        }
    }

    def save(Xml xml) {
        def filename = namespaceToFile.get(xml.targetNamespace)
        def file = new File(dest, filename)
        file.getParentFile().mkdirs()
        file.withWriter { 
            it << replaceLocations(xml)
        }
    }

    private String replaceLocations(Xml xml) {
        def thisFileLocalPath = namespaceToFile.get(xml.targetNamespace)
        File thisFile = new File(dest, thisFileLocalPath)
        def innerXml = xml.getInnerXml();
        Map<String,String> replaceMap = new HashMap<>()
        innerXml.each {
            def ns = it.targetNamespace
            def localPath = namespaceToFile.get(ns)
            if(localPath==null){
                throw new NullPointerException(ns)
                
            }
            def to = new File(dest, localPath)
            def relative = RelativePath.convertToRelativePath(thisFile.parent,to.absolutePath)
            replaceMap.put(it.url.toString(), relative)
        }
        String resultXml = xml.text
        replaceMap.each {k,v->
            resultXml = resultXml.replace(k,v)
        }
        return resultXml
    }

    private Set<Xml> getAllXmls(){
        Set<Xml> xmls = new HashSet<>()
        sources.each {
            def xml = new Xml(it)
            xmls.add(xml)
            xmls.addAll(xml.innerXml)
        }
        return xmls
    }

    @Override
    void src(Object src) {
        if (sources == null) {
            sources = new ArrayList<URL>(1);
        }
        if (src instanceof Closure) {
            //lazily evaluate closure
            Closure<?> closure = (Closure<?>)src;
            src = closure.call();
        }

        if (src instanceof CharSequence) {
            sources.add(new URL(src.toString()));
        } else if (src instanceof URL) {
            sources.add((URL)src);
        } else if (src instanceof Collection) {
            Collection<?> sc = (Collection<?>)src;
            for (Object sco : sc) {
                this.src(sco);
            }
        } else if (src != null && src.getClass().isArray()) {
            int len = Array.getLength(src);
            for (int i = 0; i < len; ++i) {
                Object sco = Array.get(src, i);
                this.src(sco);
            }
        } else {
            throw new IllegalArgumentException("Download source must " +
                    "either be a URL, a CharSequence, a Collection or an array.");
        }
    }

    @Override
    void dest(Object dest) {
        if (dest instanceof Closure) {
            Closure<?> closure = (Closure<?>)dest;
            dest = closure.call();
        }
        if (dest instanceof CharSequence) {
            this.dest = new File(dest.toString());
        } else if (dest instanceof File) {
            this.dest = dest;
        } else {
            throw new IllegalArgumentException("Download destination must " +
                    "either be a File or a CharSequence");
        }
    }

    @Override
    void namespaceToFile(Map map) {
        namespaceToFile = map
    }
}
