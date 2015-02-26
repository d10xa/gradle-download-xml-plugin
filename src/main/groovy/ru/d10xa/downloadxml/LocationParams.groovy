package ru.d10xa.downloadxml

class LocationParams {

    Closure malformedLocationHandler
    LocationsProcessor locationsProcessor

    void malformedLocationHandler(Closure closure){
        this.malformedLocationHandler = closure
        this.locationsProcessor = new LocationsProcessor(closure)
    }

}
