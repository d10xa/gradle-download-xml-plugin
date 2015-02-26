import org.gradle.util.ConfigureUtil
import org.junit.Before
import org.junit.Test
import ru.d10xa.downloadxml.DownloadXmlAction

class MalformedSchemaLocationTest {

    static String testDir = 'build/test-malformed-location'
    
    def downloadTask = {
        def path = "file:${new File('src/test/resources/malformed-schema').absolutePath}"
        src("$path/main.xsd")
        locations { malformedLocationHandler { "$path/$it" } }
        dest testDir
        namespaceToFile([
                'http://localhost/bar':'test-bar.xml',
                'http://localhost/foo':'test-foo.xml',
                'http://localhost/main':'test-main.xml',
        ])
    }
    
    @Before
    public void executeTask(){
        ConfigureUtil.configure(downloadTask, new DownloadXmlAction()).execute()
    }

    @Test
    public void 'download with malformed location'() {
        assert new File(testDir,'test-main.xml').text.contains('schemaLocation="test-foo.xml"')
        assert new File(testDir,'test-foo.xml').text.contains('schemaLocation="test-bar.xml"')
    }

}
