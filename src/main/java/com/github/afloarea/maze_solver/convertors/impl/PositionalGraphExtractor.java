package com.github.afloarea.maze_solver.convertors.impl;

import com.github.afloarea.maze_solver.graph.PositionalGraph;
import com.github.afloarea.maze_solver.algorithms.GraphNode;
import com.github.afloarea.maze_solver.graph.PositionalGraphNode;
import com.github.afloarea.maze_solver.convertors.MazeToGraphConverter;
import com.github.afloarea.maze_solver.maze.Maze;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Logger;

public final class PositionalGraphExtractor implements MazeToGraphConverter {
    private static final Logger LOGGER = Logger.getLogger(PositionalGraphExtractor.class.getName());

    @Override
    public PositionalGraph convert(Maze maze) {
        final PositionalGraphNode[][] table = new PositionalGraphNode[maze.height()][maze.width()];

        final Deque<PositionalGraphNode> graph = new ArrayDeque<>();

        for (int row = 0; row < maze.height(); row++) {
            for (int column = 0; column < maze.width(); column++) {
                final PositionalGraphNode node = processAtPosition(maze, table, row, column);
                if (node != null) {
                    graph.add(node);
                }
            }
        }

        LOGGER.info(() -> String.format("Extracted %d graph nodes", graph.size()));
        return new PositionalGraph(graph.getFirst(), graph.getLast());

    }

    private PositionalGraphNode processAtPosition(Maze maze, PositionalGraphNode[][] table, int row, int column) {
        if (maze.isBlockedAt(row, column)) {
            return null;
        }

        if (isTunnelOrSurrounded(maze, row, column)) {
            return null;
        }

        final PositionalGraphNode node = new PositionalGraphNode(row, column);
        table[row][column] = node;

        lookForNeighbourToTheLeft(maze, table, row, column);
        lookForNeighbourUp(maze, table, row, column);

        return node;
    }

    private void lookForNeighbourUp(Maze maze, PositionalGraphNode[][] table, int row, int column) {
        final PositionalGraphNode node = table[row][column];
        int distance = 0;
        int currentRow = row;
        while (maze.isFreeAt(--currentRow, column)) {
            distance++;
            if (table[currentRow][column] != null) {
                final PositionalGraphNode neighbour = table[currentRow][column];
                GraphNode.createNeighbours(node, neighbour, distance);
                break;
            }
        }
    }

    private void lookForNeighbourToTheLeft(Maze maze, PositionalGraphNode[][] graphTable, int row, int column) {
        final PositionalGraphNode node = graphTable[row][column];
        int distance = 0;
        int currentColumn = column;
        while (maze.isFreeAt(row, --currentColumn)) {
            distance++;
            if (graphTable[row][currentColumn] != null) {
                final PositionalGraphNode neighbour = graphTable[row][currentColumn];
                GraphNode.createNeighbours(node, neighbour, distance);
                break;
            }
        }
    }

    private boolean isTunnelOrSurrounded(Maze maze, int row, int column) {
        boolean isTunnel = checkTunnel(maze, row, column);
        if(isTunnel) return true;

        boolean surrounded = true;
        for (int currentRow = row - 1; currentRow <= row + 1; currentRow++) {
            for (int currentColumn = column - 1; currentColumn <= column + 1; currentColumn++) {
                if (maze.isBlockedAt(currentRow, currentColumn)) {
                    surrounded = false;
                    break;
                }
            }
            if (!surrounded) {
                break;
            }
        }

        return surrounded;
    }

    private static boolean checkTunnel(Maze maze, int row, int column) {
        return maze.isBlockedAt(row - 1, column) && maze.isBlockedAt(row + 1, column)
                && maze.isFreeAt(row - 1, column) && maze.isFreeAt(row, column + 1)

                || maze.isFreeAt(row - 1, column) && maze.isFreeAt(row + 1, column)
                && maze.isBlockedAt(row, column - 1) && maze.isBlockedAt(row, column + 1);
    }
}
