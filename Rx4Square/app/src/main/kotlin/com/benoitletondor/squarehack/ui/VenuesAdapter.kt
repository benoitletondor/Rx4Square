package com.benoitletondor.squarehack.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.benoitletondor.squarehack.Logger
import com.benoitletondor.squarehack.R
import com.benoitletondor.squarehack.data.RatingLoadingStatus
import com.benoitletondor.squarehack.data.Venue
import com.squareup.picasso.Picasso
import java.util.*

/**
 * Recycler view adapter for venues display
 *
 * @author Benoit LETONDOR
 */
public class VenuesAdapter : RecyclerView.Adapter<VenuesAdapter.ViewHolder>()
{
    /**
     * Current list of venues
     */
    private val venues: MutableList<Venue> = ArrayList<Venue>()

// ----------------------------------------->

    /**
     * Update the list of venues with new ones
     *
     * @param newVenues new venues to add
     */
    public fun updateData(newVenues: List<Venue>)
    {
        venues.clear()
        venues.addAll(newVenues)

        notifyDataSetChanged()
    }

// ----------------------------------------->

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder?
    {
        return ViewHolder(LayoutInflater.from(parent!!.getContext()).inflate(R.layout.cell_venue, parent, false)!!)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int)
    {
        if( holder == null )
        {
            return
        }

        val venue: Venue = venues.get(position)

        // Name of the venue
        holder.nameTextView.setText(venue.name)

        // Distance
        if( venue.location.distance != null )
        {
            holder.distanceTextView.setText("${venue.location.distance} m")
            holder.distanceTextView.setVisibility(View.VISIBLE)
        }
        else
        {
            holder.distanceTextView.setVisibility(View.GONE)
        }

        /*
         * Category image
         */
        if( venue.mainCategory != null )
        {
            // Check if the currently loaded url needs to be changed
            if( !venue.mainCategory.icon.equals(holder.categoryIconLoadedUrl) )
            {
                // Load category icon
                Picasso.with(holder.context)
                    .load(venue.mainCategory.icon)
                    .resizeDimen(R.dimen.venue_cell_category_iv_size, R.dimen.venue_cell_category_iv_size)
                    .placeholder(R.drawable.category_unknown)
                    .into(holder.categoryIcon)

                // Save loaded url to avoid useless reloads
                holder.categoryIconLoadedUrl = venue.mainCategory.icon
            }
        }
        else
        {
            holder.categoryIcon.setImageResource(R.drawable.category_unknown)
        }

        /*
         * Rating
         */
        if( venue.ratingAvailability == RatingLoadingStatus.AVAILABLE && venue.rating!! != holder.displayedRating )
        {
            holder.ratingLayout.setVisibility(View.VISIBLE)
            holder.ratingLoadingProgressBar.setVisibility(View.GONE)
            holder.ratingLoadingTextView.setVisibility(View.GONE)
            drawRating(holder, venue.rating!! / 2.0) // /2 cause rating goes up to 10 in Foursquare API and we only have 5 stars

            holder.displayedRating = venue.rating!!
        }
        else if( venue.ratingAvailability == RatingLoadingStatus.LOADING || venue.ratingAvailability == RatingLoadingStatus.NOT_LOADED )
        {
            holder.ratingLayout.setVisibility(View.GONE)
            holder.ratingLoadingProgressBar.setVisibility(View.VISIBLE)
            holder.ratingLoadingTextView.setVisibility(View.GONE)
        }
        else
        {
            holder.ratingLayout.setVisibility(View.GONE)
            holder.ratingLoadingProgressBar.setVisibility(View.GONE)

            if( venue.ratingAvailability == RatingLoadingStatus.NOT_AVAILABLE )
            {
                holder.ratingLoadingTextView.setText(R.string.rating_not_available)
            }
            else if( venue.ratingAvailability == RatingLoadingStatus.ERROR )
            {
                holder.ratingLoadingTextView.setText(R.string.rating_loading_error)
            }

            holder.ratingLoadingTextView.setVisibility(View.VISIBLE)
        }
    }

    override fun getItemCount(): Int
    {
        return venues.size()
    }

    /**
     * Draw rating stars depending on the rating
     *
     * @param holder
     * @param rating
     */
	private fun drawRating(holder:ViewHolder, rating:Double)
	{
        for(i in 1.0..5.0)
        {
            val imageView = getImageViewForRating(holder, i)

            if (rating >= i)
            {
                imageView.setImageResource(R.drawable.star_full)
            }
            else if (rating < i && rating > i - 1)
            {
                imageView.setImageResource(R.drawable.star_half)
            }
            else
            {
                imageView.setImageResource(R.drawable.star_empty)
            }
        }
    }

    /**
     * Get the image view for the given rating
     *
     * @param holder
     * @param rating
     * @return the image view for the given rating value
     */
    private fun getImageViewForRating(holder:ViewHolder, rating:Double):ImageView
    {
        when (rating.toInt())
        {
            1 -> return holder.star1
            2 -> return holder.star2
            3 -> return holder.star3
            4 -> return holder.star4
            5 -> return holder.star5
        }

        throw IllegalStateException("Rating is out of bounds")
    }

// ----------------------------------------->

    /**
     * ViewHolder for venue cell
     */
    class ViewHolder(val v: View) : RecyclerView.ViewHolder(v)
    {
        /**
         * Saved context
         */
        public val context: Context

        /**
         * ImageView of the category icon
         */
        public val categoryIcon: ImageView
        /**
         * Saved value of the currently load category icon to avoid reloading the same one
         */
        public var categoryIconLoadedUrl: String? = null

        /**
         * TextView to display the venue name
         */
        public val nameTextView: TextView

        /**
         * TextView to display status of loading of the rating for a venue (in case of unavailable or error)
         */
        public val ratingLoadingTextView:TextView
        /**
         * ProgressBar to show that a rating is being fetched for a venue
         */
        public val ratingLoadingProgressBar: ProgressBar
        /**
         * Layout that contains rating stars
         */
        public val ratingLayout:LinearLayout
        /**
         * First star ImageView
         */
        public val star1:ImageView
        /**
         * 2th star ImageView
         */
        public val star2:ImageView
        /**
         * 3th star ImageView
         */
        public val star3:ImageView
        /**
         * 4th star ImageView
         */
        public val star4:ImageView
        /**
         * 5th star ImageView
         */
        public val star5:ImageView
        /**
         * Saved value of the currently displayed rating to avoid redrawing the same one
         */
        public var displayedRating:Double? = null

        /**
         * TextView that display the distance of the venue if available
         */
        public val distanceTextView:TextView

        init
        {
            context = v.getContext()
            categoryIcon = v.findViewById(R.id.cell_venue_categorie_iv) as ImageView
            nameTextView = v.findViewById(R.id.cell_venue_name_tv) as TextView
            ratingLoadingTextView = v.findViewById(R.id.cell_venue_rating_loading_tv) as TextView
            ratingLoadingProgressBar = v.findViewById(R.id.cell_venue_rating_loading_pb) as ProgressBar
            ratingLayout = v.findViewById(R.id.cell_venue_rating_layout) as LinearLayout
            star1 = v.findViewById(R.id.cell_venue_star1) as ImageView
            star2 = v.findViewById(R.id.cell_venue_star2) as ImageView
            star3 = v.findViewById(R.id.cell_venue_star3) as ImageView
            star4 = v.findViewById(R.id.cell_venue_star4) as ImageView
            star5 = v.findViewById(R.id.cell_venue_star5) as ImageView
            distanceTextView = v.findViewById(R.id.cell_venue_distance_tv) as TextView
        }
    }
}