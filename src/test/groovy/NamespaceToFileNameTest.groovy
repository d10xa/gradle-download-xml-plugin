import org.junit.Test

import static ru.d10xa.downloadxml.NamespaceUtils.namespaceToFileName

class NamespaceToFileNameTest {

    def validNamespace = 'http://localhost:8080/schema/foo/bar/baz'
    def invalidNamespace = 'foo?!@#$%^&*()+bar!@#$baz'

    @Test
    void 'check with valid namespace'() {
        assert namespaceToFileName(validNamespace) == 'schema-foo-bar-baz.xml'
    }

    @Test
    void 'check for namespace with invalid characters'() {
        assert namespaceToFileName(invalidNamespace) == 'foo-bar-baz.xml'
    }

    @Test
    void 'check empty value'() {
        assert namespaceToFileName('') == '_.xml'
    }

    @Test
    void 'check null value'() {
        assert namespaceToFileName(null) == '_.xml'
    }
}
