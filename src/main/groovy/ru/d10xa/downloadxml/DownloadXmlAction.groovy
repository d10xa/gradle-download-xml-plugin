package ru.d10xa.downloadxml

import groovy.transform.Memoized
import org.gradle.api.Project
import org.gradle.util.ConfigureUtil

import java.lang.reflect.Array

class DownloadXmlAction implements DownloadXmlSpec {

    private Project project
    private Set<URL> sources = new LinkedHashSet<URL>(1)
    private File dest
    private Map<String,String> namespaceToFile = [:]
    private String username
    private String password
    private LocationParams locations
    
    void warn (String s){
        if(project){
            project.logger.warn s
        } else {
            println s
        }
    }
    
    public void execute(Project project){
        this.project = project
        mappedXmls.each {save it}
        if(notMappedNamespaces.size()>0){
            warn("Some namespaces are not mapped. Try to add it to downloadXml extension")
            notMappedNamespaces.each {
                warn "'${it}':'${NamespaceUtils.namespaceToFileName(it)}',"
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
                return
            }
            
            def to = new File(dest, localPath).absolutePath
            def from = thisFile.parentFile.absolutePath
            
            def relative = RelativePath.convertToRelativePath(from,to)

            def locationName = it.rawUrl ?: it.url.toString()
            
            replaceMap.put(locationName, relative)
        }
        String resultXml = xml.text
        replaceMap.each {k,v->
            resultXml = resultXml.replace(k,v)
        }
        return resultXml
    }

    @Memoized
    private Set<Xml> getMappedXmls(){
        allXmls.findAll {namespaceToFile.containsKey(it.targetNamespace)}
    }

    @Memoized
    private Set<Xml> getNotMappedXmls(){
        allXmls.findAll {!namespaceToFile.containsKey(it.targetNamespace)}
    }

    @Memoized
    private Set<String> getNotMappedNamespaces(){
        def result  = new TreeSet()
        def xmls = getNotMappedXmls()
        for (Xml xml: xmls ) {
            result += xml.targetNamespace
        }
        return result
    }

    @Memoized
    private Set<Xml> getAllXmls(){
        Set<Xml> xmls = new TreeSet<>(new Comparator<Xml>() {
            @Override
            int compare(Xml o1, Xml o2) {
                if(o1?.url==null||o2?.url==null){
                    return false
                }
                return o1.url.toString().compareTo(o2.url.toString())
            }
        })
        sources.each {
            def xml = new Xml(
                    url:it,
                    basicAuth:basicAuth,
                    locationParams: locations
            )
            xml.locationParams = this.locations
            xmls.add(xml)
            xmls.addAll(xml.innerXml)
        }
        return xmls
    }

    @Override
    void src(Object src) {
        if (sources == null) {
            sources = new LinkedHashSet<URL>(1);
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

    private BasicAuth getBasicAuth(){
        return new BasicAuth(username,password)
    }

    @Override
    void namespaceToFile(Map map) {
        namespaceToFile = map
    }

    @Override
    void username(String username) {
        this.username = username
    }

    @Override
    void password(String password) {
        this.password = password
    }

    @Override
    void locations(Object locations) {
        if(locations instanceof Closure){
            this.locations = ConfigureUtil.configure(locations,new LocationParams())
        } else if(locations instanceof LocationParams){
            this.locations = locations
        } else {
            throw new IllegalArgumentException("'locations' must be Closure");
        }
    }
}
