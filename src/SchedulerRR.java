import java.util.LinkedList;

public class SchedulerRR extends SchedulerBase implements Scheduler {
    private Platform platform;
    private LinkedList<Process> processes;
    private int quantum;

    public SchedulerRR(Platform p, int cpu) {
        this.platform = p;
        this.processes = new LinkedList<>();
        this.quantum = cpu;
    }

    @Override
    public void notifyNewProcess(Process p) {
        this.processes.add(p);
    }

    @Override
    public Process update(Process cpu) {
        if(cpu == null) {
            return this.scheduleFirstOrNull();
        } else {
            if (cpu.isBurstComplete()) {
                this.contextSwitches += 2;
                this.platform.log("Process " + cpu.getName() + " burst complete");
                if (cpu.isExecutionComplete()) {
                    this.platform.log("Process " + cpu.getName() + " execution complete");
                } else {
                    this.processes.add(cpu);
                }
                return this.scheduleFirstOrNull();
            } else if (cpu.getElapsedBurst() != 0 && (cpu.getElapsedBurst() % quantum) == 0) {
                this.contextSwitches += 2;
                this.platform.log("Time Quantum complete for " + cpu.getName());
                this.processes.add(cpu);
                return this.scheduleFirstOrNull();
            } else {
                return cpu;
            }
        }
    }

    private Process scheduleFirstOrNull() {
        if (this.processes.peekFirst() != null) {
            this.platform.log("Scheduled: " + this.processes.peekFirst().getName());
            return this.processes.remove();
        } else {
            return null;
        }
    }
}
