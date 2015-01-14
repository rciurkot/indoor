package pl.rciurkot.indoor.mapping

import pl.rciurkot.indoor.location.Coord

/**
 * Created by rafalciurkot on 03.01.15.
 */
public trait FloorPlan {
    public val outside: Outside

    public fun roomAt(coord: Coord): BuildingComponent

    public trait Builder {
        public fun add(upperLeft: Coord, bottomRight: Coord, room: BuildingComponent): Builder

        public fun build(): FloorPlan
    }
}

