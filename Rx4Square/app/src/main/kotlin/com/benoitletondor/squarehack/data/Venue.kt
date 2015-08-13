package com.benoitletondor.squarehack.data

/**
 * Data object that describes a venue fetched from Foursquare API
 *
 * @author Benoit LETONDOR
 */
public data class Venue
(
    val id:String,
    val name:String,
    val location:Location,
    val mainCategory:Category?,
    val verified:Boolean,
    val stats:Stats,
    var ratingAvailability:RatingLoadingStatus = RatingLoadingStatus.NOT_LOADED,
    var rating:Double? = null
)

/**
 * Enum that defines state of loading for a rating
 */
enum class RatingLoadingStatus
{
    /**
     * Rating has not been fetched
     */
    NOT_LOADED,
    /**
     * Rating is currently fetched
     */
    LOADING,
    /**
     * The rating is fetched and available
     */
    AVAILABLE,
    /**
     * The rating is fetched but not available
     */
    NOT_AVAILABLE,
    /**
     * An error occured during rating fetching
     */
    ERROR
}

/**
 * Data object that describes a location of a venue fetched from Foursquare API
 *
 * @author Benoit LETONDOR
 */
public data class Location
(
    val lat:Double? = null,
    val lng:Double? = null,
    val distance:Int? = null,
    val address:String? = null,
    val crossStreet:String? = null,
    val city:String? = null,
    val state:String? = null,
    val postalCode:String? = null,
    val country:String? = null
)

/**
 * Data object that describes a category of a venue fetched from Foursquare API
 *
 * @author Benoit LETONDOR
 */
public data class Category
(
    val id:String,
    val name:String,
    val pluralName:String,
    val icon:String
)

/**
 * Data object that describes stats about a venue fetched from Foursquare API
 *
 * @author Benoit LETONDOR
 */
public data class Stats
(
    val checkinsCount:Int,
    val usersCount:Int,
    val tipCount:Int
)