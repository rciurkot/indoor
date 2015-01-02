package pl.rciurkot.indoor.positioning

import pl.rciurkot.indoor.location.Space
import pl.rciurkot.indoor.location.Coord
import timber.log.Timber
import java.util.ArrayList
import pl.rciurkot.indoor.location.LocationAware
import pl.rciurkot.kotlin.util.power
import org.apache.commons.math3.linear.MatrixUtils
import org.apache.commons.math3.linear.RealVector
import org.apache.commons.math3.linear.BlockRealMatrix
import org.apache.commons.math3.linear.QRDecomposition
import org.apache.commons.math3.linear.SingularValueDecomposition

/**
 * Created by rafalciurkot on 22.12.14.
 * Trilateriation based on solution described here: http://inside.mines.edu/~whereman/talks/TurgutOzal-11-Trilateration.pdf
 */
public class HeremanTrilateration : PositionResolver {

    override fun calculatePositionIn(space: Space): Coord {
        val coordinates = space.coordinatesWithDistance

        if (coordinates.size() > 2) {
            val sorted = coordinates sortBy { it -> it.hashCode() }
            val referencePoint = sorted.first()
            val subList = sorted.subList(1, sorted.size())

            val aMatrix = createAMatrix(referencePoint, subList)
            val bVector = createBVector(referencePoint, subList)

            val solver = QRDecomposition(aMatrix).getSolver()
//            val solver = SingularValueDecomposition(aMatrix).getSolver()
            val solution = solver.solve(bVector)
            val result = solution.add(MatrixUtils.createRealVector(doubleArray(referencePoint.coords.x, referencePoint.coords.y, referencePoint.coords.z)))

            return Coord("TR_${space.hashCode()}", result getEntry 0, result getEntry 1, result getEntry 2)
        } else {
            return Coord("TR_${space.hashCode()}", 0.0, 0.0, 0.0)
        }
    }


    private fun createAMatrix(referencePoint: LocationAware, coordinatesWithoutRefPoint: List<LocationAware>): BlockRealMatrix {
        val aMatrix = BlockRealMatrix(coordinatesWithoutRefPoint.size(), 3) //3 from "x,y,z"

        val xArray = DoubleArray(coordinatesWithoutRefPoint.size())
        val yArray = DoubleArray(coordinatesWithoutRefPoint.size())
        val zArray = DoubleArray(coordinatesWithoutRefPoint.size())

        for (i in coordinatesWithoutRefPoint.indices) {
            xArray[i] = coordinatesWithoutRefPoint[i].coords.x - referencePoint.coords.x
            yArray[i] = coordinatesWithoutRefPoint[i].coords.y - referencePoint.coords.y
            zArray[i] = coordinatesWithoutRefPoint[i].coords.z - referencePoint.coords.z
        }

        aMatrix.setColumn(0, xArray)
        aMatrix.setColumn(1, yArray)
        aMatrix.setColumn(2, zArray)

        return aMatrix
    }

    private fun createBVector(referencePoint: LocationAware, coordinatesWithoutRefPoint: List<LocationAware>): RealVector {
        //        val bVector = Array(sortedCoordinatesWithoutReferencePoint.size(), { i -> b(referencePoint, sortedCoordinatesWithoutReferencePoint.elementAt(i)) })
        val bArray = DoubleArray(coordinatesWithoutRefPoint.size())
        for (i in coordinatesWithoutRefPoint.indices) {
            bArray[i] = b(coordinatesWithoutRefPoint.elementAt(i), referencePoint)
        }
        return MatrixUtils.createRealVector(bArray)
    }

    private fun b(first: LocationAware, second: LocationAware): Double = 0.5 * ((second.dist!! power 2) - (first.dist!! power 2) + squareOfDistance(first, second))

    private fun squareOfDistance(first: LocationAware, second: LocationAware): Double = (first.coords.x - second.coords.x).power(2) + (first.coords.y - second.coords.y).power(2) + (first.coords.z - second.coords.z).power(2)
}