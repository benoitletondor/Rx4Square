package com.benoitletondor.squarehack.data

import android.location.Location
import com.benoitletondor.squarehack.BuildConfig
import com.benoitletondor.squarehack.Logger
import com.benoitletondor.squarehack.data
import com.squareup.okhttp.HttpUrl
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import org.json.JSONArray
import org.json.JSONObject
import rx.Observable
import rx.exceptions.OnErrorThrowable
import rx.functions
import java.io.IOException
import java.util.ArrayList

/**
 * Helper to query Foursquare webservices
 *
 * @author Benoit LETONDOR
 */
public object WebserviceHelper
{
    /**
     * HttpUrl of the venues API
     */
    private val FOURSQUARE_VENUES_ENDPOINT_URL:HttpUrl = HttpUrl.parse("https://api.foursquare.com/v2/venues/search")
    /**
     * String URL of the venue, "VENUE_ID" should be replaced by the actual id of the venue
     */
    private val FOURSQUARE_VENUE_ENDPOINT:String = "https://api.foursquare.com/v2/venues/VENUE_ID"

    /**
     * Foursquare API version
     */
    private val FOURSQUARE_API_VERSION: String = "20150726"

// --------------------------------------------------------->

    /**
     * Create an Observable to populate rating data of a venue
     *
     * @param venue Venue to populate
     * @return an Observable that will query Foursquare API on subscribe
     */
    public fun fetchFoursquareVenueRating(venue: Venue): Observable<Venue>
    {
        return Observable.create<Venue>
        {
            try
            {
                val urlBuilder:HttpUrl.Builder = HttpUrl.parse(FOURSQUARE_VENUE_ENDPOINT.replace("VENUE_ID", venue.id)).newBuilder()
                addCredentialsData(urlBuilder)

                val request: Request = Request.Builder()
                        .url(urlBuilder.build())
                        .build();

                val client: OkHttpClient = OkHttpClient()
                val response: Response = client.newCall(request).execute()

                if (response.isSuccessful())
                {
                    populateVenueWithWebserviceData(venue, response)

                    it.onNext(venue)
                    it.onCompleted()
                }
                else
                {
                    throw IOException("bad http code : ${response.code()}")
                }
            }
            catch(e:Exception)
            {
                OnErrorThrowable.addValueAsLastCause(e, venue)
                it.onError(OnErrorThrowable.from(e))
            }
        }
    }

    /**
     * Parse the webservice response to populate rating<br />
     * NB : This function will not update the ratingAvailability of the venue
     *
     * @param venue the venue to populate
     * @param response the webservice response
     */
    private fun populateVenueWithWebserviceData(venue: Venue, response:Response)
    {
        val jsonString: String = response.body().string()

        val json: JSONObject = JSONObject(jsonString).getJSONObject("response").getJSONObject("venue")

        if( jsonHasField(json, "rating") )
        {
            venue.rating = json.getDouble("rating")
        }
    }

// --------------------------------------------------------->

    /**
     * Create an Observable to query venues for the given location
     *
     * @param userLocation location of the user
     * @return an Observable that will query Foursquare API on subscribe
     */
    public fun fetchFoursquareVenuesData(userLocation:Location): Observable<List<Venue>>
    {
        return Observable.create<List<Venue>>
        {
            try
            {
                val latitude:Double = userLocation.getLatitude()
                val longitude:Double = userLocation.getLongitude()

                val urlBuilder:HttpUrl.Builder = FOURSQUARE_VENUES_ENDPOINT_URL.newBuilder()
                addCredentialsData(urlBuilder)
                urlBuilder.addQueryParameter("ll", "$latitude,$longitude")

                val request: Request = Request.Builder()
                        .url(urlBuilder.build())
                        .build();

                val client: OkHttpClient = OkHttpClient()
                val response: Response = client.newCall(request).execute()

                if (response.isSuccessful())
                {
                    it.onNext(serializeVenuesWebserviceData(response))
                    it.onCompleted()
                }
                else
                {
                    throw IOException("bad http code : ${response.code()}")
                }
            }
            catch(e:Exception)
            {
                it.onError(e)
            }
        }
    }

    /**
     * Serialize webservice response to a list of Venue objects
     *
     * @param response venues webservice response
     * @return list of venues
     */
    private fun serializeVenuesWebserviceData(response:Response): List<Venue>
    {
        val jsonString: String = response.body().string()

        val json: JSONObject = JSONObject(jsonString)
        val resp: JSONArray = json.getJSONObject("response").getJSONArray("venues")

        val venues: MutableList<Venue> = ArrayList<Venue>(resp.length())

        for(i in 0..resp.length()-1)
        {
            venues.add(serializeVenueWebserviceData(resp.getJSONObject(i)))
        }

        return venues
    }

    /**
     * Create a Venue object out of the webservice response json
     *
     * @param venueJSON webservice json of a venue
     * @return a populated Venue object
     */
    private fun serializeVenueWebserviceData(venueJson: JSONObject) : Venue
    {
        return Venue(
            venueJson.getString("id"),
            venueJson.getString("name"),
            serializeLocationWebserviceData(venueJson.getJSONObject("location")),
            serializeMainCategoryWebserviceData(venueJson.getJSONArray("categories")),
            venueJson.getBoolean("verified"),
            serializeStatsWebserviceData(venueJson.getJSONObject("stats"))
        )
    }

    /**
     * Create a Location object out of the webservice response json
     *
     * @param locationJson webservice json of a location
     * @return a populated Location object
     */
    private fun serializeLocationWebserviceData(locationJson: JSONObject) : data.Location
    {
        return data.Location(
            if (jsonHasField(locationJson, "lat")) locationJson.getDouble("lat") else null,
            if (jsonHasField(locationJson, "lng")) locationJson.getDouble("lng") else null,
            if (jsonHasField(locationJson, "distance")) locationJson.getInt("distance") else null,
            if (jsonHasField(locationJson, "address")) locationJson.getString("address") else null,
            if (jsonHasField(locationJson, "crossStreet")) locationJson.getString("crossStreet") else null,
            if (jsonHasField(locationJson, "city")) locationJson.getString("city") else null,
            if (jsonHasField(locationJson, "state")) locationJson.getString("state") else null,
            if (jsonHasField(locationJson, "postalCode")) locationJson.getString("postalCode") else null,
            if (jsonHasField(locationJson, "country")) locationJson.getString("country") else null
        )
    }

    /**
     * Parse the webservice JSON array of categories and extract the main one if available
     *
     * @param categoriesJsonArray webservice json array of categories
     * @return a populated Category object if available, null otherwise
     */
    private fun serializeMainCategoryWebserviceData(categoriesJsonArray: JSONArray) : Category?
    {
        for(i in 0..categoriesJsonArray.length()-1)
        {
            val categorieJson: JSONObject = categoriesJsonArray.getJSONObject(i)

            // Ignore secondary categories, only take the primary one
            if( !jsonHasField(categorieJson, "primary") || !categorieJson.getBoolean("primary") )
            {
                continue
            }

            return Category(
                categorieJson.getString("id"),
                categorieJson.getString("name"),
                categorieJson.getString("pluralName"),
                categorieJson.getJSONObject("icon").getString("prefix") + "bg_" + "88" + categorieJson.getJSONObject("icon").getString("suffix") //FIXME optim resolution (88 hardcoded)
            )
        }

        return null
    }

    /**
     * Parse the webservice JSON of a venue statistic
     *
     * @param statsJson json of the venue stats
     * @return a populated Stats object
     */
    private fun serializeStatsWebserviceData(statsJson: JSONObject) : Stats
    {
        return Stats(
            statsJson.getInt("checkinsCount"),
            statsJson.getInt("usersCount"),
            statsJson.getInt("tipCount")
        )
    }

    /**
     * Check if a json object contains a field with a non null value
     *
     * @return true of the field exists and is not null
     */
    private fun jsonHasField(json: JSONObject, field:String) : Boolean
    {
        return json.has(field) && !json.isNull(field)
    }

// --------------------------------------------------------->

    /**
     * Helper to add Foursquare GET credentials data to an url builder
     *
     * @param builder an http URL builder to populate
     */
    private fun addCredentialsData(builder:HttpUrl.Builder)
    {
        builder.addQueryParameter("client_id", BuildConfig.FOURSQUARE_CLIENT_ID)
        builder.addQueryParameter("client_secret", BuildConfig.FOURSQURE_CLIENT_SECRET)
        builder.addQueryParameter("m", "foursquare")
        builder.addQueryParameter("v", FOURSQUARE_API_VERSION)
    }
}