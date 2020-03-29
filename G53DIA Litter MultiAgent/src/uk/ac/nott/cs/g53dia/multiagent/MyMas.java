package uk.ac.nott.cs.g53dia.multiagent;
import uk.ac.nott.cs.g53dia.multilibrary.*;

import java.util.*;

public class MyMas extends MAS {

    /** 
     * Number of agents in the MAS (this is just an **example**, not a requirement).
     */
    private static int MAS_SIZE = 2;

    protected SharedKnowledge sharedKnowledge = new SharedKnowledge();

    public MyMas(Random r) {
		for (int i=0; i<MAS_SIZE; i++) {
			this.add(new MyLitterAgent(i+4, sharedKnowledge));
		}
    }
}
