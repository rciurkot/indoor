package pl.rciurkot.indoor.positioning

import pl.rciurkot.indoor.location.Space
import pl.rciurkot.indoor.location.Coord

/**
 * Created by rafalciurkot on 22.12.14.
 */
public trait PositionResolver {
    public fun calculatePositionIn(space: Space) : Coord
}