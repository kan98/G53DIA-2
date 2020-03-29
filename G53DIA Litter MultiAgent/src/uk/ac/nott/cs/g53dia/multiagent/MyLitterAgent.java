package uk.ac.nott.cs.g53dia.multiagent;

import java.util.ArrayList;
import java.util.List;

import uk.ac.nott.cs.g53dia.multilibrary.*;

/**
 * A simple example LitterAgent
 * 
 * @author Julian Zappala
 */
/*
 * Copyright (c) 2011 Julian Zappala
 * 
 * See the file "license.terms" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
public class MyLitterAgent extends LitterAgent {
	public MyLitterAgent(int direction, SharedKnowledge sharedKnowledge){
		this.direction = direction;
		this.sharedKnowledge = sharedKnowledge;
		this.deliberative = new Deliberative(sharedKnowledge);
	}

	Reactive reactive = new Reactive();
	Deliberative deliberative;

	private final int direction;
	private SharedKnowledge sharedKnowledge;
	private List<Cell> cellPoints = new ArrayList<>();
	private List<state> stateList = new ArrayList<>();

	private List<Cell> recyclingBins = new ArrayList<>();
	private List<Cell> wasteBins = new ArrayList<>();
	private List<Cell> recyclingStations = new ArrayList<>();
	private List<Cell> wasteStations = new ArrayList<>();

	protected enum state {
		FORAGE,
		MOVE_TO_POINT,
		PICKUP_WASTE,
		PICKUP_RECYCLING,
		LITTER_DROP_OFF,
		REFUEL
	}

	protected enum binType {
		NONE,
		WASTE,
		RECYCLING
	}

	private void getNextState(Point currentPoint) {
		if (!stateList.isEmpty()) {
			if (stateList.get(0) != state.MOVE_TO_POINT) {
				stateList.remove(0);
			} else {
				if (!cellPoints.isEmpty()) {
					if (currentPoint.equals(cellPoints.get(0).getPoint())) {
						stateList.remove(0);
						cellPoints.remove(0);
					}
				} else {
					stateList = new ArrayList<>();
					stateList.add(state.FORAGE);
				}
			}
		}
		if (stateList.isEmpty()) {
			stateList.add(state.FORAGE);
		}
	}

	private void deliberativeRoutePlanning() {
		boolean isEqualPosition = false;
		if (!cellPoints.isEmpty()) {
			isEqualPosition = getPosition().equals(cellPoints.get(0).getPoint());
		}

		if (stateList.isEmpty() || stateList.get(0) == state.FORAGE || (stateList.size() >= 2 &&
				(stateList.get(1) == state.PICKUP_RECYCLING || stateList.get(1) == state.PICKUP_WASTE) && !isEqualPosition)) {
			binType binType = MyLitterAgent.binType.NONE;
			if (getWasteLevel() > 0) {
				binType = MyLitterAgent.binType.WASTE;
			} else if (getRecyclingLevel() > 0) {
				binType = MyLitterAgent.binType.RECYCLING;
			}

			Deliberative deliberative = new Deliberative(sharedKnowledge);
			deliberative.planRoute(getPosition(), MAX_LITTER - getLitterLevel(),
					binType, cellPoints, recyclingBins, wasteBins, wasteStations, recyclingStations);

			if (!deliberative.getStateList().isEmpty()) {
				for (Cell cellPoint: cellPoints) {
					if (cellPoint instanceof LitterBin) {
						sharedKnowledge.removePlannedBin((LitterBin) cellPoint);
					}
				}

				stateList = deliberative.getStateList();
				cellPoints = deliberative.getSelectedRoute();
			}
		}
	}

	private void storeCellInfo(Cell[][] view, long timeStep) {
		if(timeStep % 2 == 0) {
			wasteBins = new ArrayList<>();
			recyclingBins = new ArrayList<>();
			wasteStations = new ArrayList<>();
			recyclingStations = new ArrayList<>();
		}

		for (int i=0; i != view.length; i++) {
			for (Cell cell : view[i]) {
				if (cell instanceof RecyclingBin && !recyclingBins.contains(cell)) {
					recyclingBins.add(cell);
				} else if (cell instanceof WasteBin && !wasteBins.contains(cell)) {
					wasteBins.add(cell);
				} else if (cell instanceof WasteStation && !wasteStations.contains(cell)) {
					wasteStations.add(cell);
				} else if (cell instanceof RecyclingStation && !recyclingStations.contains(cell)) {
					recyclingStations.add(cell);
				}
			}
		}
	}

	/*
	 * The following is a simple demonstration of how to write a tanker. The
	 * code below is very stupid and simply moves the tanker randomly until the
	 * charge agt is half full, at which point it returns to a charge pump.
	 */
	public Action senseAndAct(Cell[][] view, long timeStep) {
		sharedKnowledge.addNewCells(view);

		storeCellInfo(view, timeStep);

		deliberativeRoutePlanning();

		Cell rechargePoint = reactive.recharge(getCurrentCell(view), getChargeLevel()-2, sharedKnowledge.getRechargePoints());
		if (rechargePoint != null && !stateList.contains(state.REFUEL) && !(getCurrentCell(view) instanceof RechargePoint)) {
			cellPoints.add(0, rechargePoint);
			stateList.add(0, state.REFUEL);
			stateList.add(0, state.MOVE_TO_POINT);
		}

		if (timeStep % 1000 == 0) {
			System.out.println("timestep: " + timeStep + "; score = " + getScore());
		}

		getNextState(getPosition());

		switch (stateList.get(0)) {
			case REFUEL:
				return new RechargeAction();
			case LITTER_DROP_OFF:
				return new DisposeAction();
			case PICKUP_RECYCLING:
				sharedKnowledge.removePlannedBin((LitterBin) getCurrentCell(view));
				RecyclingBin recyclingBin = (RecyclingBin) getCurrentCell(view);
				return new LoadAction(recyclingBin.getTask());
			case PICKUP_WASTE:
				sharedKnowledge.removePlannedBin((LitterBin) getCurrentCell(view));
				WasteBin wasteBin = (WasteBin) getCurrentCell(view);
				return new LoadAction(wasteBin.getTask());
			case MOVE_TO_POINT:
				return new MoveTowardsAction(cellPoints.get(0).getPoint());
			case FORAGE:
			default:
				return new MoveAction(direction);
		}
	}
}
