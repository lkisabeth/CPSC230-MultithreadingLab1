import java.util.Random;
import java.util.concurrent.ForkJoinPool;

public class main {
    public static void main (String [] args) {
        int[] array;
        int[] N = {10, 100, 1000, 10000};
        int[] threadCount = {1, 2, 4, 10};
        Random rand = new Random();
        Run[] runs = new Run[(N.length * threadCount.length) + N.length];

        // Parallel version
        int runCounter = 0;
        // For each value of N
        for (int i = 0; i<N.length; i++) {
            // For each number of threads in threadCount
            for (int j = 0; j < threadCount.length; j++) {
                array = new int[N[i]];
                for (int k = 0; k < array.length; k++) {
                    // get a random number from 1-100
                    array[k] = rand.nextInt(100 - 1) + 1;
                }

                ParallelSum all = new ParallelSum(array, 0, array.length-1);
                ForkJoinPool pool = new ForkJoinPool(threadCount[j]);

                long startTime = System.nanoTime(); // start timer
                // timed {
                pool.invoke(all);
                // }
                long endTime = System.nanoTime();   // end timer
                long duration = (endTime - startTime);

                Run run = new Run(runCounter+1, N[i], "Parallel", threadCount[j], duration);
                runs[runCounter++] = run;
            }
        }

        // Sequential version
        // For each value of N
        for (int i = 0; i<N.length; i++) {
            array = new int[N[i]];
            for (int j = 0; j < array.length; j++) {
                // get a random number from 1-100
                array[j] = rand.nextInt(100 - 1) + 1;
            }
            // Number of threads for Sequential version is always 1.

            SequentialSum seqsum = new SequentialSum();

            long startTime = System.nanoTime(); // start timer
            // timed {
            seqsum.sum(array, 0, array.length-1);
            // }
            long endTime = System.nanoTime();   // end timer
            long duration = (endTime - startTime);

            Run run = new Run(runCounter+1, N[i], "Sequential", 1, duration);
            runs[runCounter++] = run;
        }

        // Find slowest run
        long slowest = runs[0].getTimeToComplete();
        for (int i = 0; i < runs.length; i++) {
            if (runs[i].getTimeToComplete() > slowest) slowest = runs[i].getTimeToComplete();
        }

        // Calculate efficiency factors
        for (int i = 0; i < runs.length; i++) {
            runs[i].setEfficiencyFactor(slowest/runs[i].getTimeToComplete());
        }

        // Print out a table of results
        for (int i = 0; i < runs.length; i++) {
            System.out.println("Run " + runs[i].getId() + " | Array Size: " + runs[i].getArraySize() + " | Algorithm: " + runs[i].getAlgorithm() + " | # of Threads: " + runs[i].getThreadsUsed() + " | Efficiency Factor: " + runs[i].getEfficiencyFactor());
        }
    }
}
