package dev.phlogiston.phrecycler

fun <T> List<T>.split(position: Int) = Pair(take(position), drop(position))