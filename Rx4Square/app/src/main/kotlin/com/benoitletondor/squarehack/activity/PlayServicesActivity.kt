package com.benoitletondor.squarehack.activity

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Api
import java.util.*

/**
 * Activity that is connected to PlayServices
 *
 * @author Benoit LETONDOR
 */
public abstract class PlayServicesActivity(val apis:Array<Api<out Api.ApiOptions.NotRequiredOptions>>) : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    /**
     * Request code to use when launching the resolution activity
     */
    private val REQUEST_RESOLVE_ERROR:Int = 1001;
    /**
     * Unique tag for the error dialog fragment
     */
    private val DIALOG_ERROR:String = "dialog_error";
    /**
     * Unique tag for the bundle state when resolving an error
     */
    private val STATE_RESOLVING_ERROR:String = "resolving_error";
    /**
     * Bool to track whether the app is already resolving an error
     */
    private var isResolvingError:Boolean = false;

    /**
     * PlayServices client
     */
    private var googleApiClient: GoogleApiClient? = null

// ------------------------------------------->

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super<AppCompatActivity>.onCreate(savedInstanceState)

        isResolvingError = savedInstanceState != null && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);

        /*
         * Play Services init
         */
        val googleApiBuilder:GoogleApiClient.Builder = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)

        for(api in apis)
        {
            googleApiBuilder.addApi(api)
        }

        googleApiClient = googleApiBuilder.build()
    }

    override fun onStart() {
        super<AppCompatActivity>.onStart()

        if ( !isResolvingError )
        {
            googleApiClient?.connect()
        }
    }

    override fun onStop() {
        onPlayServicesNotAvailable()
        googleApiClient?.disconnect()

        super<AppCompatActivity>.onStop()
    }

    override fun onDestroy()
    {
        googleApiClient = null

        super<AppCompatActivity>.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super<AppCompatActivity>.onSaveInstanceState(outState, outPersistentState)

        outState!!.putBoolean(STATE_RESOLVING_ERROR, isResolvingError)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super<AppCompatActivity>.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_RESOLVE_ERROR)
        {
            isResolvingError = false;
            if (resultCode == Activity.RESULT_OK)
            {
                // Make sure the app is not already connected or attempting to connect
                if (!googleApiClient!!.isConnecting() && !googleApiClient!!.isConnected())
                {
                    googleApiClient!!.connect()
                }
            }
        }
    }

// -------------------------------------------->

    /**
     * Will be called when play services are available for use
     *
     * @param googleApiClient Google API client ready to be used
     */
    abstract fun onPlayServicesAvailable(googleApiClient: GoogleApiClient)

    /**
     * Will be called when play services are not available.<br />
     * This can happen:
     * <ul>
     *     <li>If there's no PlayServices at all on the device</li>
     *     <li>If PlayServices connection is suspended</li>
     *     <li>If the activity onStop is reached</li>
     * </ul>
     * <br />
     * <b>No call to the API should be done after this method</b>
     */
    abstract fun onPlayServicesNotAvailable()

// -------------------------------------------->

    override fun onConnectionSuspended(cause: Int)
    {
        onPlayServicesNotAvailable()
    }

    override fun onConnected(connectionHint: Bundle?)
    {
        onPlayServicesAvailable(googleApiClient!!)
    }

    override fun onConnectionFailed(result: ConnectionResult?)
    {
        if ( isResolvingError )
        {
            // Already attempting to resolve an error.
            return;
        }
        else if ( result!!.hasResolution() )
        {
            try
            {
                isResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            }
            catch (e: IntentSender.SendIntentException)
            {
                // There was an error with the resolution intent. Try again.
                googleApiClient!!.connect();
            }
        }
        else
        {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            isResolvingError = true;

            onPlayServicesNotAvailable()
        }
    }

// -------------------------------------------->

    /* Creates a dialog for an error message */
    private fun showErrorDialog(errorCode:Int)
    {
        // Create a fragment for the error dialog
        val dialogFragment:ErrorDialogFragment = ErrorDialogFragment();

        // Pass the error that should be displayed
        val args:Bundle = Bundle();
        args.putInt(DIALOG_ERROR, errorCode);

        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    private fun onDialogDismissed()
    {
        isResolvingError = false;
    }

    /* A fragment to display an error dialog */
    public inner class ErrorDialogFragment : DialogFragment()
    {
        override fun onCreateDialog(savedInstanceState:Bundle?) : Dialog
        {
            // Get the error code and retrieve the appropriate dialog
            val errorCode:Int = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        override fun onDismiss(dialog: DialogInterface)
        {
            (getActivity() as PlayServicesActivity).onDialogDismissed();
        }
    }
}