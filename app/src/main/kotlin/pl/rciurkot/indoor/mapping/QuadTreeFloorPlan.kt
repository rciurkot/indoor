package pl.rciurkot.indoor.mapping

import com.jwetherell.algorithms.data_structures.QuadTree
import com.jwetherell.algorithms.data_structures.QuadTree.XYPoint
import com.jwetherell.algorithms.data_structures.QuadTree.MxCifQuadTree
import pl.rciurkot.indoor.location.Coord
import java.util.ArrayList
import pl.rciurkot.indoor.location.coord
import pl.rciurkot.kotlin.util.max
import pl.rciurkot.kotlin.util.min

/**
 * Created by rafalciurkot on 14.01.15.
 */
public class QuadTreeFloorPlan private (upperLeft: Coord, bottomRight: Coord) : FloorPlan {
    val tree = RoomQuadTree(upperLeft, bottomRight)

    private class object {
        val QUERY_DIAMETER: Float = 0.5f
    }

    override fun roomAt(coord: Coord): BuildingComponent {
        val x = (coord.x - QUERY_DIAMETER / 2).toFloat()
        val y = (coord.y - QUERY_DIAMETER / 2).toFloat()

        val entries = tree.queryRange(x, y, QUERY_DIAMETER, QUERY_DIAMETER)
        if (entries.size() < 1) {
            throw RuntimeException("no rooms found at $coord")
        } else if (entries.size() > 1) {
            //TODO: pick best room
        }
        return entries[0].buildingComponent
    }

    public class Builder : FloorPlan.Builder {
        val rooms = ArrayList<RoomAABB>()
        var upperLeftMostCorner = coord(java.lang.Double.MAX_VALUE, java.lang.Double.MAX_VALUE)
        var bottomRightMostCorner = coord(-java.lang.Double.MAX_VALUE, -java.lang.Double.MAX_VALUE)

        override fun internalAdd(upperLeft: Coord, bottomRight: Coord, room: BuildingComponent) {
            rooms add RoomAABB(upperLeft, bottomRight, room)
            upperLeftMostCorner = coord(min(upperLeftMostCorner.x, upperLeft.x), min(upperLeftMostCorner.y, upperLeft.y))
            bottomRightMostCorner = coord(max(bottomRightMostCorner.x, upperLeft.x), max(bottomRightMostCorner.y, upperLeft.y))
        }

        override fun build(): FloorPlan {
            val floorPlan = QuadTreeFloorPlan(upperLeftMostCorner, bottomRightMostCorner)
            rooms forEach { floorPlan.tree insert it }
            return floorPlan
        }
    }
}


class RoomAABB(upperLeft: Coord, bottomRight: Coord, val buildingComponent: BuildingComponent) :
        QuadTree.AxisAlignedBoundingBox(XYPoint(upperLeft.x.toFloat(), upperLeft.y.toFloat()), (bottomRight.x - upperLeft.x).toFloat(), (bottomRight.y - upperLeft.y).toFloat())

class RoomQuadTree(upperLeft: Coord, bottomRight: Coord) :
        MxCifQuadTree<RoomAABB>(upperLeft.x.toFloat(), upperLeft.y.toFloat(), (bottomRight.x - upperLeft.x).toFloat(), (bottomRight.y - upperLeft.y).toFloat()) {

    fun insert(roomAabb: RoomAABB): Boolean = (getRoot() as MxCifQuadTree.MxCifQuadNode).insert(roomAabb)
}