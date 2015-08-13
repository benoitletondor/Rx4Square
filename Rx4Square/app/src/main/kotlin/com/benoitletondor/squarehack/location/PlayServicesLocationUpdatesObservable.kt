package com.benoitletondor.squarehack.location

import android.location.Location
import com.benoitletondor.squarehack.Logger
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import rx.Observable
import rx.Subscriber
import rx.subscriptions.Subscriptions

/**
 * Class that implements an Observable subscription to query PlayServices for the user position with regular updates
 *
 * @author Benoit LETONDOR
 */
private class PlayServicesLocationUpdatesObservable(val googleApiClient: GoogleApiClient, val locationRequest:LocationRequest) :  Observable.OnSubscribe<Location>
{
    override fun call(t: Subscriber<in Location>?)
    {
        if( t == null )
        {
            return
        }

        if( !googleApiClient.isConnected() )
        {
            t.onError(IllegalStateException("Google API not connected"))
            return
        }

        val listener:SubscriberLocationListener = SubscriberLocationListener(t)

        Logger.debug("Subscribed to location updates")

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, listener)

        // Remove location updates on unsub
        t.add(Subscriptions.create(
        {
            Logger.debug("Unsubscribed to location updates")
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, listener)
        }))
    }

    /**
     * Inner class that contains a subscriber and implements the location listener interface
     * TODO check if inline implementation is possible in Kotlin
     */
    private class SubscriberLocationListener(val subscriber:Subscriber<in Location>) : LocationListener
    {
        override fun onLocationChanged(location: Location?)
        {
            if ( location != null )
            {
                Logger.debug("Location retrieved : ${location.getLatitude()},${location.getLongitude()}")
                subscriber.onNext(location)
            }
        }
    }

}

/**
 * Helper to create an Observable that will get the user location with updates
 *
 * @return an Observable for location
 */
fun createPlayServicesLocationUpdatesObservable(googleApiClient:GoogleApiClient, locationRequest:LocationRequest) : Observable<Location>
{
    return Observable.create(PlayServicesLocationUpdatesObservable(googleApiClient, locationRequest))
}