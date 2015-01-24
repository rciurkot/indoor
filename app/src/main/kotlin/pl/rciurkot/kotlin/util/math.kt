package pl.rciurkot.kotlin.util

/**
 * Created by rafalciurkot on 02.01.15.
 */
public fun Number.power(exp: Number): Double = Math.pow(toDouble(), exp.toDouble())

fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)

fun <N : Number> max(lhs: N, rhs: N) = if (lhs.toDouble() > rhs.toDouble()) lhs else rhs
fun <N : Number>  min(lhs: N, rhs: N) = if (lhs.toDouble() < rhs.toDouble()) lhs else rhs

fun abs(a: Double) = if (a >= 0) a else -1 * a