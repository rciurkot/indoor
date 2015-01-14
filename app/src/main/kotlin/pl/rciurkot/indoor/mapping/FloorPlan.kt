package pl.rciurkot.indoor.mapping

import pl.rciurkot.indoor.location.Coord

/**
 * Created by rafalciurkot on 03.01.15.
 */
public trait FloorPlan {
    public fun roomAt(coord: Coord): BuildingComponent

    public trait Builder {
        public final fun plus(upperLeft: Coord, bottomRight: Coord, room: BuildingComponent): Builder {
            internalAdd(upperLeft, bottomRight, room)
            return this
        }

        protected fun internalAdd(upperLeft: Coord, bottomRight: Coord, room: BuildingComponent)

        protected fun build(): FloorPlan
    }
}

