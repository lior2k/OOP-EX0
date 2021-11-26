package ex0.algo;

import ex0.Building;
import ex0.CallForElevator;
import ex0.Elevator;
import ex0.MyCalls;

import java.util.Vector;

public class MyElevatorAlgo implements ElevatorAlgo{
    final private Building B;
    final private Vector<CallForElevator> AllCalls;

    public MyElevatorAlgo(Building B) {
        this.B=B;
        AllCalls = new Vector<>();
    }


    @Override
    public Building getBuilding() {
        return this.B;
    }

    @Override
    public String algoName() {
        return "Lior's & Liel's Algo";
    }

    @Override
    public int allocateAnElevator(CallForElevator c) {
        AllCalls.add(c);
        int ans = 0;
        double t = Integer.MAX_VALUE;
        for (int i=0; i < B.numberOfElevetors(); i++) {    // take fastest elevator which state == LEVEL
            if (getBuilding().getElevetor(i).getState() == Elevator.LEVEL) {
                if (Time(B.getElevetor(i)) < t) {
                    t = Time(B.getElevetor(i));
                    ans = i;
                }
            }
            if (t < Integer.MAX_VALUE) {
                return ans;
            }
        }
        if (c.getType() == CallForElevator.UP) {  //if call direction is up + all elevators are moving, take elevator thats moving up from below src
            for (int i= 0; i < B.numberOfElevetors(); i++) {
                if (B.getElevetor(i).getPos() < c.getSrc()) {
                    if (B.getElevetor(i).getState() == Elevator.UP) {
                        if (t > Time(B.getElevetor(i))) {
                            t = Time(B.getElevetor(i));
                            ans = i;
                        }
                    }
                }
            }
            if (t < Integer.MAX_VALUE) {
                return ans;
            }
        } else {
            for (int i=0; i < B.numberOfElevetors(); i++) {  // if call direction is down + all elevators are moving take elevator thats moving down from a floor above src
                if (B.getElevetor(i).getPos() > c.getSrc()) {
                    if (B.getElevetor(i).getState() == Elevator.DOWN) {
                        if (t > Time(B.getElevetor(i))) {
                            t = Time(B.getElevetor(i));
                            ans = i;
                        }
                    }
                }
            }
            if (t < Integer.MAX_VALUE) {
                return ans;
            }
        }

        int closest_dest = Integer.MAX_VALUE;
        for (int i=0; i < B.numberOfElevetors(); i++) {    //take elevator with closest dest to src (dest being the last dest
            Elevator E = B.getElevetor(i);
            MyCalls ElvCalls = new MyCalls(E);
            for (int j = 0; j < AllCalls.size(); j++) {
                if (AllCalls.get(j).allocatedTo() == i && AllCalls.get(j).getState() != CallForElevator.DONE) {
                    ElvCalls.add(AllCalls.get(j));
                }
            }
            if (ElvCalls.r) {
                CallForElevator[] PrioCalls = ElvCalls.getPriorityCalls();
                for (int k=0; k < PrioCalls.length; k++) {
                    if (Math.abs(c.getSrc()-PrioCalls[k].getDest()) < closest_dest) {
                        closest_dest = Math.abs(c.getSrc()-PrioCalls[k].getDest());
                        ans = i;
                    }
                }
            }
        }
        for (int i=0; i < AllCalls.size(); i++) {        // delete finished calls
            if (AllCalls.get(i).getState()==CallForElevator.DONE) {
                AllCalls.remove(i);
            }
        }
        return ans;
}

    @Override
    public void cmdElevator(int elev) {
        Elevator E = B.getElevetor(elev);
        MyCalls ElvCalls = new MyCalls(E);
        for (int i = 0; i < AllCalls.size(); i++) {
            if (AllCalls.get(i).allocatedTo() == elev && AllCalls.get(i).getState() != CallForElevator.DONE) {
                ElvCalls.add(AllCalls.get(i));
            }
        }
        if (ElvCalls.r) { //to check for "unreal" calls and to prevent errors
            if (ElvCalls.getSize() == 1) { //the elevator has only 1 call pending
                CallForElevator c = ElvCalls.getFirst();
                if (c.getState() == CallForElevator.INIT) {
                    E.goTo(c.getSrc());
                    if (E.getState() == Elevator.LEVEL) {
                        E.stop(c.getSrc());
                    }
                } else if (c.getState() == CallForElevator.GOING2SRC) {
                    E.stop(c.getSrc());
                    if (E.getState() == Elevator.LEVEL) {
                        E.goTo(c.getSrc());
                    }
                } else if (c.getState() == CallForElevator.GOIND2DEST) {
                    E.stop(c.getDest());
                    if (E.getState() == Elevator.LEVEL) {
                        E.goTo(c.getDest());
                    }
                }
            } else if (ElvCalls.getSize() > 1) {   //the elevator has multiple calls pending
                CallForElevator[] CurrentCalls = ElvCalls.getPriorityCalls();
                int Direction = CurrentCalls[0].getType();
                if (Direction == 1) {
                    int minfloor=Integer.MAX_VALUE;
                    for (int i=0; i < CurrentCalls.length; i++) {
                        if (CurrentCalls[i].getState() == CallForElevator.INIT || CurrentCalls[i].getState() == CallForElevator.GOING2SRC) {
                            if (CurrentCalls[i].getSrc() < minfloor) {
                                minfloor = CurrentCalls[i].getSrc();
                            }
                        }
                        if (CurrentCalls[i].getState() == CallForElevator.GOIND2DEST) {
                            if (CurrentCalls[i].getDest() < minfloor) {
                                minfloor = CurrentCalls[i].getDest();
                            }
                        }
                    }
                    if (E.getState() == Elevator.LEVEL) {
                        E.goTo(minfloor);
                    } else {
                        E.stop(minfloor);
                    }
                } else if (Direction == -1) {
                    int maxfloor = Integer.MIN_VALUE;
                    for (int i=0; i < CurrentCalls.length; i++) {
                        if (CurrentCalls[i].getState() == CallForElevator.INIT || CurrentCalls[i].getState() == CallForElevator.GOING2SRC) {
                            if (CurrentCalls[i].getSrc() > maxfloor) {
                                maxfloor = CurrentCalls[i].getSrc();
                            }
                        }
                        if (CurrentCalls[i].getState() == CallForElevator.GOIND2DEST) {
                            if (CurrentCalls[i].getDest() > maxfloor) {
                                maxfloor = CurrentCalls[i].getDest();
                            }
                        }
                    }
                    if (E.getState() == Elevator.LEVEL) {
                        E.goTo(maxfloor);
                    } else {
                        E.stop(maxfloor);
                    }
                }
            }
        }
    }

    public double Time(Elevator E) { //calculate elevator time to reach its final destination
        MyCalls ElvCalls = new MyCalls(E);
        for (int i=0;i<AllCalls.size();i++) {
            if (AllCalls.get(i).allocatedTo()==E.getID() && AllCalls.get(i).getState() != CallForElevator.DONE) {
                ElvCalls.add(AllCalls.get(i));
            }
        }
        return ElvCalls.getTime();
    }

}