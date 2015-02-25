import org.junit.Test

import static ru.d10xa.downloadxml.NamespaceUtils.namespaceToFileName

class NamespaceToFileNameTest {

    def validNamespace = 'http://localhost:8080/schema/foo/bar/baz'
    def invalidNamespace = 'foo?!@#$%^&*()+bar!@#$baz'

    @Test
    public void 'check with valid namespace'() {
        assert namespaceToFileName(validNamespace).equals('schema-foo-bar-baz.xml')
    }

    @Test
    public void 'check for namespace with invalid characters'() {
        assert namespaceToFileName(invalidNamespace).equals('foo-bar-baz.xml')
    }

    @Test
    public void 'check empty value'() {
        assert namespaceToFileName('').equals('_.xml')
    }

    @Test
    public void 'check null value'() {
        assert namespaceToFileName(null).equals('_.xml')
    }
}
