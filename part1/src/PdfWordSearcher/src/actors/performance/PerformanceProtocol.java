package actors.performance;

public interface PerformanceProtocol {
    record PerformMessage() implements PerformanceProtocol {}
    record KillMessage() implements PerformanceProtocol {}
}
