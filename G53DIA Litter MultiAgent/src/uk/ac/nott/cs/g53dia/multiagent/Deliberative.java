package uk.ac.nott.cs.g53dia.multiagent;

import uk.ac.nott.cs.g53dia.multilibrary.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class handles the deliberative component of the agent.
 *
 * An object of the class is created in the MyLitterAgent to help plan actions for the agent.
 *
 */
public class Deliberative {

    private Helpers helpers = new Helpers();

    private SharedKnowledge sharedKnowledge;
    private float scoreRatio = -1000;
    private int remainingCapacity;
    private MyLitterAgent.binType binType = MyLitterAgent.binType.NONE;

    private List<Cell> currentCells = new ArrayList<>();
    private List<Cell> selectedRoute = new ArrayList<>();
    private Cell closestStation = null;

    private List<Cell> wasteStations = new ArrayList<>();
    private List<Cell> recyclingStations = new ArrayList<>();

    Map<Cell, Cell> binToStation = new HashMap<>();
    Map<Cell, Integer> binToStationDistance = new HashMap<>();


    public Deliberative(SharedKnowledge sharedKnowledge){
     this.sharedKnowledge = sharedKnowledge;
    }

    /**
     * Goes through a list of cells of either waste or recycling bins and return a list of bins which aren't empty.
     * Only adds the bin to the list if it's not in another agent's route.
     *
     * @param bins A list of Cell objects of either waste or recycling bins.
     * @return A list of Cell objects of either waste or recycling bins that have non-empty tasks in them.
     *
     */
    private List<Cell> getBins (List<Cell> bins) {
        List<Cell> fullBins = new ArrayList<>();

        for (Cell bin: bins) {
            LitterBin tempLitter = (LitterBin) bin;
            if (tempLitter.getTask() != null && tempLitter.getTask().getRemaining() > 0
                        && (!sharedKnowledge.getPlannedBins().contains(bin) || currentCells.contains(bin))) {
                    if (bin instanceof WasteBin) {
                        if (!wasteStations.isEmpty()) {
                            fullBins.add(bin);
                            binToStation.put(bin, helpers.findClosest(bin.getPoint(), wasteStations));
                        }
                    } else {
                        if (!recyclingStations.isEmpty()) {
                            fullBins.add(bin);
                            binToStation.put(bin, helpers.findClosest(bin.getPoint(), recyclingStations));
                        }
                    }
                    binToStationDistance.put(bin, helpers.getMinDistance());
            }
        }
        return fullBins;
    }

    /**
     * This method is called by the senseAndAct method from the MyLitterAgent class for each time step.
     * It gets all the bins that aren't empty and calls the getBestRoute method to plan the best route for the agent.
     *
     * The method will take the list of bins from the getBestRoute method and create actions for them to add to stateList.
     * The method will also add a litter drop off action to the closest respective station into the stateList queue.
     *
     */
    protected void planRoute(Point currentLocation, int remainingCapacity,
                             MyLitterAgent.binType binType, List<Cell> currentCells, List<Cell> recyclingBins,
                             List<Cell> wasteBins, List<Cell> wasteStations, List<Cell> recyclingStations) {
        this.remainingCapacity = remainingCapacity;
        this.binType = binType;
        this.currentCells = currentCells;
        this.wasteStations = wasteStations;
        this.recyclingStations = recyclingStations;

        if (!currentCells.isEmpty()) {
            currentCells.remove(currentCells.size() - 1);
        }

        List<Cell> availableWasteBins = getBins(wasteBins);
        List<Cell> availableRecyclingBins = getBins(recyclingBins);

        if (binType != MyLitterAgent.binType.RECYCLING) {
            getBestRoute(availableWasteBins, new ArrayList<>(), currentLocation, 0, 0, MyLitterAgent.binType.WASTE);
        }

        if (binType != MyLitterAgent.binType.WASTE) {
            getBestRoute(availableRecyclingBins, new ArrayList<>(), currentLocation, 0, 0, MyLitterAgent.binType.RECYCLING);
        }
    }

    /**
     * Recursive method that does a depth first search to output the best route for the agent.
     *
     * The output is a list of either recycling or waste bins that the search method can add actual agent states for.
     * Output is stored in a global variable and not returned by the function.
     *
     */
    private void getBestRoute(List<Cell> bins, List<Cell> selectedBins, Point currentLocation,
                              int score, int cost, MyLitterAgent.binType binType) {
        float averageRatio = 0;
        int counter = 0;
        for (Cell bin: bins) {
            LitterBin tempLitter = (LitterBin) bin;
            int tempScore = tempLitter.getTask().getRemaining();
            int tempCost = currentLocation.distanceTo(bin.getPoint());

            float tempRatio = (float) tempScore/tempCost;
            averageRatio += tempRatio;
            counter++;

            Cell closestStation;
            closestStation = binToStation.get(bin);
            int stationCost = binToStationDistance.get(bin);

            if (score + tempScore > remainingCapacity) {
                tempScore = remainingCapacity - score;
            }

            if (tempRatio >= averageRatio/counter) {
                List<Cell> tempSelectedBins = new ArrayList<>(selectedBins);
                tempSelectedBins.add(bin);

                List<Cell> tempBins = new ArrayList<>(bins);
                tempBins.remove(bin);

                if (score + tempScore < remainingCapacity && !tempBins.isEmpty() && tempSelectedBins.size() < 5) {
                    getBestRoute(tempBins, tempSelectedBins, bin.getPoint(), score + tempScore,
                            cost + tempCost, binType);
                }

                float tempScoreRatio = (float)(score + tempScore) / (cost + tempCost + stationCost);
                if (tempScoreRatio > scoreRatio) {
                    scoreRatio = tempScoreRatio;
                    selectedRoute = new ArrayList<>(tempSelectedBins);
                    this.closestStation = closestStation;
                    this.binType = binType;
                }
            }
        }
    }

    public List<Cell> getSelectedRoute() {
        for (Cell bin: selectedRoute) {
            sharedKnowledge.addPlannedBin((LitterBin) bin);
        }

        selectedRoute.add(closestStation);
        return selectedRoute;
    }

    public List<MyLitterAgent.state> getStateList() {
        List<MyLitterAgent.state> stateList = new ArrayList<>();
        for (Cell ignored : selectedRoute) {
            stateList.add(MyLitterAgent.state.MOVE_TO_POINT);
            if (binType == MyLitterAgent.binType.WASTE) {
                stateList.add(MyLitterAgent.state.PICKUP_WASTE);
            } else {
                stateList.add(MyLitterAgent.state.PICKUP_RECYCLING);
            }
        }

        if (!selectedRoute.isEmpty()) {
            stateList.add(MyLitterAgent.state.MOVE_TO_POINT);
            stateList.add(MyLitterAgent.state.LITTER_DROP_OFF);
        }
        return stateList;
    }
}
