import org.junit.Test
import ru.d10xa.downloadxml.Location
import ru.d10xa.downloadxml.LocationsProcessor

class LocationTest {
    
    @Test void 'malformed location test'(){
        def handler = {url-> "http://localhost:8080/$url"}
        
        Location location = new Location(
                raw: "abc",
                locationsProcessor: new LocationsProcessor(handler)
        )     

        assert location.raw == 'abc'
        assert location.processed == 'http://localhost:8080/abc'
        assert location.malformed == true
    }
    
    @Test void 'well formed location test'(){
        def handler = {url-> "---$url---"}

        Location location = new Location(
                raw: "http://localhost:8080/abc",
                locationsProcessor: new LocationsProcessor(handler)
        )

        assert location.raw == 'http://localhost:8080/abc'
        assert location.processed == 'http://localhost:8080/abc'
        assert location.malformed == false
    }


}
