import java.util.Date;
import java.util.Random;

/**
 * Created by stg on 01/12/2016.
 */
public class CoyoteCrossover {

    double probability;

    public CoyoteCrossover() {
        this(0.5);
    }

    public CoyoteCrossover(double probability) {
        this.probability = probability;
    }

    public void crossover(CoyoteChromosome mom, CoyoteChromosome dad) {
        Random token = new Random(new Date().getTime());

        int momGene, dadGene;
        int max = mom.getSize();

        int mutations = 0;
        boolean momMutated, dadMutated;

        int offset = token.nextInt(max);
        int index;

        for (int i = 0; i < max; ++i) {
            index = (offset + i) % max;
            if (mom.isBlacklisted(index))
                continue; //l'elemento è inserito nella tabu list come elemento della diagonale e non può essere modificato!

            //if (token.nextDouble() > probability)
            //    continue;//valuta randomicamente o a seconda dei tagli se fare il crossover dell'elemento origin o no


            momGene = mom.get(index); //elemento del genoma A di cui fare il crossover
            dadGene = dad.get(index); //elemento del genoma B di cui fare il crossover


            if (mom.testSwap(index, dadGene) && dad.testSwap(index, momGene)) {
                mom.set(index, dadGene);
                dad.set(index, momGene);
            }
        }

        if (!mom.isFeasible())
            new CoyoteMutation().mutate(mom, 0);
        if (!dad.isFeasible())
            new CoyoteMutation().mutate(dad, 0);
    }
}
