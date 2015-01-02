package pl.rciurkot.kotlin.util

/**
 * Created by rafalciurkot on 22.12.14.
 */
public val Collection<*>?.notempty: Boolean
    get() = this?.isNotEmpty() ?: false