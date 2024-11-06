package ga;

import java.util.Arrays;
import java.util.Random;

public class GATSP {

    public static void main(String[] args) {
        double[][] adjacency = {
            {0, 2, 6, 4, 3, 3},
            {2, 0, 3, 4, 5, 2},
            {6, 3, 0, 4, 7, 5},
            {4, 4, 4, 0, 2, 3},
            {3, 5, 7, 2, 0, 1},
            {3, 2, 5, 3, 1, 0}
        };

        // PARAMETERS ----------------------------------------------------------
        int popSize = 8;
        int numSelectedIndividual = 3;
        int maxGeneration = 100;
        double mutationRate = 0.5;
        int nVertex = adjacency.length;
        
        // Best Solution
        int[] bestSolution = null;
        double bestFitness = Double.MIN_VALUE;

        // PEMBANGKITAN POPULASI AWAL SECARA RANDOM ----------------------------
        int[][] population = new int[popSize][];
        double[] distance = new double[popSize];
        double[] fitness = new double[popSize];
        // variabel ini dibuat untuk membuat persis initial populasi
        int[][] solusiGlobal = new int[popSize][];


        for (int i = 0; i < popSize; i++) {
            int[] individual = randomChromosome(nVertex, 2);
            population[i] = individual;
            distance[i] = calculateDistnce(individual, adjacency);
            fitness[i] = 1.0 / distance[i];
            solusiGlobal[i] = individual;
            if (fitness[i] > bestFitness) {
                bestFitness = fitness[i];
                bestSolution = individual;
            }
        }

        System.out.println("-------------------------------------------------");
        System.out.println("INITIAL POPULATION");
        for (int i = 0; i < popSize; i++) {
            System.out.print("[");
            for (int j = 0; j < population[i].length; j++) {
                if (j > 0) {
                    System.out.print(", ");
                }
                System.out.print("V" + population[i][j]);
            }
            System.out.print("] = ");
            System.out.println(fitness[i]);
        }
        
        //MEMBUAT PERSIS INITIAL POPULATION MENGGUNAKAN solusiGlobal
        System.out.println("-------------------------------------------------");
        for (int i = 0; i < popSize; i++) {
            System.out.print("[");
            for (int j = 0; j < solusiGlobal[i].length; j++) {
                if (j > 0) {
                    System.out.print(", ");
                }
                System.out.print("V"+solusiGlobal[i][j]);
            }
            System.out.print("] = ");
            //System.out.println(Arrays.toString(solusiGlobal[i]));
            System.out.println(fitness[i]);
        }
        
        System.out.println("-------------------------------------------------");
        System.out.println("ELITISM");
        System.out.print("[");
        for (int j = 0; j < bestSolution.length; j++) {
            if (j > 0) {
                System.out.print(", ");
            }
            System.out.print("V" + bestSolution[j]);
        }
        System.out.print("] = ");
        System.out.println(bestFitness);

        //EVOLUTION ------------------------------------------------------------
        for (int g = 1; g <= maxGeneration; g++) {
            //Selection Using Tournament ---------------------------------------
            double[][] indexFitness = new double[popSize][2];
            for (int i = 0; i < popSize; i++) {
                indexFitness[i][0] = i;
                indexFitness[i][1] = fitness[i];
            }
            // Sorting Fitness
            for (int i = 0; i < indexFitness.length - 1; i++) {
                double max = indexFitness[i][1];
                int iMax = i;
                for (int j = i + 1; j < indexFitness.length; j++) {
                    if (indexFitness[j][1] > max) {
                        max = indexFitness[j][1];
                        iMax = j;
                    }
                }
                if (iMax > i) {
                    double temp0 = indexFitness[i][0];
                    double temp1 = indexFitness[i][1];
                    indexFitness[i][0] = indexFitness[iMax][0];
                    indexFitness[i][1] = indexFitness[iMax][1];
                    indexFitness[iMax][0] = temp0;
                    indexFitness[iMax][1] = temp1;
                }
            }
            int[][] population1 = new int[popSize][];
            for (int i = 0; i < popSize; i++) {
                int index = (int) indexFitness[i][0];
                population1[i] = population[index].clone();
                // System.out.println(Arrays.toString(population1[i])+ " = " + fitness1[i]);
            }

            // Tournament ------------------------------------------------------
            int[][] population2 = new int[popSize][nVertex + 1];
            for (int i = 0; i < numSelectedIndividual; i++) {
                population2[i] = population1[i];
            }

//            //TEST PRINT SEBELUM CROSSOVER ---------------------------------------
//            System.out.println("SEBELUM CROSSOVER -----------------------------");
//            for (int i = 0; i < population2.length; i++) {
//                System.out.println(Arrays.toString(population2[i]));
//            }
            // Crossover -------------------------------------------------------
            int k = numSelectedIndividual;
            while (k < popSize) {
                int indexParent1 = randomInteger(0, numSelectedIndividual - 1);
                int indexParent2 = indexParent1;
                while (indexParent1 == indexParent2) {
                    indexParent2 = randomInteger(0, numSelectedIndividual - 1);
                }
                int[] parent1 = population2[indexParent1];
                int[] parent2 = population2[indexParent2];

                int[] ofspring1 = parent1.clone();
                int[] ofspring2 = parent2.clone();

                //Random Crossover point
                int point1 = randomInteger(1, nVertex-1);
                int point2 = point1;
                while (point2 == point1) {
                    point2 = randomInteger(1, nVertex-1);
                }
                if (point1 > point2) {
                    int temp = point1;
                    point1 = point2;
                    point2 = temp;
                }

                // PMX
                for (int i = point1; i <= point2; i++) {
                    int value1 = ofspring1[i];
                    int value2 = ofspring2[i];

                    for (int j = 1; j <= nVertex; j++) {
                        if (ofspring1[j] == value2) {
                            ofspring1[j] = value1;
                            ofspring1[i] = value2;
                            break;
                        }
                    }

                    for (int j = 1; j <= nVertex; j++) {
                        if (ofspring2[j] == value1) {
                            ofspring2[j] = value2;
                            ofspring2[i] = value1;
                        }
                    }
                }

                // set ofspring as new individual on population ----------------
                if (k < population2.length) {
                    population2[k] = ofspring1;
                    k++;
                }
                if (k < population2.length) {
                    population2[k] = ofspring2;
                    k++;
                }
            }

//            //TEST PRINT HASIL CROSSOVER ---------------------------------------
//            System.out.println("HASIL CROSSOVER -----------------------------");
//            for (int i = 0; i < population2.length; i++) {
//                System.out.println(Arrays.toString(population2[i]));
//            }
            // Mutation -------------------------------------------------------
            for (int i = 0; i < popSize; i++) {
                double rm = new Random().nextDouble();
                if (rm > mutationRate) {
                    int point1 = randomInteger(1, nVertex-1);
                    int point2 = point1;
                    while (point2 == point1) {
                        point2 = randomInteger(1, nVertex-1);
                    }

                    // SWAP
                    int temp = population2[i][point1];
                    population2[i][point1] = population2[i][point2];
                    population2[i][point2] = temp;
                }
            }

            // Set New Population, Hitung fitness dan Elitism
            for (int i = 0; i < popSize; i++) {
                population[i] = population2[i];
                distance[i] = calculateDistnce(population[i], adjacency);
                fitness[i] = 1.0 / distance[i];
                if (fitness[i] > bestFitness) {
                    bestFitness = fitness[i];
                    bestSolution = population[i];
                }
            }

        } //END OF EVOLUTION

        System.out.println("=================================================");
        System.out.println("BEST INDIVIDUAL");
        System.out.println(Arrays.toString(bestSolution));
        System.out.println("Fitness = " + bestFitness);

    }

    public static int[] randomChromosome(int nVertex, int depot) {
        int[] chromosome = null;
        if (nVertex > 0) {
            chromosome = new int[nVertex + 1];
            chromosome[0] = depot;
            chromosome[nVertex] = depot;
            for (int i = 1; i < nVertex; i++) {
                boolean unique = false;
                while (!unique) {
                    unique = true;
                    int r = randomInteger(0, nVertex - 1);
                    for (int j = 0; j < i; j++) {
                        if (r == chromosome[j]) {
                            unique = false;
                            break;
                        }
                    }

                    if (unique) {
                        chromosome[i] = r;
                    }
                }
            }
        }
        return chromosome;
    }

    public static int randomInteger(int min, int max) {
        if (min > max) {
            int temp = min;
            min = max;
            max = temp;
        }
        Random random = new Random();
        int r = min + random.nextInt(max - min + 1);
        return r;
    }

    public static double calculateDistnce(int[] solusi, double[][] jarak) {
        double totalJarak = 0;
        int v0 = solusi[0];
        for (int i = 1; i < solusi.length; i++) {
            int v1 = solusi[i];
            double total = jarak[v0][v1];
            totalJarak += total;
            v0 = v1;
        }

        return totalJarak;

    }

}
