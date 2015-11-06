Gradle Download Xml Plugin
==========================

[ ![Download](https://api.bintray.com/packages/d10xa/maven/ru.d10xa%3Agradle-download-xml-plugin/images/download.svg) ](https://bintray.com/d10xa/maven/ru.d10xa%3Agradle-download-xml-plugin/_latestVersion)

Apply plugin
------------

### Gradle >= 2.1

```groovy
plugins {
    id 'ru.d10xa.download-xml' version '0.0.4'
}
```

### Gradle < 2.1

```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'ru.d10xa:gradle-download-xml-plugin:0.0.4'
    }
}

apply plugin: 'ru.d10xa.download-xml'

```

Usage
-----

```groovy
task downloadWsdl << {
    println 'Downloading...'
    
    downloadXml {
        src(['http://www.predic8.com:8080/crm/CustomerService?wsdl'])
        dest buildDir
        namespaceToFile([
                'http://predic8.com/wsdl/crm/CRMService/1/': 'service.wsdl',
                'http://predic8.com/crm/1/'                : 'a/b/c/schema-crm.xsd',
                'http://predic8.com/common/1/'             : 'x/y/z/schema-common.xsd'
        ])
        // username 'foo' // optional
        // password 'bar' // optional
        // locations {
        //      malformedLocationHandler { 
        //          "http://localhost:8080/$it" // optional
        //      } 
        // }
    }
    
    println 'Download Complete...'
}
```

schema locations will be overwritten by the relative
```xml
<!--in a/b/c/schema-crm.xsd-->
<xsd:import schemaLocation="../../../x/y/z/schema-common.xsd"
    namespace="http://predic8.com/common/1/"></xsd:import>
```

Malformed schema locations
--------------------------

You can handle local schema locations with malformedLocationHandler
```groovy
locations { 
    malformedLocationHandler { "http://localhost:8080/$it" }
}
```
