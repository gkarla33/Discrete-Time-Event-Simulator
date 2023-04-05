import java.text.DecimalFormat;
import java.util.*;

public class Simulator {
    double avgServiceTimeCPU;
    double avgServiceTimeDisk;
    int lambda;

    boolean CPUIdle = true, diskIdle = true;
    double clock = 0;
    int ID = 0;

    double timeRunningCPU = 0;   // total time CPU is busy.
    double timeRunningDisk = 0;  // total time Disk is busy.
    int ttlReadyQueue = 0;       // Total number of processes in the Ready queue after each clock update.
    int ttlDiskQueue = 0;        // Total number of processes in the Disk queue after each clock update.
    double ttlTurnaround = 0;

    int MAX_PROCESSES = 10000;        // Max processes to be completed
    int processesCompleted = 0;     // Current amount of processes completed
    int steps = 0;

    LinkedList<Process> ProcessReadyQueue = new LinkedList<>();
    LinkedList<Process> DiskQueue = new LinkedList<>();
    LinkedList<Event> EventQueue = new LinkedList<>();

    /**
     * Constructor without arguments. Outputs brief information about the required arguments.
     */
    public Simulator() {
        System.out.println("\nAverage Arrival Rate (lambda):  The rate at which new processes will arrive.");
        System.out.println("Average CPU Service Time:       Used to generate the CPU service time for each process.");
        System.out.println("Average Disk Service Time:      Used to generate the Disk service time for each process.\n");

        System.out.println("Enter arguments as: java Simulator <avgArrivalRate avgServiceTimeCPU avgServiceTimeDisk>\n");
    }

    /**
     * Sets the averages needed to run the discrete-time simulator
     *
     * @param avgArrivalRate     average arrival rate, lambda. Used to generate the processes' arrival rates.
     * @param avgServiceTimeCPU  the CPU's average service time. Used to generate processing times in the CPU.
     * @param avgServiceTimeDisk the disk's average service time. Used to generate processing times in the disk.
     */
    public Simulator(int avgArrivalRate, double avgServiceTimeCPU, double avgServiceTimeDisk) {
        this.lambda = avgArrivalRate;
        this.avgServiceTimeCPU = avgServiceTimeCPU;
        this.avgServiceTimeDisk = avgServiceTimeDisk;
    }

    /**
     * Generates a new process, including its ID and arrival rate, and adds it to the event queue.
     */
    void generateProcess() {
        // Generate values following a Poisson distribution by using exponential formula.
        double arrivalRate = (-1.0 / lambda) * Math.log(genRandom());
        double serviceTimeCPU = (-1.0 / avgServiceTimeCPU) * Math.log(genRandom());
        double serviceTimeDisk = (-1.0 / avgServiceTimeDisk) * Math.log(genRandom());

        double arrivalTime = clock + arrivalRate;

        Process pr = new Process(ID, serviceTimeCPU, serviceTimeDisk, arrivalTime);  // Create process using instance of Process class
        ProcessReadyQueue.add(pr);
        ID++;
    }

    /**
     * Generates a random number between zero and one.
     *
     * @return a random number.
     */
    double genRandom() {
        Random rand = new Random();
        double randNum = 0;

        while (randNum == 0) {           // If random number is equal to 0, generate a random number again
            randNum = rand.nextDouble();
        }

        return randNum;
    }

    /**
     * Handles the processes arriving to the CPU.
     *
     * @param pr the process arriving to the CPU.
     */
    void handleArrivalCPU(Process pr) {
        if (CPUIdle == true) {  // CPU idle. Processing.
            this.CPUIdle = false;   //set CPU to busy
            double CPUServiceTime = pr.getCpuTime();    // Get processing time for current process

            this.clock += CPUServiceTime;       // update clock variable to time after processing
            timeRunningCPU += CPUServiceTime;   // update total time CPU has been busy

            Event event = new Event(clock, 2, pr);     // Schedule Departure
            EventQueue.add(event);                          // Add departure to Event Queue
        }
        else {
            ProcessReadyQueue.add(pr);      // CPU busy. Send process to Ready Queue.
        }
    }

    /**
     * Handles departure from CPU. Decides whether process exits or enters disk queue.
     *
     * @param pr the process leaving the CPU.
     */
    void handleDepartureCPU(Process pr) {
        double coinFlip = genRandom();
        double time = clock;

        // Use coinFlip variable to determine whether process completely exits or processes in Disk.
        if (coinFlip <= 0.4) {
            DiskQueue.add(pr);
        }
        else {  // Process exits simulator.
            processesCompleted++;

            this.ttlTurnaround += (time - pr.getArrTime());    // Add turnaround time to ttlTurnaround
        }

        this.CPUIdle = true;
    }

    /**
     * Handles processes arriving to the disk.
     *
     * @param pr Process arriving to disk.
     **/
    void handleArrivalDisk(Process pr) {
        double diskServiceTime = pr.getDiskTime();

        if (diskIdle == true) {
            this.diskIdle = false;
            this.clock += diskServiceTime;      // add time to clock
            timeRunningDisk += diskServiceTime; // update total time disks has been busy

            Event event = new Event(clock, 4, pr);  // Schedule disk departure
            EventQueue.add(event);
        } else {  // Disk busy. Add process to disk queue.
            DiskQueue.add(pr);
        }
    }

    /**
     * Handles processes leaving the disk and checks for processes in the Disk Queue.
     *
     * @param pr the process leaving the disk.
     */
    void handleDepartureDisk(Process pr) {
        ProcessReadyQueue.add(pr);      // Process is sent back to the CPU Ready Queue

        this.diskIdle = true;
    }

    /**
     * Outputs the results of each experiment
     */
    void outputMetrics() {
        DecimalFormat percent = new DecimalFormat("#.##%");
        DecimalFormat df = new DecimalFormat("#.###");

        double finalTime = clock;
        double timeByMin = finalTime / 60.0;

        double avgTurnaround = ttlTurnaround / (double)processesCompleted;
        double avgUtilCPU = timeRunningCPU / finalTime;
        double avgUtilDisk = timeRunningDisk / finalTime;

        System.out.println("\nCompletion Time = " + finalTime);
        System.out.println("\nAverage Turnaround Time: " + df.format(avgTurnaround));
        System.out.println("Average Throughput: " + df.format((double) processesCompleted / timeByMin));
        System.out.println("Average CPU Utilization: " + percent.format(avgUtilCPU));
        System.out.println("Average Disk Utilization: " + percent.format(avgUtilDisk));

        System.out.println("Average Number of Processes in CPU Ready Queue: " +
                df.format((double)ttlReadyQueue / steps));
        System.out.println("Average Number of Processes in Disk Queue: " +
                df.format((double)ttlDiskQueue / steps) + "\n");
    }

    /**
     * Starts simulation
     */
    void runSimulator() {
        int eventType;

        while (processesCompleted != MAX_PROCESSES) {
            // If there is a process in the Ready Queue, create a CPU arrival event.
            if (!ProcessReadyQueue.isEmpty()) {
                Event event = new Event(clock, 1, ProcessReadyQueue.remove());   // CPU Arrival scheduled.
                EventQueue.add(event);
            }

            // If there is a process in the Disk Queue, create a CPU arrival event.
            if (!DiskQueue.isEmpty()) {     // Schedule arrival of next process in disk queue
                Event event = new Event(clock, 3, DiskQueue.remove());
                EventQueue.add(event);
            }

            if (!EventQueue.isEmpty()) {
                Event event = EventQueue.remove();
                eventType = event.getEventType();

                switch (eventType) {
                    case 1:    // CPU Arrival
                        handleArrivalCPU(event.getProcess());
                        break;
                    case 2:    // CPU Departure
                        handleDepartureCPU(event.getProcess());
                        break;
                    case 3:    // Disk Arrival
                        handleArrivalDisk(event.getProcess());
                        break;
                    case 4:    // Disk Departure
                        handleDepartureDisk(event.getProcess());
                        break;
                    default:
                        break;
                }
            }

            if(EventQueue.size() < 5) {
                generateProcess();
            }

            steps++;
            this.ttlReadyQueue += ProcessReadyQueue.size();     // Record current number of processes in Ready Queue
            this.ttlDiskQueue += DiskQueue.size();      // Record current number of processes in Disk Queue
        }
        outputMetrics();
    }

    public static void main(String[] args) {
        Simulator sim;
        if (args.length <= 0) {
            sim = new Simulator();
        }
        else {
            double AVG_CPU_SERV_TIME = Double.parseDouble(args[0]);
            double AVG_DISK_SERV_TIME = Double.parseDouble(args[1]);
            int lambda = Integer.parseInt(args[2]);
            sim = new Simulator(lambda, AVG_CPU_SERV_TIME, AVG_DISK_SERV_TIME);
            sim.runSimulator();
        }
    }
}