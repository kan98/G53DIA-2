package uk.ac.nott.cs.g53dia.multiagent;

import uk.ac.nott.cs.g53dia.multilibrary.*;

import java.util.ArrayList;
import java.util.List;

public class SharedKnowledge {

    private List<Cell> rechargePoints = new ArrayList<>();
    private List<LitterBin> plannedBins = new ArrayList<>();

    protected void addNewCells(Cell[][] view) {
        for (int i=0; i != view.length; i++) {
            for (int j=0; j != view[i].length; j++) {
                Cell currentView = view[i][j];
                if(currentView instanceof RechargePoint && !rechargePoints.contains(currentView)) {
                    rechargePoints.add(currentView);
                }
            }
        }
    }

    protected List<Cell> getRechargePoints() {
        return rechargePoints;
    }

    protected void addPlannedBin(LitterBin bin) {
        plannedBins.add(bin);
    }

    protected void removePlannedBin(LitterBin bin) {
        plannedBins.remove(bin);
    }

    protected List<LitterBin> getPlannedBins() {
        return plannedBins;
    }
}
