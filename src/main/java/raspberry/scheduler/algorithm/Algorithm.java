package raspberry.scheduler.algorithm;

/**
 * Represent the scheduling algorithm
 * @Author Takahiro
 */
public interface Algorithm {
    /**
     * Finds a valid and optimal solution given with specified parameters
     * in constructor
     * @return outputSchedule a schedule that represent the result
     */
    OutputSchedule findPath();

}
