package pl.rciurkot.indoor.mapping

import com.jwetherell.algorithms.data_structures.QuadTree
import com.jwetherell.algorithms.data_structures.QuadTree.XYPoint
import com.jwetherell.algorithms.data_structures.QuadTree.MxCifQuadTree
import pl.rciurkot.indoor.location.Coord
import java.util.ArrayList
import pl.rciurkot.indoor.location.coord
import pl.rciurkot.kotlin.util.max
import pl.rciurkot.kotlin.util.min
import pl.rciurkot.kotlin.util.abs
import timber.log.Timber

/**
 * Created by rafalciurkot on 14.01.15.
 */
public class QuadTreeFloorPlan private (upperLeft: Coord, bottomRight: Coord) : FloorPlan {
    override val outside = object : Element {
        override val buildingComponent: BuildingComponent = Outside()
        override val area: Area = RectangleArea(coord(-java.lang.Double.MAX_VALUE, -java.lang.Double.MAX_VALUE), coord(java.lang.Double.MAX_VALUE, java.lang.Double.MAX_VALUE))
    }
    val tree = RoomQuadTree(upperLeft, bottomRight)

    private class object {
        val QUERY_DIAMETER: Float = 0.5f
        val RECURSION_RATIO: Float = 0.55f
    }

    override fun roomAt(coord: Coord): Element {
        return roomAt(coord, QUERY_DIAMETER)
    }

    private fun roomAt(coord: Coord, diameter: Float): Element {
        val entries = neighbours(coord, diameter.toDouble())

        if (entries.size() < 1) {
            return outside
        } else if (entries.size() > 1) {
            return roomAt(coord, diameter * RECURSION_RATIO)
        } else {
            return entries[0]
        }
    }

    override fun neighbours(coord: Coord, radius: Double): List<Element> {
        val x = (coord.x - radius).toFloat()
        val y = (coord.y - radius).toFloat()
        val r2 = radius.toFloat() * 2f

        Timber.e("neighbours: $x $y $r2")

        return tree.queryRange(x, y, r2, r2)
    }

    /**
     * taxicab metric
     */
    override fun dist(coord: Coord, elem: Element): Double {
        val area = elem.area
        if (area is RectangleArea) {
            return min(abs(area.bottomRight.x - coord.x), abs(area.upperLeft.x - coord.x)) + min(abs(area.bottomRight.y - coord.y), abs(area.upperLeft.y - coord.y))
        } else {
            throw UnsupportedOperationException()
        }
    }
    //
    //    override fun dist(elem1: Element, elem2: Element): Double {
    //        val area1 = elem1.area
    //        val area2 = elem2.area
    //        if(area1 is RectangleArea && area2 is RectangleArea){
    //            val left = if(area1.upperLeft.x <= area2.upperLeft.x) area1 else area2
    //            val right = if(area1.upperLeft.x <= area2.upperLeft.x) area2 else area1
    //
    //            val horizontalDist = right.upperLeft.x - left.bottomRight.x
    //        }
    //    }


    public class Builder : FloorPlan.Builder {
        val rooms = ArrayList<RoomAABB>()
        var upperLeftMostCorner = coord(java.lang.Double.MAX_VALUE, java.lang.Double.MAX_VALUE)
        var bottomRightMostCorner = coord(-java.lang.Double.MAX_VALUE, -java.lang.Double.MAX_VALUE)

        override fun add(upperLeft: Coord, bottomRight: Coord, room: BuildingComponent): Builder {
            rooms add RoomAABB(upperLeft, bottomRight, room)
            upperLeftMostCorner = coord(min(upperLeftMostCorner.x, upperLeft.x), min(upperLeftMostCorner.y, upperLeft.y))
            bottomRightMostCorner = coord(max(bottomRightMostCorner.x, bottomRight.x), max(bottomRightMostCorner.y, bottomRight.y))
            return this
        }

        override fun build(): FloorPlan {
            val floorPlan = QuadTreeFloorPlan(upperLeftMostCorner, bottomRightMostCorner)
            rooms forEach { floorPlan.tree insert it }
            return floorPlan
        }
    }
}


class RoomAABB(upperLeft: Coord, bottomRight: Coord, override val buildingComponent: BuildingComponent) :
        QuadTree.AxisAlignedBoundingBox(XYPoint(upperLeft.x.toFloat(), upperLeft.y.toFloat()), (bottomRight.x - upperLeft.x).toFloat(), (bottomRight.y - upperLeft.y).toFloat()), Element {
    override val area = RectangleArea(upperLeft, bottomRight)

    override fun toString(): String = "${javaClass.getSimpleName()}(name=${buildingComponent.name}, ul=${area.upperLeft}, br=${area.bottomRight})"
}

class RoomQuadTree(upperLeft: Coord, bottomRight: Coord) :
        MxCifQuadTree<RoomAABB>(upperLeft.x.toFloat(), upperLeft.y.toFloat(), (bottomRight.x - upperLeft.x).toFloat(), (bottomRight.y - upperLeft.y).toFloat()) {

    fun insert(roomAabb: RoomAABB): Boolean = (getRoot() as MxCifQuadTree.MxCifQuadNode).insert(roomAabb)
}