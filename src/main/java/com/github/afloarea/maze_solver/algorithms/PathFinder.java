package com.github.afloarea.maze_solver.algorithms;

import java.util.Queue;

/**
 * General algorithm interface for finding the shortest path in a graph.
 */
public interface PathFinder {

    /**
     * Find the shortest path in a graph.
     *
     * @param startNode the starting node of the resulting path
     * @param endNode   the final node in the resulting path
     * @return the shortest path
     */
    <T extends GraphNode<T>> Queue<T> findShortestPath(T startNode, T endNode, PathSearchStrategy searchStrategy);

}