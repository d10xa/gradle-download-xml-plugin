Gradle Download Xml Plugin
==========================

Apply plugin
------------

```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'ru.d10xa:gradle-download-xml-plugin:0.0.1'
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
