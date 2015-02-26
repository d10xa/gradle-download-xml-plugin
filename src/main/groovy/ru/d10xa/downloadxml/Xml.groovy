package ru.d10xa.downloadxml

import groovy.transform.EqualsAndHashCode
import groovy.transform.Memoized
import groovy.transform.ToString
import org.w3c.dom.Document
import org.xml.sax.InputSource

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

@EqualsAndHashCode(includes = ["url"])
@ToString(includes = "url", includePackage = false)
class Xml {

    URL url
    String rawUrl
    BasicAuth basicAuth
    LocationParams locationParams

    Xml(){}
    Xml(CharSequence url) { this.url = url.toURL() }

    Xml(URL url) { this.url = url }
    
    Xml(Xml parent,URL url){
        this.basicAuth = parent.basicAuth
        this.locationParams = parent.locationParams
        this.url = url
    }

    Xml(URL url, BasicAuth basicAuth) { this.url = url; this.basicAuth = basicAuth }

    @Memoized
    private static getTextFromUrl(URL url, BasicAuth basicAuth) {
        if (basicAuth) {
            def connection = url.openConnection()
            basicAuth.fillRequestProperty(connection)
            return connection.content.text
        } else {
            return url.text
        }
    }

    public String getText() {
        getTextFromUrl(url, basicAuth)
    }

    @Memoized
    public List<Location> getSchemaLocations() {
        def doc = getDocument()
        XPathFactory xPathFactory = XPathFactory.newInstance()
        def xPath = xPathFactory.newXPath()
        def expression = xPath.compile("//*[local-name()=\'import']/attribute::schemaLocation")
        def expressionInclude = xPath.compile("//*[local-name()=\'include']/attribute::schemaLocation")
        def evaluate = expression.evaluate(doc, XPathConstants.NODESET)
        def list = new ArrayList<Location>()
        evaluate.each {
            list.add(
                    new Location(
                            raw: it?.value as String,
                            locationsProcessor: locationParams?.locationsProcessor
                    ))
        }
        evaluate = expressionInclude.evaluate(doc, XPathConstants.NODESET)
        evaluate.each {
            list.add(
                    new Location(
                            raw: it?.value as String,
                            locationsProcessor: locationParams?.locationsProcessor
                    ))
        }
        list
    }

    @Memoized
    public String getTargetNamespace() {
        def doc = getDocument()
        def element = doc.getDocumentElement()
        def attribute = element.getAttribute("targetNamespace")
        attribute
    }

    @Memoized
    public Set<Xml> getInnerXml() {
        return new XmlLoader(this).getInnerXml();
    }

    @Memoized
    private Document getDocument() {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        InputSource is = new InputSource()
        is.setCharacterStream(new StringReader(text))
        dBuilder.parse(is)
    }

}

