import java.util.LinkedList;
import java.util.Queue;

public class SchedulerFCFS extends SchedulerBase implements Scheduler {
    private Platform platform;
    private LinkedList<Process> processes;

    public SchedulerFCFS(Platform p) {
        this.platform = p;
        this.processes = new LinkedList<>();
    }

    @Override
    public void notifyNewProcess(Process p) {
        this.processes.add(p);
    }

    @Override
    public Process update(Process cpu) {
        if (cpu == null) {
            if (this.processes.peekFirst() != null) {
                this.platform.log("Scheduled: " + this.processes.peekFirst().getName());
                return this.processes.remove();
            } else {
                return null;
            }
        } else if (cpu.isBurstComplete()) {
            this.contextSwitches += 2;
            this.platform.log("Process " + cpu.getName() + " burst complete");
            if (!cpu.isExecutionComplete()) {
                this.processes.add(cpu);
            } else {
                this.platform.log("Process " + cpu.getName() + " execution complete");
            }

            if (this.processes.peekFirst() != null) {
                this.platform.log("Scheduled: " + this.processes.peekFirst().getName());
                return this.processes.remove();
            } else {
                return null;
            }

        } else {
            return cpu;
        }
    }
}
