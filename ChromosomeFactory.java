import java.util.List;

/**
 * Created by stg on 02/12/2016.
 */
public class ChromosomeFactory {
    private static ChromosomeFactory me;
    private int segNum, segLen, costs[];
    private List<Integer[]> kMap;
    private Integer[] tetaArray, reqArray;
    protected ChromosomeFactory(int segNum, int segLen, List<Integer[]> kMap, int[] costs, Integer[] tetaArray, Integer[] reqArray) {
        this.segNum = segNum;
        this.segLen = segLen;
        this.kMap = kMap;
        this.costs = costs;
        this.tetaArray = tetaArray;
        this.reqArray = reqArray;
    }

    public static synchronized ChromosomeFactory instance(int segNum, int segLen, List<Integer[]> kMap, int[] costs, Integer[] tetaArray, Integer[] reqArray) {
        if (me == null)
            me = new ChromosomeFactory(segNum, segLen, kMap, costs, tetaArray, reqArray);
        return me;
    }

    public CoyoteChromosome createEmpty() {
        CoyoteChromosome ch = new CoyoteChromosome(segNum, segLen, kMap);
        ch.setCosts(costs);
        ch.tetaFiller(tetaArray);
        ch.requestFiller(reqArray);
        return ch;
    }

    public static CoyoteChromosome instance() {
        return me.createEmpty();
    }
}
