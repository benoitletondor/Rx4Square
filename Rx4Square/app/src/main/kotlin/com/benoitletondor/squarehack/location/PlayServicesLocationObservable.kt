package com.benoitletondor.squarehack.location

import android.location.Location
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import rx.Observable

/**
 * Helper to create an Observable that will retrieve the position of the user using both last known position at the beginning and updates
 *
 * @return an observable for Location
 */
public fun createPlayServicesLocationObservable(googleApiClient: GoogleApiClient, locationRequest: LocationRequest) : Observable<Location>
{
    return Observable.merge(createPlayServicesLastKnownLocationObservable(googleApiClient), createPlayServicesLocationUpdatesObservable(googleApiClient, locationRequest))
}
