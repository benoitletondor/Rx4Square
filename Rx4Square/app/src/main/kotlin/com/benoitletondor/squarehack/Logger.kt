package com.benoitletondor.squarehack

import android.util.Log

/**
 * Helper to easily log into the application.
 *
 * @author Benoit LETONDOR
 */
public object Logger
{
    /**
     * Default logger tag.
     */
    public val DEFAULT_TAG: String = "4Square"

    /**
     * Is the logger in dev mode
     */
    private val dev = true

// ----------------------------------------->

    /**
     * Return the default tag depending on the debug variable
	 *
     * @param debug
     * @return
     */
    private fun getDefaultTag(debug: Boolean): String
    {
        if (debug)
        {
            return DEFAULT_TAG + "-debug"
        }

        return DEFAULT_TAG
    }

// ----------------------------------------->

    /**
     * Error log with debug, tag, message and throwable.
	 *
     * @param tag    Tag to show.
     * @param debug    Is this a debug log (= for the lib) or a log that should be displayed to the dev
     * @param msg    Message of the error.
     * @param t        Throwable exception.
     */
    public fun error(tag: String, debug: Boolean, msg: String, t: Throwable?)
    {
        if (!debug || dev)
        {
            Log.e(tag, msg, t)
        }
    }

    /**
     * Error log with tag, message and throwable.
     *
     * @param tag    Tag to show.
     * @param msg    Message of the error.
     * @param t        Throwable exception.
     */
    public fun error(tag: String, msg: String, t: Throwable? = null)
    {
        error(tag, true, msg, t)
    }

    /**
     * Error log with message and throwable. Uses the default tag.
     *
     * @param msg    Message of the error.
     * @param t        Throwable exception.
     */
    public fun error(msg: String, t: Throwable)
    {
        error(getDefaultTag(true), msg, t)
    }

    /**
     * Error log with debug, message and throwable. Uses the default tag.
     *
     * @param debug    Is this a debug log (= for the lib) or a log that should be displayed to the dev
     * @param msg    Message of the error.
     * @param t        Throwable exception.
     */
    public fun error(debug: Boolean, msg: String, t: Throwable)
    {
        error(getDefaultTag(debug), debug, msg, t)
    }

    /**
     * Error log with message. Uses the default tag.
     *
     * @param msg    Message of the error.
     */
    public fun error(msg: String)
    {
        error(getDefaultTag(true), msg, null)
    }

    /**
     * Error log with message. Uses the default tag.
     *
     * @param debug    Is this a debug log (= for the lib) or a log that should be displayed to the dev
     * @param msg    Message of the error.
     */
    public fun error(debug: Boolean, msg: String)
    {
        error(getDefaultTag(debug), debug, msg, null)
    }

// ------------------------------------------>

    /**
     * Warning log with tag, debug, message and throwable.
     *
     * @param tag    Tag to show.
     * @param debug    Is this a debug log (= for the lib) or a log that should be displayed to the dev
     * @param msg    Message of the warning.
     * @param t        Throwable exception.
     */
    public fun warning(tag: String, debug: Boolean, msg: String, t: Throwable?)
    {
        if (!debug || dev)
        {
            Log.w(tag, msg, t)
        }
    }

    /**
     * Warning log with tag, message and throwable.
     *
     * @param tag    Tag to show.
     * @param msg    Message of the warning.
     * @param t        Throwable exception.
     */
    public fun warning(tag: String, msg: String, t: Throwable? = null)
    {
        warning(tag, true, msg, t)
    }

    /**
     * Warning log with message and throwable. Uses the default tag.
     *
     * @param msg    Message of the warning.
     * @param t        Throwable exception.
     */
    public fun warning(msg: String, t: Throwable)
    {
        warning(getDefaultTag(true), msg, t)
    }

    /**
     * Warning log with debug, message and throwable. Uses the default tag.
     *
     * @param debug    Is this a debug log (= for the lib) or a log that should be displayed to the dev
     * @param msg    Message of the warning.
     * @param t        Throwable exception.
     */
    public fun warning(debug: Boolean, msg: String, t: Throwable)
    {
        warning(getDefaultTag(debug), debug, msg, t)
    }

    /**
     * Warning log with debug & message. Uses the default tag.
     *
     * @param debug    Is this a debug log (= for the lib) or a log that should be displayed to the dev
     * @param msg    Message of the warning.
     */
    public fun warning(debug: Boolean, msg: String)
    {
        warning(getDefaultTag(debug), debug, msg, null)
    }

    /**
     * Warning log with message. Uses the default tag.
     *
     * @param msg    Message of the warning.
     */
    public fun warning(msg: String)
    {
        warning(getDefaultTag(true), msg, null)
    }

// ------------------------------------------>

    /**
     * Debug log with tag, debug, message and throwable.
     *
     * @param tag    Tag to show.
     * @param debug    Is this a debug log (= for the lib) or a log that should be displayed to the dev
     * @param msg    Message of debug.
     * @param t        Throwable exception.
     */
    public fun debug(tag: String, debug: Boolean, msg: String, t: Throwable?)
    {
        if (!debug || dev)
        {
            Log.d(tag, msg, t)
        }
    }

    /**
     * Debug log with tag, message and throwable.
     *
     * @param tag    Tag to show.
     * @param msg    Message of debug.
     * @param t        Throwable exception.
     */
    public fun debug(tag: String, msg: String, t: Throwable? = null)
    {
        debug(tag, true, msg, t)
    }

    /**
     * Debug log with debug, message and throwable. Uses the default tag.
     *
     * @param debug    Is this a debug log (= for the lib) or a log that should be displayed to the dev
     * @param msg    Message of debug.
     * @param t        Throwable exception.
     */
    public fun debug(debug: Boolean, msg: String, t: Throwable)
    {
        debug(getDefaultTag(debug), debug, msg, t)
    }

    /**
     * Debug log with message and throwable. Uses the default tag.
     *
     * @param msg    Message of debug.
     * @param t        Throwable exception.
     */
    public fun debug(msg: String, t: Throwable)
    {
        debug(getDefaultTag(true), msg, t)
    }

    /**
     * Debug log with debug & message. Uses the default tag.
     *
     * @param debug    Is this a debug log (= for the lib) or a log that should be displayed to the dev
     * @param msg    Message of debug.
     */
    public fun debug(debug: Boolean, msg: String)
    {
        debug(getDefaultTag(debug), debug, msg, null)
    }

    /**
     * Debug log with message. Uses the default tag.
     *
     * @param msg    Message of debug.
     */
    public fun debug(msg: String)
    {
        debug(getDefaultTag(true), msg, null)
    }

// ------------------------------------------>

    /**
     * Information log with tag, message and throwable.
     *
     * @param tag    Tag to show.
     * @param debug    Is this a debug log (= for the lib) or a log that should be displayed to the dev
     * @param msg    Message of the information.
     * @param t        Throwable exception.
     */
    public fun info(tag: String, debug: Boolean, msg: String, t: Throwable?)
    {
        if (!debug || dev)
        {
            Log.i(tag, msg, t)
        }
    }

    /**
     * Information log with tag, message and throwable.
     *
     * @param tag    Tag to show.
     * @param msg    Message of the information.
     * @param t        Throwable exception.
     */
    public fun info(tag: String, msg: String, t: Throwable? = null)
    {
        info(tag, true, msg, t)
    }

    /**
     * Information log with debug, message and throwable. Uses the default tag.
     *
     * @param debug    Is this a debug log (= for the lib) or a log that should be displayed to the dev
     * @param msg    Message of the information.
     * @param t        Throwable exception.
     */
    public fun info(debug: Boolean, msg: String, t: Throwable)
    {
        info(getDefaultTag(debug), debug, msg, t)
    }

    /**
     * Information log with tag, message and throwable. Uses the default tag.
     *
     * @param msg    Message of the information.
     * @param t        Throwable exception.
     */
    public fun info(msg: String, t: Throwable)
    {
        info(getDefaultTag(true), msg, t)
    }

    /**
     * Information log with debug & message. Uses the default tag.
     *
     * @param debug    Is this a debug log (= for the lib) or a log that should be displayed to the dev
     * @param msg    Message of the information.
     */
    public fun info(debug: Boolean, msg: String)
    {
        info(getDefaultTag(debug), debug, msg, null)
    }

    /**
     * Information log with message. Uses the default tag.
     *
     * @param msg    Message of the information.
     */
    public fun info(msg: String)
    {
        info(getDefaultTag(true), msg, null)
    }

// ------------------------------------------>

    /**
     * Verbose log with tag, debug, message and throwable.
     *
     * @param tag    Tag to show.
     * @param debug    Is this a debug log (= for the lib) or a log that should be displayed to the dev
     * @param msg    Message.
     * @param t        Throwable exception.
     */
    public fun verbose(tag: String, debug: Boolean, msg: String, t: Throwable?)
    {
        if (!debug || dev)
        {
            Log.v(tag, msg, t)
        }
    }

    /**
     * Verbose log with tag, message and throwable.
     *
     * @param tag    Tag to show.
     * @param msg    Message.
     * @param t        Throwable exception.
     */
    public fun verbose(tag: String, msg: String, t: Throwable? = null)
    {
        verbose(tag, true, msg, t)
    }

    /**
     * Verbose log with debug, message and throwable. Uses the default tag.
     *
     * @param debug    Is this a debug log (= for the lib) or a log that should be displayed to the dev
     * @param msg    Message.
     * @param t        Throwable exception.
     */
    public fun verbose(debug: Boolean, msg: String, t: Throwable)
    {
        verbose(getDefaultTag(debug), debug, msg, t)
    }

    /**
     * Verbose log with message and throwable. Uses the default tag.
     *
     * @param msg    Message.
     * @param t        Throwable exception.
     */
    public fun verbose(msg: String, t: Throwable)
    {
        verbose(getDefaultTag(true), msg, t)
    }

    /**
     * Verbose log with debug & message. Uses the default tag.
     *
     * @param debug    Is this a debug log (= for the lib) or a log that should be displayed to the dev
     * @param msg    Message.
     */
    public fun verbose(debug: Boolean, msg: String)
    {
        verbose(getDefaultTag(debug), debug, msg, null)
    }

    /**
     * Verbose log with message. Uses the default tag.
     *
     * @param msg    Message.
     */
    public fun verbose(msg: String)
    {
        verbose(getDefaultTag(true), msg, null)
    }
}
