package pl.rciurkot.indoor.location

import java.util.ArrayList
import java.util.HashSet
import java.util.HashMap
import timber.log.Timber

/**
 * Created by rafalciurkot on 22.12.14.
 */
public class Space {
    private val coords = HashMap<String, LocationAware>()

    /**
     * returns copy of current coordinates - any changes made to returned object WON'T affect current state of Space
     */
    public val coordinates: Set<LocationAware>
        get(){
            val copy = HashSet<LocationAware>()
            coords forEach { copy add it.value.copy() }
            return copy
        }

    public fun registerCoord(coord: Coords): Space {
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
}