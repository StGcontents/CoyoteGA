import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by stg on 01/12/2016.
 */
public class CoyoteChromosome implements Cloneable {

    private int[] genes;
    private int[] costs;
    private int[] requests, absoluteRequests;
    private int segmentLen;
    private int fitness;
    private BooleanCascade feasibility;
    private List<Integer[]> userTypeMap;
    private int size;


    public CoyoteChromosome(int segmentNum, int segmentLen, List<Integer[]> kMap) {
        this.genes = new int[(segmentLen + 1) * segmentNum]; //also teta
        this.costs = new int[(segmentLen + 1) * segmentNum];
        this.requests = new int[segmentLen];
        this.absoluteRequests = new int[segmentLen];
        this.segmentLen = segmentLen + 1;
        userTypeMap = kMap;
        feasibility = new BooleanCascade(segmentLen);
        size = segmentNum * (segmentLen + 1);
    }

    public void tetaFiller(Integer... tetaArray) {
        for (int i = 0; i < tetaArray.length; i++) {
            genes[i * (segmentLen)] = tetaArray[i];
            costs[i * (segmentLen)] = Integer.MAX_VALUE;
        }
    }

    public void setCosts(int[] costs) {
        this.costs = costs;
    }

    public void blackListFiller(Integer... indexes) {
        for (int i : indexes)
            costs[i] = Integer.MAX_VALUE;
    }

    public void requestFiller(Integer... requests) {
        for (int i = 0; i < segmentLen - 1; ++i) {
            this.requests[i] = -requests[i]; //negative numbers!
            this.absoluteRequests[i] = requests[i];
        }
    }

    public boolean isFeasible() {
        return feasibility.isTrue();
    }

    public int length() {
        return genes.length;
    }

    public boolean isTeta(int i) {
        return i % segmentLen == 0;
    }

    public boolean isBlacklisted(int i) {
        return Integer.MAX_VALUE == costs[i];
    }

    public int get(int i) {
        return genes[i];
    }

    public boolean testSwap(int i, int newValue) {
        int currentTeta = genes[i - (i % segmentLen)];
        return newValue >= 0 && currentTeta - (newValue - genes[i]) > 0;
    }

    public boolean testConstraint(int i) {
        int index = (i - 1) % segmentLen;
        //System.out.println("CONSTRAINT " + index + " MET? " + feasibility.isTrueAtIndex(index));
        return feasibility.isTrueAtIndex(index);
    }

    public void set(int i, int newValue) {
        int gap = newValue - genes[i];
        genes[i] = newValue;
        genes[i - (i % segmentLen)] -= gap;


        fitness += gap * costs[i];

        int j = (i % segmentLen) - 1;
        int k = getCurrentType(i);
        requests[j] += gap * k;
        if (requests[j] >= 0) feasibility.switchOn(j);
        else feasibility.switchOff(j);
    }

    private int getCurrentType(int i) {
        int k = userTypeMap.get(0)[1];
        for (int j = 1; j < userTypeMap.size(); j++) {
            if (i < userTypeMap.get(j)[0]) break;
            k = userTypeMap.get(j)[1];
        }
        return k;
    }

    public int getTeta(int i) {
        return genes[i - (i % segmentLen)];
    }

    public int getTetaChunk(int i) {
        int teta = genes[i - (i % (segmentLen))];
        int reqIndex = (i % (segmentLen));
        int req = -requests[reqIndex - 1];
        req /= getCurrentType(i);
        return req < teta  ? req /*+ (teta - req) / 4*/ : teta;
    }

    public CoyoteChromosome clone() {
        try {
            CoyoteChromosome copy = (CoyoteChromosome) super.clone();
            copy.fitness = this.fitness;
            return copy;
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getFitness() {
        return isFeasible() ? fitness : Integer.MAX_VALUE;
    }

    public int getSize() {
        return size;
    }

    public void printGenes() {
        for (int e : genes) {
            System.out.print(e);
            System.out.print(" | ");
        }
        System.out.println();
        System.out.println("FEASIBILITY: " + isFeasible());
        for (int e : requests) {
            System.out.print(e);
            System.out.print(" | ");
        }
        System.out.println("FITNESS: " + fitness);
    }

    public void printCosts() {
        for (int e : costs) {
            System.out.print(e);
            System.out.print(" | ");
        }
    }

    public int getShrinkable(int index) {
        int k = getCurrentType(index);
        int resizable = requests[(index % segmentLen) - 1] / k;
        int window = /*(int) 0.3 * */absoluteRequests[(index % segmentLen) - 1] / k;
        return window + resizable;
        //return resizable + window < genes[index] ? resizable + window : resizable;
    }

    public int getRequestSize() {
        return segmentLen;
    }

    public void discombobulate() {
        int offset = new Random(new Date().getTime()).nextInt(size), index;
        for (int i = 0; i < size / 1.5; ++i) {
            index = (i + offset) % size;
            if (isBlacklisted(index) || genes[index] == 0)
                continue;
            set(index, 0);
        }
    }

    public int[] export() {
        int[] exportCopy = new int[size];
        System.arraycopy(genes, 0, exportCopy, 0, size);
        return exportCopy;
    }

    public int[] exportRequests() {
        int[] exportCopy = new int[requests.length];
        System.arraycopy(requests, 0, exportCopy, 0, requests.length);
        return exportCopy;
    }

    public int[] getCosts() {
        return costs;
    }

    class BooleanCascade {
        private boolean bottomBoolean = false;
        private List<boolean[]> booleanCascade = new ArrayList<>();

        BooleanCascade(int size) {
            boolean resized, booleanFloor[];
            while (size != 1) {
                resized = size % 2 == 1;
                size += size % 2;
                booleanFloor = new boolean[size];
                booleanCascade.add(booleanFloor);
                if (resized)
                    booleanFloor[size - 1] = true;
                size /= 2;
            }
        }

        boolean isTrueAtIndex(int index) {
            return booleanCascade.get(0)[index];
        }

        boolean isTrue() {
            return bottomBoolean;
        }

        void switchOn(int index) {
            propagate(0, index, true);
        }

        void switchOff(int index) {
            propagate(0, index, false);
        }

        private void propagate(int level, int index, boolean state) {
            if (level >= booleanCascade.size()) {
                bottomBoolean = state;
                return;
            }
            boolean[] cascadeLevel = booleanCascade.get(level);
            if (state == cascadeLevel[index]) {
                return;
            }
            else {
                cascadeLevel[index] = state;
                boolean coupledState = index % 2 == 0 ? cascadeLevel[index + 1] : cascadeLevel[index - 1];
                if (coupledState) {
                    propagate(level + 1, index / 2, state);
                }
            }
        }
    }
}
