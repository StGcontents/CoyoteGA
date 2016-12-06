import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by stg on 01/12/2016.
 */
public class Main {

    public static final List<Integer> BLACKLIST = new ArrayList<>();

    public static void main(String[] argv) {
        BLACKLIST.clear();
        new ChromosomePool(100, new InputParser("C:\\Users\\stg\\Desktop\\Co_30_1_NT_0.txt").getChromosome()).evolve(5000l);
/*        CoyoteChromosome ch = new InputParser("C:\\Users\\stg\\Desktop\\Co_30_1_NT_0.txt").getChromosome();

        new CoyoteMutation().mutate(ch, 0);
        ch.printGenes();

        CoyoteChromosome clone = ch.clone();
        new CoyoteMutation().mutate(clone, 0);
        System.out.println();
        clone.printGenes();*/
    }
}
