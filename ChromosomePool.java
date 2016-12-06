import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by stg on 02/12/2016.
 */
public class ChromosomePool {

    static boolean timeout = false;
    int maxSize;
    int bestFitness = Integer.MAX_VALUE;
    List<CoyoteChromosome> pool;
    int[] bestGenes, requests;

    public ChromosomePool(int size, CoyoteChromosome chromosome) {
        maxSize = size;
        pool = new ArrayList<>();

        //patientZero = chromosome.clone();
        //new CoyoteMutation().mutate(chromosome, 0);
        //pool.add(chromosome);
    }

    public void evolve(long limit) {

        //Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        //Thread evolvingThread = new Thread(() -> manageGenerations());
        //evolvingThread.setPriority(Thread.MIN_PRIORITY);
        //evolvingThread.run();
        Date start = new Date();
        manageGenerations();
        Date end = new Date();
        System.out.println("MILLISECONDS: " + (end.getTime() - start.getTime()));
        //try { Thread.sleep(limit); }
        //catch (InterruptedException e) { e.printStackTrace(); }

        //timeout = true;
        System.out.println("BEST FITNESS: " + bestFitness);
        int fitness = 0;
        int[] costs = pool.get(0).getCosts();
        for (int i = 0; i < bestGenes.length; ++i ) {
            if (costs[i] != Integer.MAX_VALUE)
                fitness += bestGenes[i] * costs[i];
        }
        System.out.println("CHECK FITNESS: " + fitness);

        for (int e : bestGenes) {
            System.out.print(e);
            System.out.print(" | ");
        }
        System.out.println();
        for (int e : requests) {
            System.out.print(e);
            System.out.print(" | ");
        }
    }

    private void manageGenerations() {
        Random random = new Random(new Date().getTime());
        double asexProb = 1;
        double mutationProb = 0.7;
        double asexChance, mutationChance;
        CoyoteChromosome chosenOne0, chosenOne1, newChromosome0, newChromosome1, temp[];
        int i = 0;
        for (;;) {
            if (i > 15000) return;
            System.out.println("GENERATION " + i);
            newChromosome1 = null;
            asexChance = random.nextDouble();
            if (pool.size() < maxSize / 2 || asexChance < asexProb - i / 100.0) {
                System.out.println("ASEXUAL");
                newChromosome0 = ChromosomeFactory.instance();
                new CoyoteMutation().mutate(newChromosome0, i);
                pool.add(newChromosome0);
                updateBest(newChromosome0);
            }
            else {
                chosenOne0 = pool.get(random.nextInt(pool.size()));
                mutationChance = random.nextDouble();
                if (mutationChance < 0.5) {//mutationProb - (currentBest == null ? 0 : currentBest.getFitness() / (double) chosenOne0.getFitness())) {
                    System.out.println("MUTATION");
                    newChromosome0 = mutation(chosenOne0, i);
                    if (newChromosome0.getFitness() < 0)
                        pool.remove(newChromosome0);
                    else updateBest(newChromosome0);
                }
                else {
                    System.out.println("CROSSOVER");
                    chosenOne1 = pool.get(random.nextInt(pool.size()));
                    if (chosenOne1 == null)
                        chosenOne1 = pool.get(random.nextInt(pool.size()));

                    temp = sexualReproduction(chosenOne0, chosenOne1, i);
                    newChromosome0 = temp[0];
                    updateBest(newChromosome0);
                    newChromosome1 = temp[1];
                    updateBest(newChromosome1);
                    //updateBest(newChromosome0, newChromosome1);
                }
            }
            prune();
            ++i;
        }
    }

    private void updateBest(CoyoteChromosome... chromosomes) {
        for (CoyoteChromosome ch : chromosomes) {
            if (ch != null) {
                System.out.println("SOLUTION FOUND: " + ch.getFitness());
            }
            if (bestFitness > ch.getFitness()) {
                bestFitness = ch.getFitness();
                bestGenes = ch.export();
                requests = ch.exportRequests();
            }
        }
    }

    private void prune() {
        System.out.println("POP SIZE: " + pool.size());
        CoyoteChromosome unfitChromosome = pool.get(0);
        CoyoteChromosome currentChromosome;
        int target;
        while (pool.size() > maxSize) {
            target = 0;
            for (int i = 1; i < pool.size(); ++i) {
                currentChromosome = pool.get(i);
                if (unfitChromosome.getFitness() < currentChromosome.getFitness()) {
                    unfitChromosome = currentChromosome;
                    target = i;
                }
            }
            pool.remove(target);
        }
    }

    private CoyoteChromosome asexualReproduction(int index, int generation) {
        return asexualReproduction(pool.get(index), generation);
    }

    private CoyoteChromosome asexualReproduction(CoyoteChromosome chromosome, int generation) {
        CoyoteChromosome clone = chromosome.clone();
        pool.add(clone);
        new CoyoteMutation().mutate(clone, generation);
        return clone;
    }

    private CoyoteChromosome[] sexualReproduction(int iMom, int iDad, int generation) {
        return sexualReproduction(pool.get(iMom), pool.get(iDad), generation);
    }

    private CoyoteChromosome[] sexualReproduction(CoyoteChromosome mom, CoyoteChromosome dad, int generation) {
        /*CoyoteChromosome momClone = mom.clone();
        CoyoteChromosome dadClone = dad.clone();
        pool.add(momClone);
        pool.add(dadClone);
        new CoyoteCrossover().crossover(momClone, dadClone);*/
        /*if (!momClone.isFeasible())
            new CoyoteMutation(true).mutate(momClone, generation);
        if (!dadClone.isFeasible())
            new CoyoteMutation(true).mutate(dadClone, generation);*/
        new CoyoteCrossover().crossover(mom, dad);
        return new CoyoteChromosome[] {mom, dad};
    }

    private CoyoteChromosome mutation(int index, int generation) {
        return mutation(pool.get(index), generation);
    }

    private CoyoteChromosome mutation(CoyoteChromosome chromosome, int generation) {
        new CoyoteMutation().mutate(chromosome, generation);
        return chromosome;
    }
}
