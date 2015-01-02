package pl.rciurkot.indoor.location

import java.util.ArrayList
import java.util.HashSet
import java.util.HashMap
import timber.log.Timber
import pl.rciurkot.kotlin.util.format

/**
 * Created by rafalciurkot on 22.12.14.
 */
public class Space {
    private val coords = HashMap<String, LocationAware>()

    /**
     * returns copy of current coordinates - any changes made to returned object WON'T affect current state of Space
     */
    public val coordinates: Set<LocationAware>
        get() {
            val set = HashSet<LocationAware>()
            coords forEach { set add it.value.copy() }
            return set
        }

    /**
     * returns copy of current coordinates that have the distance set - any changes made to returned object WON'T affect current state of Space
     */
    public val coordinatesWithDistance: Set<LocationAware>
        get() {
            val set = HashSet<LocationAware>()
            coords forEach {
                if (it.value.dist != null) {
                    set add it.value.copy()
                }
            }
            return set
        }

    public fun registerCoord(coord: Coord): Space {
        if (coords containsKey coord.id) {
            throw RuntimeException("Coordinates with id $coord.id already registered")
        }
        coords.put(coord.id, LocationAware(coord))
        return this
    }

    public fun updateDist(id: String, dist: Double) {
        val oldDist = coords get id
        if (oldDist == null) {
            Timber.w("Cannot update distance of $id to $dist - no such coord registered")
        } else {
            oldDist.dist = dist
            Timber.d("Distance of $id updated to $dist")
        }
    }

    public fun getDist(id: String): Double {
        val dist = (coords get id).dist
        if (dist == null)
            throw RuntimeException("Unknown location id: $id")
        return dist
    }

    override fun toString(): String {
        val sb = StringBuilder()
        coords forEach {
            sb appendln "(${it.value.coords.x}, ${it.value.coords.y})=${it.value.dist?.format(3)}"
        }

        return sb.toString()
    }
}