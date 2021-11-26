package ex0;

public class MyCalls {                     //this class is used to access all the calls which are allocated to an Elevator
    public boolean r;                      //in a specific moment, by doing so we can calculate every elevator's time to
    private CallForElevator[] Calls;       //complete their journey and know which elevator should be allocated to which call,
    private int size;                      //also, we can use the data to know where every elevator needs to go / stop at any given moment,
    private Elevator E;                    //and that way we know how to command the elevator in the cmdElevator func.

    public MyCalls(Elevator E) {
        Calls = new CallForElevator[1];
        size = 0;
        r = false;
        this.E = E;
    }

    public int getSize() {
        return size;
    }

    public void add(CallForElevator c) {
        r = true;
        if (size < Calls.length) {
            Calls[size] = c;
        } else {
            resize();
            Calls[size] = c;
        }
        size++;
    }

    private void resize() {
        CallForElevator[] temp = new CallForElevator[Calls.length * 2];
        for (int i = 0; i < Calls.length; i++) {
            temp[i] = Calls[i];
        }
        Calls = temp;
    }

    public CallForElevator getFirst() {
        return Calls[0];
    }

    public CallForElevator[] getPriorityCalls() { //return an array of all calls that are in the same direction
        int Direction = Calls[0].getType();       //so we can complete all calls in same direction before moving on to calls in the opposite direction
        int i = 0;
        int counter=0;
        while (i < getSize()) {
            if (Calls[i].getType() == Direction) {
                counter++;
            }
            i++;
        }
        CallForElevator[] ans = new CallForElevator[counter];
        i = 0;
        int k=0;
        while (i < getSize()) {
            if (Calls[i].getType() == Direction) {
                ans[k++] = Calls[i];
            }
            i++;
        }
        return ans;
    }

    public double getTime() {
        double totalTime = 0;
        for (int i=0;i<getSize();i++) {
            totalTime = totalTime + Time2(Calls[i].getSrc(),Calls[i].getDest(),this.E );
        }
        return totalTime;
    }

        public double Time2 ( int src, int dest, Elevator E){
            int pos = E.getPos();
            double V1;
            double V2;
            if (E.getState() == Elevator.LEVEL) {
                if (pos == src) {
                    V1 = ((Math.abs(pos - src)) / E.getSpeed()) + E.getStartTime() + E.getTimeForOpen() + E.getTimeForClose();
                    V2 = ((Math.abs(src - dest)) / E.getSpeed()) + E.getStartTime() + E.getStopTime() + E.getTimeForOpen() + E.getTimeForClose();
                } else {
                    V1 = ((Math.abs(pos - src)) / E.getSpeed()) + E.getStartTime() + E.getStopTime() + E.getTimeForOpen() + E.getTimeForClose();
                    V2 = ((Math.abs(src - dest)) / E.getSpeed()) + E.getStartTime() + E.getStopTime() + E.getTimeForOpen() + E.getTimeForClose();
                }
            } else {
                V1 = ((Math.abs(pos - src)) / E.getSpeed()) + E.getStopTime() + E.getTimeForOpen() + E.getTimeForClose();
                V2 = ((Math.abs(src - dest)) / E.getSpeed()) + E.getStartTime() + E.getStopTime() + E.getTimeForOpen() + E.getTimeForClose();
            }
            return V1 + V2;
        }

    }
