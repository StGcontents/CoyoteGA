import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by stg on 02/12/2016.
 */
public class InputParser {
    BufferedReader reader, costsReader;
    CoyoteChromosome chromosome;
    int origin, dest, t, k;
    List<Integer[]> userTasks = new ArrayList<>();
    List<Integer> sparseDestinations = new ArrayList<>();
    List<Integer> sparseDestinationIndex = new ArrayList<>();
    List<List<List<Integer>>> costs = new ArrayList<>();
    int[][][] tetas;
    List<Integer> sparseTetas = new ArrayList<>();
    List<Integer[]> kMap = new ArrayList<>();

    
    public InputParser(String path) {
        try {
            reader = new BufferedReader(new FileReader(new File(path)));
            costsReader = new BufferedReader(new FileReader(new File(path)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void parseIndexes() throws Exception {
        String string = reader.readLine();
        String[] chunks = string.split(" ");
        origin = dest = Integer.parseInt(chunks[0]);
        t = Integer.parseInt(chunks[1]);
        k = Integer.parseInt(chunks[2]);
        reader.readLine();
    }

    private void parseUserType() throws Exception {
        String string = reader.readLine();
        String[] chunks = string.split(" ");
        for (int i = 0; i < k; ++i)
            userTasks.add(new Integer[] { i, Integer.parseInt(chunks[i]) });
        reader.readLine();
    }

    private void parseRequestAndCapacity() throws Exception{
        for (int i = 0; i < t * k * (origin + 1); ++i)
            reader.readLine();
        reader.readLine();
        String string = reader.readLine();
        String[] chunks = string.split(" ");
        int req;
        int index = 0;
        for (String s : chunks) {
            req = Integer.parseInt(s);
            if (req != 0) {
                sparseDestinations.add(req);
                sparseDestinationIndex.add(index);
            }
            ++index;
        }
        reader.readLine();

        tetas = new int[t][k][origin];
        for (int i = 0; i < t; ++i) {
            for (int j = 0; j < k; j++) {
                reader.readLine();
                string = reader.readLine();
                chunks = string.split(" ");
                for (int l = 0; l < origin; l++) {
                    tetas[i][j][l] = Integer.parseInt(chunks[l]);
                }
            }
        }

        int counter = 0;
        for (int i = 0; i < k; ++i) {
            kMap.add(new Integer[] {counter, userTasks.get(i)[1]});
            for (int j = 0; j < t; ++j) {
                int[] tts = tetas[j][i];
                for (int c : tts) {
                    if (c != 0) {
                        counter += sparseDestinations.size() + 1;
                        sparseTetas.add(c);
                    }
                }
            }
        }
    }

    private void parseCosts() throws Exception {
        reader.close();
        for (int i = 0; i < 4; ++i)
            costsReader.readLine();

        String string, chunks[];

        for (int i = 0; i < t; ++i) {
            for (int j = 0; j < k; ++j) {
                costsReader.readLine();
                ArrayList<List<Integer>> kList = new ArrayList();
                costs.add(kList);
                for (int l = 0; l < origin; l++) {
                    string = costsReader.readLine();
                    if (tetas[i][j][l] != 0) {
                        ArrayList<Integer> costSegment = new ArrayList<>();
                        chunks = string.split(" ");
                        for (int m : sparseDestinationIndex) {
                            int currentCost = m != l ? (int) Double.parseDouble(chunks[m]) :
                                    Integer.MAX_VALUE;
                            costSegment.add(currentCost);
                        }
                        kList.add(costSegment);
                    }
                }
            }
        }

        costsReader.close();
    }

    private void generateChromosome() {
        int[] costArray = new int[sparseTetas.size() * (sparseDestinations.size() + 1)];
        int counter = 0;
        for (int i = 0; i < costs.get(0).size(); ++i) {
            for (int j = 0; j < costs.size(); ++j) {
                List<Integer> sparseCosts = costs.get(j).get(i);
                costArray[counter] = Integer.MAX_VALUE;
                counter++;
                for (int c : sparseCosts) {
                    costArray[counter] = c;
                    ++counter;
                }
            }
        }
        ChromosomeFactory.instance(sparseTetas.size(), sparseDestinations.size(), kMap, costArray, sparseTetas.toArray(new Integer[sparseTetas.size()]), sparseDestinations.toArray(new Integer[sparseDestinations.size()]));
    }

    public CoyoteChromosome getChromosome() {
        try {
            parseIndexes();
            parseUserType();
            parseRequestAndCapacity();
            parseCosts();
            generateChromosome();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return ChromosomeFactory.instance();
    }
}
