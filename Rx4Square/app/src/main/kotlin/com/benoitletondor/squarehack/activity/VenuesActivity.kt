package com.benoitletondor.squarehack.activity

import android.location.Location
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import com.benoitletondor.squarehack.Logger
import com.benoitletondor.squarehack.R
import com.benoitletondor.squarehack.data.RatingLoadingStatus
import com.benoitletondor.squarehack.data.Venue
import com.benoitletondor.squarehack.data.WebserviceHelper
import com.benoitletondor.squarehack.location.createPlayServicesLocationObservable
import com.benoitletondor.squarehack.ui.VenuesAdapter
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import java.util.*
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.exceptions.OnErrorThrowable
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Base Activity of the application that displays venues around the user
 *
 * @author Benoit LETONDOR
 */
public class VenuesActivity: PlayServicesActivity
{
    /**
     * Recycler view used to display venues (will not be null at runtime)
     */
    private var venuesRecyclerView: RecyclerView? = null
    /**
     * Recycler view adaptor (will not be null at runtime)
     */
    private var venuesRecyclerViewAdapter: VenuesAdapter? = null
    /**
     * Observable for location updates
     */
    private var locationSubscription:Subscription? = null

// ----------------------------------------->

    constructor() : super(Array(1, {LocationServices.API})){}

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venues)

        /*
         * Recycler view init
         */
        venuesRecyclerView = findViewById(R.id.venues_recycler_view) as RecyclerView

        // for optimization as long as content do not change the layout size of the RecyclerView
        venuesRecyclerView!!.setHasFixedSize(true)

        // use a linear layout manager
        venuesRecyclerView!!.setLayoutManager(LinearLayoutManager(this));

        // init adapter
        venuesRecyclerViewAdapter = VenuesAdapter()
        venuesRecyclerView!!.setAdapter(venuesRecyclerViewAdapter);
    }

    override fun onDestroy()
    {
        locationSubscription = null

        super.onDestroy()
    }

    // ------------------------------------------->

    override fun onPlayServicesAvailable(googleApiClient: GoogleApiClient)
    {
        Logger.debug("onPlayServicesAvailable")

        val locationRequest:LocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
            .setInterval(10000)
            .setFastestInterval(7500)

        var toast:Toast? = null

        locationSubscription = createPlayServicesLocationObservable(googleApiClient, locationRequest)
            // Filter on distinct locations
            .distinctUntilChanged{ location ->
                location.getLatitude() + location.getLongitude()
            }
            // For every location fetch venues
            .flatMap{ location ->
                Logger.debug("Fetching venues for location  ${location.getLatitude()},${location.getLongitude()} ...")

                toast?.cancel()
                toast = Toast.makeText(this, R.string.toast_fetching_venues, Toast.LENGTH_LONG)
                toast!!.show()

                WebserviceHelper.fetchFoursquareVenuesData(location)
                    .subscribeOn(Schedulers.io())
            }
            // Fetch rating for venues
            .flatMap { venues ->
                Logger.debug("Venues retrieved")

                Observable.from(venues)
                    // For each venue, fetch rating
                    .flatMap{ venue ->
                        venue.ratingAvailability = RatingLoadingStatus.LOADING

                        WebserviceHelper.fetchFoursquareVenueRating(venue)
                            .subscribeOn(Schedulers.io())
                            // Handle error by simply setting the error status and returning this venue
                            .onErrorReturn{ error ->
                                Logger.error("Error while fetching rating for venue ${venue.name}", error)
                                venue.ratingAvailability = RatingLoadingStatus.ERROR

                                venue
                            }
                            // Set state on complete
                            .doOnNext{ venue ->
                                if( venue.ratingAvailability != RatingLoadingStatus.ERROR ) // check if not in error since we catch errors
                                {
                                    venue.ratingAvailability = if( venue.rating == null ) RatingLoadingStatus.NOT_AVAILABLE else RatingLoadingStatus.AVAILABLE
                                }
                            }
                    }
                    // Sort venues by distance and fallback on name
                    .toSortedList{ venue1, venue2 ->
                        if( venue1.location.distance != null && venue2.location.distance != null )
                        {
                            venue1.location.distance - venue2.location.distance
                        }
                        else
                        {
                            venue1.name.compareTo(venue2.name)
                        }
                    }

            }
            .observeOn(AndroidSchedulers.mainThread())
            .retry(5) // 5 retry before error
            .subscribe({ venues ->
                toast?.cancel()

                venuesRecyclerViewAdapter?.updateData(venues)
            },
            { error ->
                toast?.cancel()

                // Unable to get location
                Logger.error("Error while fetching venues", error)

                Toast.makeText(this, R.string.toast_error_fetching_venues, Toast.LENGTH_SHORT).show()
            })
    }

    override fun onPlayServicesNotAvailable()
    {
        Logger.debug("onPlayServicesNotAvailable")
        locationSubscription?.unsubscribe()
    }
}
