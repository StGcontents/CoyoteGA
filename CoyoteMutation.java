import java.util.Date;
import java.util.Random;

/**
 * Created by stg on 02/12/2016.
 */
public class CoyoteMutation {
    private double probability;
    private double shrinkChance = 0.3;
    private final double INCREMENT_CHANCE = 0.8;

    public CoyoteMutation() {
        this(0.5);
    }

    public CoyoteMutation(double probability) {
        this.probability = probability;
    }

    public CoyoteMutation(boolean force) {
        this(0.5);
        if (force) shrinkChance = 0;
    }

    public void mutate(CoyoteChromosome chromosome, int generation) {
        Random token = new Random(new Date().getTime());

        int gene, geneOffset;
        int i, max = chromosome.getSize(), index;
        int mutated = 0;
        int offset = token.nextInt(max);
        int minMutations = chromosome.getRequestSize();

        if (chromosome.isFeasible()) {
            chromosome.discombobulate();
        }

        for (i = 0; i < max || !chromosome.isFeasible(); ++i) { //until feasible and mutated at least once
            index = (i + offset) % max;

            if (chromosome.isBlacklisted(index))
                continue; //l'elemento è inserito nella tabu list come elemento della diagonale e non può essere modificato!

            if (token.nextDouble() > probability)
                continue;//valuta randomicamente o a seconda dei tagli se fare il crossover dell'elemento origin o no

            gene = chromosome.get(index);

            if (!chromosome.testConstraint(index)) { //requests in dest not met, augment tasks
                if (token.nextDouble() > INCREMENT_CHANCE + (i / 10.0))
                    continue;
                //System.out.println("ASSIGNING UP TO " + (chromosome.getTetaChunk(index) + 1) + " TO " + index);
                //geneOffset = token.nextInt(token.nextDouble() > 0.2 ? chromosome.getTetaChunk(index) + 1 : chromosome.getTetaChunk(index) / 2 + 1);
                geneOffset = chromosome.getTetaChunk(index);
                if (geneOffset == 0)
                    continue;
                gene += geneOffset;
            }
            else {
                if (token.nextDouble() > shrinkChance - 0.2 * (chromosome.isFeasible() ? 1 : 0))
                    continue;
                geneOffset = Math.max(0, token.nextInt(chromosome.getShrinkable(index) + 1)); //chromosome.get(index) - token.nextInt(chromosome.get(index) + 1);
                if (geneOffset == 0)
                    continue;
                gene -= geneOffset;
            }

            if (chromosome.testSwap(index, gene)) {
                chromosome.set(index, gene);
                ++mutated;
            }
        }
    }
}
