package com.warships.nodes;

import com.warships.constants.WarshipConstants;

public class EngineNode extends TechNode {

    private int techLevelRequired;

    public EngineNode(int engineNumber) {
        super(("Engine#" + (engineNumber+2)), determineUnlockCost(engineNumber));
        super.setEngineNumber(engineNumber);
        this.techLevelRequired = 300 * engineNumber;
    }

    public int getTechLevelRequired() {
        return techLevelRequired;
    }

    private static int determineUnlockCost(int engine) {
        switch (engine) {
            case 1:
                return WarshipConstants.ENGINE_ONE_UNLOCK_COST;
            case 2:
                return WarshipConstants.ENGINE_TWO_UNLOCK_COST;
            case 3:
                return WarshipConstants.ENGINE_THREE_UNLOCK_COST;
            case 4:
                return WarshipConstants.ENGINE_FOUR_UNLOCK_COST;
            case 5:
                return WarshipConstants.ENGINE_FIVE_UNLOCK_COST;
            default:
                throw new IllegalArgumentException("Invalid engine number provided: " + engine);
        }
    }
}
