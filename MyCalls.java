package ex0;

import java.util.Arrays;

public class MyCalls {
    public boolean r;
    private CallForElevator [] Calls;
    private int size;

    public MyCalls() {
        Calls = new CallForElevator[1];
        size=0;
        r=false;
    }
    public int getSize() {
        return size;
    }


    public void add(CallForElevator c) {
        r=true;
        if (size<Calls.length) {
            Calls[size] = c;
        } else {
            resize();
            Calls[size] = c;
        }
        size++;
    }

    private void resize() {
        CallForElevator [] temp = new CallForElevator[Calls.length*2];
        for (int i=0;i<Calls.length;i++) {
            temp[i]=Calls[i];
        }
        Calls=temp;
    }

//    public CallForElevator[] getCalls() {
//        int counter=0;
//        for (int i=0; i<Calls.length;i++) {
//            if (Calls[i]!=null) {
//                counter++;
//            }
//        }
//        CallForElevator[] temp = new CallForElevator[counter];
//        for (int i=0;i<temp.length;i++) {
//            temp[i]=Calls[i];
//        }
//        return temp;
//    }
    public CallForElevator getFirst() {
        return Calls[0];
    }

    public CallForElevator[] getPriorityCalls() {
        int counter=0;
        int Direction=Calls[0].getType();
        int i=0;
        while (i<getSize() && Calls[i].getType()==Direction) {
            counter++;
            i++;
        }
        CallForElevator [] ans = new CallForElevator[counter];
        i=0;
        while (i<getSize() && Calls[i].getType()==Direction) {
            ans[i]=Calls[i];
            i++;
        }
        return ans;
    }


}