package pl.rciurkot.indoor.location

/**
 * Created by rafalciurkot on 22.12.14.
 */
public data class LocationAware(val coords: Coords) {
    var dist: Double? = null

    override fun hashCode(): Int = coords.hashCode()
}

public data class Coords(
        val id: String,
        val x: Double,
        val y: Double,
        val z: Double = 0.0
) {
    override fun hashCode(): Int = id.hashCode()
}