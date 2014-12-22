package pl.rciurkot.indoor.positioning

import pl.rciurkot.indoor.location.Space
import pl.rciurkot.indoor.location.Coords
import timber.log.Timber

/**
 * Created by rafalciurkot on 22.12.14.
 */
public class Trilateration : PositionResolver {
    override fun calculatePositionIn(space: Space): Coords {
        space.coordinates forEach { Timber.d(it.toString()) }

        return Coords("TR_${space.hashCode()}", 0.0, 0.0, 0.0)
    }
}