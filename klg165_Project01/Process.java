public class Process {
    public int ID;
    public double cpuServiceTime;
    public double diskServiceTime;
    public double arrTime;

    /**
     * Constructor for class Process
     *
     * @param ID            process ID. Used to set class variable ID.
     * @param cpuTime       process CPU service time. Sets class variable cpuServiceTime.
     * @param diskTime      process disk service time. Sets class variable diskServiceTime.
     * @param arrTime       Time process first arrived.
     */
    public Process(int ID, double cpuTime, double diskTime, double arrTime) {
        this.ID = ID;
        this.cpuServiceTime = cpuTime;
        this.diskServiceTime = diskTime;
        this.arrTime = arrTime;
    }

    /**
     * Accesses the current value of class variable ID.
     *
     * @return  the process ID.
     */
    public int getID () {
        return ID;
    }

    /**
     * Accesses the CPU service time of the process.
     *
     * @return the CPU service time.
     */
    public double getCpuTime() {
        return cpuServiceTime;
    }

    /**
     * Accesses the disk service time of the process.
     *
     * @return the disk service time.
     */
    public double getDiskTime() {
        return diskServiceTime;
    }

    /**
     * Accesses the process arrival time.
     *
     * @return time process first arrived.
     */
    public double getArrTime() {
        return arrTime;
    }
}