import java.util.Random;
import java.util.concurrent.ForkJoinPool;

public class Main {
    public static void main (String [] args) {
        int[] array;
        int[] N = {10, 100, 1000, 10000};
        int[] threadCount = {1, 2, 4, 10};
        Random rand = new Random();
        Run[] runs = new Run[(N.length * threadCount.length) + N.length];

        // Parallel version
        int runCounter = 0;
        // For each value of N
        for (int value : N) {
            // For each number of threads in threadCount
            for (int i : threadCount) {
                array = new int[value];
                for (int k = 0; k < array.length; k++) {
                    // get a random number from 1-100
                    array[k] = rand.nextInt(100 - 1) + 1;
                }

                ParallelSum all = new ParallelSum(array, 0, array.length - 1);
                ForkJoinPool pool = new ForkJoinPool(i);

                long startTime = System.nanoTime(); // start timer
                // timed {
                pool.invoke(all);
                // }
                long endTime = System.nanoTime();   // end timer
                long duration = (endTime - startTime);

                Run run = new Run(runCounter + 1, value, "Parallel", i, duration);
                runs[runCounter++] = run;
            }
        }

        // Sequential version
        // For each value of N
        for (int value : N) {
            array = new int[value];
            for (int j = 0; j < array.length; j++) {
                // get a random number from 1-100
                array[j] = rand.nextInt(100 - 1) + 1;
            }
            // Number of threads for Sequential version is always 1.

            SequentialSum seqsum = new SequentialSum();

            long startTime = System.nanoTime(); // start timer
            // timed {
            seqsum.sum(array, 0, array.length - 1);
            // }
            long endTime = System.nanoTime();   // end timer
            long duration = (endTime - startTime);

            Run run = new Run(runCounter + 1, value, "Sequential", 1, duration);
            runs[runCounter++] = run;
        }

        // Find slowest run
        long slowest = runs[0].getTimeToComplete();
        for (Run run : runs) {
            if (run.getTimeToComplete() > slowest) slowest = run.getTimeToComplete();
        }

        // Calculate efficiency factors
        for (Run run : runs) {
            run.setEfficiencyFactor(slowest / run.getTimeToComplete());
        }

        // Print out a table of results
        System.out.println("Run | Array Size | Algorithm | Threads | Efficiency");
        for (Run run : runs) {
            System.out.println(" " + run.getId() + "  |     " + run.getArraySize() + "     | " + run.getAlgorithm() + "  |    " + run.getThreadsUsed() + "    |     " + run.getEfficiencyFactor());
        }
    }
}
