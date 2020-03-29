package uk.ac.nott.cs.g53dia.multiagent;

import uk.ac.nott.cs.g53dia.multilibrary.Cell;
import uk.ac.nott.cs.g53dia.multilibrary.RechargePoint;

import java.util.List;

import static uk.ac.nott.cs.g53dia.multilibrary.LitterAgent.MAX_CHARGE;

public class Reactive {
    Helpers helpers = new Helpers();

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
