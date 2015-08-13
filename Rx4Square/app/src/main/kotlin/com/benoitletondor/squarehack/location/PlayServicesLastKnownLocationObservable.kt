package com.benoitletondor.squarehack.location

import android.location.Location
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import rx.Observable
import rx.Subscriber
import java.io.IOException

/**
 * Class that implements an Observable subscription to query PlayServices for the lastKnownPosition of the user
 *
 * @author Benoit LETONDOR
 */
private class PlayServicesLastKnownLocationObservable(val googleApiClient:GoogleApiClient) :  Observable.OnSubscribe<Location>
{
    override fun call(t: Subscriber<in Location>?)
    {
        if( t==null )
        {
            return
        }

        if( !googleApiClient.isConnected() )
        {
            t.onError(IllegalStateException("Google API not connected"))
            return
        }

        val location:Location? = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)

        if (location != null)
        {
            t.onNext(location) // Only send the position of available
        }

        t.onCompleted() // Complete if position is null or not
    }

}

/**
 * Helper to create an Observable that will retrieve the last known user position
 *
 * @return an observable for Location that will complete after first try
 */
fun createPlayServicesLastKnownLocationObservable(googleApiClient:GoogleApiClient) : Observable<Location>
{
    return Observable.create(PlayServicesLastKnownLocationObservable(googleApiClient))
}