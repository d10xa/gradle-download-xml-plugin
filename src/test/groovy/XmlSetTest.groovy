import org.junit.Test
import ru.d10xa.downloadxml.Xml

class XmlSetTest {

    @Test void 'check Xml equals and hashcode'(){
        def url = 'http://localhost'
        def set = new LinkedHashSet<Xml>()
        (1..10).each { set += new Xml(url) }
        assert set.size() == 1
    }

}
