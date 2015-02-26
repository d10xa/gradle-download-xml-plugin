package ru.d10xa.downloadxml

import groovy.transform.Memoized

class LocationsProcessor {

    private Closure locationsHandler

    LocationsProcessor(Closure locationsHandler) {
        this.locationsHandler = locationsHandler
    }

    @Memoized
    String process(String location) {
        locationNeedProcessing(location) ? locationsHandler(location) : location
    }

    private boolean locationNeedProcessing(String location) {
        if (locationsHandler == null) {
            return false
        }
        try {
            new URL(location)
            return false
        } catch (MalformedURLException e) {
            return true
        }
    }

}
