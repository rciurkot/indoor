package pl.rciurkot.indoor.mapping

import pl.rciurkot.indoor.location.Coord

/**
 * Created by rafalciurkot on 03.01.15.
 */
public trait FloorPlan {
    public val outside: Element

    public fun roomAt(coord: Coord): Element

    public fun neighbours(coord: Coord, radius: Double): List<Element>

    public fun dist(coord: Coord, elem: Element): Double

    public trait Builder {
        public fun add(upperLeft: Coord, bottomRight: Coord, room: BuildingComponent): Builder

        public fun build(): FloorPlan
    }


}

public trait Area
public class RectangleArea(val upperLeft: Coord, val bottomRight: Coord) : Area
public trait Element {
    val buildingComponent: BuildingComponent
    val area: Area
}
