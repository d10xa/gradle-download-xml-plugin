package ru.d10xa.downloadxml

import groovy.transform.Memoized

class Location {
    
    String raw
    LocationsProcessor locationsProcessor
    
    @Memoized
    String getProcessed(){
        locationsProcessor == null ? raw : locationsProcessor.process(raw)
    }
    
    boolean isMalformed(){
        !raw.equals(getProcessed())
    }
    
}
