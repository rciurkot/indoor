package pl.rciurkot.indoor.location

/**
 * Created by rafalciurkot on 22.12.14.
 */
public data class LocationAware(
        val coords: Coord, var dist: Double? = null
) {
    override fun hashCode(): Int = coords.hashCode()
}

public data class Coord(
        val id: String,
        val x: Double,
        val y: Double,
        val z: Double = 0.0
) {
    override fun hashCode(): Int = id.hashCode()
}

public fun coord(x: Number, y: Number): Coord = Coord("", x.toDouble(), y.toDouble())