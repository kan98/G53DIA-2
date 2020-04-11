package uk.ac.nott.cs.g53dia.multiagent;

import uk.ac.nott.cs.g53dia.multilibrary.Cell;
import uk.ac.nott.cs.g53dia.multilibrary.Point;

import java.util.List;

/**
 * This class contains helper methods that can be used from throughout the agent package.
 *
 */
public class Helpers {
    private int minDistance = 0;

    /**
     * Finds the closest cell from a list of cells based on a certain Point.
     *
     * @param cellLocation Current point to compare cells to.
     * @param cells List of cells to consider to find the closest to the Point.
     * @return The cell that is closest to the Point.
     *
     */
    protected Cell findClosest (Point cellLocation, List<Cell> cells) {
        int minDistance = cellLocation.distanceTo(cells.get(0).getPoint());
        Cell closestPoint = cells.get(0);

        if (!cells.isEmpty()) {
            for (Cell cell: cells) {
                int currentDistance = cellLocation.distanceTo(cell.getPoint());
                if (currentDistance < minDistance) {
                    minDistance = currentDistance;
                    closestPoint = cell;
                }
            }
            this.minDistance = minDistance;
            return closestPoint;
        } else {
            return null;
        }
    }

    protected int getMinDistance() {
        return minDistance;
    }
}
