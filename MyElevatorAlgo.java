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
        return "Lior's Algo";
    }

    @Override
    public int allocateAnElevator(CallForElevator c) {
        AllCalls.add(c);
        int ans=0;
        double t0=Integer.MAX_VALUE;
        double t=Integer.MAX_VALUE;
        if (c.getType()==Elevator.UP) {  //if call direction is up + all elevators are moving, take elevator thats moving up from below src
            for (int i=0; i<B.numberOfElevetors(); i++) {
                if (B.getElevetor(i).getPos()<c.getSrc()) {
                    if (B.getElevetor(i).getState()==Elevator.UP) {
                        if (t>Time(c.getSrc(),c.getDest(),B.getElevetor(i))) {
                            t=Time(c.getSrc(),c.getDest(),B.getElevetor(i));
                            ans=i;
                        }
                    }
                }
            }
            if (t<Integer.MAX_VALUE) {
                return ans;
            }
        } else {
            for (int i=0; i<B.numberOfElevetors(); i++) {  // if call direction is down + all elevators are moving take elevator thats moving down from a floor above src
                if (B.getElevetor(i).getPos()>c.getSrc()) {
                    if (B.getElevetor(i).getState()==Elevator.DOWN) {
                        if (t>Time(c.getSrc(),c.getDest(),B.getElevetor(i))) {
                            t=Time(c.getSrc(),c.getDest(),B.getElevetor(i));
                            ans=i;
                        }
                    }
                }
            }
            if (t<Integer.MAX_VALUE) {
                return ans;
            }
        }
        for (int i=0; i<B.numberOfElevetors(); i++) {    // take fastest elevator which state == LEVEL
            if (getBuilding().getElevetor(i).getState()== Elevator.LEVEL) {
                if (Time(c.getSrc(),c.getDest(),B.getElevetor(i))<t0) {
                    t0 = Time(c.getSrc(), c.getDest(), B.getElevetor(i));
                    ans=i;
                }
            }
            if (t0<Integer.MAX_VALUE) {
                return ans;
            }
        }

        for (int i=0; i<AllCalls.size(); i++) { //take elev with closest dest to src (worst case)
            int temp = Integer.MAX_VALUE;
            if (!AllCalls.get(i).equals(c)) {
                if (AllCalls.get(i).getDest()==c.getSrc()) {
                    ans = AllCalls.get(i).allocatedTo();
                    return ans;
                } else {
                    if (Math.abs(AllCalls.get(i).getDest()-c.getSrc())<temp) {
                        temp = AllCalls.get(i).getDest()-c.getSrc();
                        ans=AllCalls.get(i).allocatedTo();
                    }
                }
            }
            if (temp<Integer.MAX_VALUE) {
                return ans;
            }
        }

        return ans;
    }

    @Override
    public void cmdElevator(int elev) {
        Elevator E = B.getElevetor(elev);
        MyCalls ElvCalls = new MyCalls();
        for (int i = 0; i < AllCalls.size(); i++) {
            if (AllCalls.get(i).allocatedTo() == elev && AllCalls.get(i).getState() != CallForElevator.DONE) {
                ElvCalls.add(AllCalls.get(i));
            }
        }
        if (ElvCalls.r) { //to check for "unreal" calls and to prevent errors
           if (ElvCalls.getSize()==1) { //the elevator has only 1 call pending
               CallForElevator c = ElvCalls.getFirst();
               if (c.getState()==CallForElevator.INIT) {
                   E.goTo(c.getSrc());
                   if (E.getState()==Elevator.LEVEL) {
                       E.stop(c.getSrc());
                   }
               } else if (c.getState()==CallForElevator.GOING2SRC) {
                   E.stop(c.getSrc());
                   if (E.getState()==Elevator.LEVEL) {
                       E.goTo(c.getSrc());
                   }
               } else if (c.getState()==CallForElevator.GOIND2DEST) {
                   E.stop(c.getDest());
                   if (E.getState()==Elevator.LEVEL) {
                       E.goTo(c.getDest());
                   }
               }
           } else if (ElvCalls.getSize()>1) {   //the elevator has multiple calls pending
               CallForElevator[] CurrentCalls = ElvCalls.getPriorityCalls();
               int Direction = CurrentCalls[0].getType();
               if (Direction==1) {
                   int minfloor=Integer.MAX_VALUE;
                   for (int i=0; i<CurrentCalls.length;i++) {
                       if (CurrentCalls[i].getState()==CallForElevator.INIT || CurrentCalls[i].getState()==CallForElevator.GOING2SRC) {
                           if (CurrentCalls[i].getSrc()<minfloor) {
                               minfloor=CurrentCalls[i].getSrc();
                           }
                       }
                       if (CurrentCalls[i].getState()==CallForElevator.GOIND2DEST) {
                           if (CurrentCalls[i].getDest()<minfloor) {
                               minfloor=CurrentCalls[i].getDest();
                           }
                       }
                   }
                   if (E.getState()==Elevator.LEVEL) {
                       E.goTo(minfloor);
                   } else {
                       E.stop(minfloor);
                   }
               } else if (Direction == -1) {
                   int maxfloor=Integer.MIN_VALUE;
                   for (int i=0; i<CurrentCalls.length;i++) {
                       if (CurrentCalls[i].getState()==CallForElevator.INIT || CurrentCalls[i].getState()==CallForElevator.GOING2SRC) {
                           if (CurrentCalls[i].getSrc()>maxfloor) {
                               maxfloor=CurrentCalls[i].getSrc();
                           }
                       }
                       if (CurrentCalls[i].getState()==CallForElevator.GOIND2DEST) {
                           if (CurrentCalls[i].getDest()>maxfloor) {
                               maxfloor=CurrentCalls[i].getDest();
                           }
                       }
                   }
                   if (E.getState()==Elevator.LEVEL) {
                       E.goTo(maxfloor);
                   } else {
                       E.stop(maxfloor);
                   }
               }
           }

           /* else if (CurrentCalls.length>1) { //the elevator has multiple calls pending
               int up_calls=0;
               int down_calls=0;
               for (int i=0;i<CurrentCalls.length;i++) {
                   if (CurrentCalls[i].getType()==CallForElevator.UP) {
                       up_calls++;
                   } else if (CurrentCalls[i].getType()==CallForElevator.DOWN) {
                       down_calls++;
                   }
               }
               if (up_calls>=down_calls) {
                   int Min_Src=Integer.MAX_VALUE;
                   int Min_Dest=Integer.MAX_VALUE;
                   for (int i=0;i<CurrentCalls.length;i++) {
                       CallForElevator c = CurrentCalls[i];
                       if (c.getType()==CallForElevator.UP) {
                           if (c.getSrc()<Min_Src && E.getPos()<c.getSrc()) {
                               Min_Src=c.getSrc();
                           }
                           if (c.getDest()<Min_Dest) {
                               Min_Dest=c.getDest();
                           }
                       }
                   }
                   if (Min_Src<Min_Dest) {
                       if (E.getState()==Elevator.LEVEL) {
                           E.goTo(Min_Src);
                       } else {
                           E.stop(Min_Src);
                       }
                   } else {
                       if (E.getState()==Elevator.LEVEL) {
                           E.stop(Min_Dest);
                       } else {
                           E.goTo(Min_Dest);
                       }
                   }
               } else {
                   int Max_Src=Integer.MIN_VALUE;
                   int Max_Dest=Integer.MIN_VALUE;
                   for (int i=0;i<CurrentCalls.length;i++) {
                       CallForElevator c = CurrentCalls[i];
                       if (c.getType()==CallForElevator.DOWN) {
                           if (c.getSrc()>Max_Src && E.getPos()>c.getSrc()) {
                               Max_Src=c.getSrc();
                           }
                           if (c.getDest()>Max_Dest) {
                               Max_Dest=c.getDest();
                           }
                       }
                   }
                   if (Max_Src>Max_Dest) {
                       if (E.getState()==Elevator.LEVEL) {
                           E.goTo(Max_Src);
                       } else {
                           E.stop(Max_Src);
                       }
                   } else {
                       if (E.getState()==Elevator.LEVEL) {
                           E.goTo(Max_Dest);
                       } else {
                           E.stop(Max_Dest);
                       }
                   }
               }
           }*/
        }
    }

    public double Time(int src, int dest, Elevator E){
        int pos = E.getPos();
        double V1 = ((Math.abs(pos-src))/E.getSpeed())+E.getStartTime()+E.getStopTime()+E.getTimeForOpen()+E.getTimeForClose();
        double V2 = ((Math.abs(src-dest))/E.getSpeed())+E.getStartTime()+E.getStopTime()+E.getTimeForOpen()+E.getTimeForClose();
        return V1+V2;
    }

}
