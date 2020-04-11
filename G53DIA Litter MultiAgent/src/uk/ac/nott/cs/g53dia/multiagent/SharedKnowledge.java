package uk.ac.nott.cs.g53dia.multiagent;

import uk.ac.nott.cs.g53dia.multilibrary.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This class includes methods help store and retrieve shared knowledge between agents.
 * This class essentially allows the agents to communicate between each other.
 *
 */
public class SharedKnowledge {

    private List<Cell> rechargePoints = new ArrayList<>();
    private List<LitterBin> plannedBins = new ArrayList<>();

    /**
     * This method is called at every time step by each agent.
     * It stores new recharge points it doesn't have saved into the relevant list.
     *
     * @param view The 30x30 view scope of cells the agent can see.
     *
     */
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

    /**
     * This method is called when a bin gets added to an agent's route.
     * It stores bin to the plannedBins list so other agents do not plan it in as well.
     *
     * @param bin The bin to be added.
     *
     */
    protected void addPlannedBin(LitterBin bin) {
        plannedBins.add(bin);
    }

    /**
     * This method is called when a bin gets removed to an agent's route.
     * The bin gets removed from the PlannedBins list to be free for any agent to plan it in their route.
     *
     * @param bin The bin to be removed.
     *
     */
    protected void removePlannedBin(LitterBin bin) {
        plannedBins.remove(bin);
    }

    protected List<Cell> getRechargePoints() {
        return rechargePoints;
    }

    protected List<LitterBin> getPlannedBins() {
        return plannedBins;
    }
}
