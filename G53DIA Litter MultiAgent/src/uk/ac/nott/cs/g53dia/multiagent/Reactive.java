package uk.ac.nott.cs.g53dia.multiagent;

import uk.ac.nott.cs.g53dia.multilibrary.Cell;
import uk.ac.nott.cs.g53dia.multilibrary.RechargePoint;

import java.util.List;

import static uk.ac.nott.cs.g53dia.multilibrary.LitterAgent.MAX_CHARGE;

/**
 * This class includes methods that reactive components from the DemoLitterAgent may use.
 *
 */
public class Reactive {
    Helpers helpers = new Helpers();

    /**
     * This method checks whether it is appropriate to refuel at this time step or not.
     * If it is appropriate to refuel, it will return the closest refuel point.
     *
     * @param cell The current cell the agent is on.
     * @param chargeLevel The amount of charge the agent has left.
     * @param rechargePoints A list of recharge points the agent has seen to be used to find the closest one.
     * @return A recharge point cell if the agent is to recharge, otherwise null.
     *
     */
    protected Cell recharge(Cell cell, int chargeLevel, List<Cell> rechargePoints) {
        if ((chargeLevel <= MAX_CHARGE * 0.3) && !(cell instanceof RechargePoint)) {
            Cell closestRechargePoint = helpers.findClosest(cell.getPoint(), rechargePoints);
            int minDistance = helpers.getMinDistance();
            if (closestRechargePoint != null && (minDistance >= chargeLevel || (minDistance < 5 && chargeLevel < 50)
                    || (minDistance < 2 && chargeLevel < 100))) {
                return closestRechargePoint;
            }
        }
        return null;
    }
}
