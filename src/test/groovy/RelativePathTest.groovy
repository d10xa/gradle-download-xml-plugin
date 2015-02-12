import org.junit.Test

import static org.hamcrest.CoreMatchers.equalTo
import static org.hamcrest.MatcherAssert.assertThat
import static ru.d10xa.downloadxml.RelativePath.convertToRelativePath

class RelativePathTest {
    
    @Test void relative(){
        def from = '/home/user/example/build/c/service.wsdl'
        def to = '/home/user/example/build/a/b/base.xsd'
        assertThat convertToRelativePath(from, to), equalTo('../../a/b/base.xsd')
        assertThat convertToRelativePath(from, new File(to).parent), equalTo('../../a/b')
        assertThat convertToRelativePath(new File(from).parent, to), equalTo('../a/b/base.xsd')
    }
}
