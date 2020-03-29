package uk.ac.nott.cs.g53dia.multiagent;

import uk.ac.nott.cs.g53dia.multilibrary.Cell;
import uk.ac.nott.cs.g53dia.multilibrary.Point;

import java.util.List;

public class Helpers {
    int minDistance = 0;

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

    public int getMinDistance() {
        return minDistance;
    }
}
