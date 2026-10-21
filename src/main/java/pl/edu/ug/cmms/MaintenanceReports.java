package pl.edu.ug.cmms;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.time.Duration;
import java.util.HashMap;
import java.util.TreeMap;

import pl.edu.ug.cmms.WorkOrder.Priority;
import pl.edu.ug.cmms.WorkOrder.WorkOrderStatus;
import pl.edu.ug.cmms.WorkOrder.WorkOrderType;

public final class MaintenanceReports {

    private MaintenanceReports() {
    }

    public static void printOpenOrdersByPriority(List<WorkOrder> workOrders) {
        Map<Priority, Long> counts = new EnumMap<>(Priority.class);

        for (Priority priority : Priority.values()) {
            counts.put(priority, 0L);
        }

        for (WorkOrder order : workOrders) {
            if (order.status() == WorkOrderStatus.OPEN
                    || order.status() == WorkOrderStatus.IN_PROGRESS) {
                counts.merge(order.priority(), 1L, Long::sum);
            }
        }

        System.out.println("Niezakończone zlecenia według priorytetu:");
        for (Priority priority : Priority.values()) {
            System.out.printf("%s: %d%n", priority, counts.get(priority));
        }
    }

    public static void printTopMachinesByBreakdowns(
            List<Machine> machines,
            List<WorkOrder> workOrders) {

        List<MachineBreakdownCount> topMachines = machines.stream()
                .map(machine -> new MachineBreakdownCount(
                        machine,
                        workOrders.stream()
                                .filter(order -> order.machineId().equals(machine.id()))
                                .filter(order -> order.type() == WorkOrderType.BREAKDOWN)
                                .filter(order -> order.status() != WorkOrderStatus.CANCELLED)
                                .count()))
                .sorted(Comparator
                        .comparingLong(MachineBreakdownCount::count)
                        .reversed()
                        .thenComparing(result -> result.machine().id()))
                .limit(3)
                .toList();

        System.out.println("Trzy maszyny z największą liczbą awarii:");
        for (MachineBreakdownCount result : topMachines) {
            System.out.printf("%s (%s): %d%n",
                    result.machine().id(),
                    result.machine().name(),
                    result.count());
        }
    }

    private record MachineBreakdownCount(Machine machine, long count) {
    }

    public static Map<String, Duration> calculateMttrByHall(
        List<Machine> machines,
        List<WorkOrder> workOrders) {

    Map<String, String> hallByMachineId = new HashMap<>();
    for (Machine machine : machines) {
        hallByMachineId.put(machine.id(), machine.hall());
    }

    Map<String, Duration> totalDurationByHall = new TreeMap<>();
    Map<String, Long> orderCountByHall = new HashMap<>();

    for (WorkOrder order : workOrders) {
        if (order.type() != WorkOrderType.BREAKDOWN
                || order.status() != WorkOrderStatus.DONE) {
            continue;
        }

        String hall = hallByMachineId.get(order.machineId());
        if (hall == null) {
            throw new IllegalArgumentException(
                    "Nie znaleziono maszyny o id: " + order.machineId());
        }

        Duration duration = Duration.between(order.reportedAt(), order.closedAt());
        totalDurationByHall.merge(hall, duration, Duration::plus);
        orderCountByHall.merge(hall, 1L, Long::sum);
    }

    Map<String, Duration> averageByHall = new TreeMap<>();
    for (Map.Entry<String, Duration> entry : totalDurationByHall.entrySet()) {
        long orderCount = orderCountByHall.get(entry.getKey());
        averageByHall.put(entry.getKey(), entry.getValue().dividedBy(orderCount));
    }

    return Map.copyOf(averageByHall);
}

    public static void printMttrByHall(
            List<Machine> machines,
            List<WorkOrder> workOrders) {

        Map<String, Duration> mttrByHall = calculateMttrByHall(machines, workOrders);

        System.out.println("MTTR zakończonych awarii według hal:");

        machines.stream()
                .map(Machine::hall)
                .distinct()
                .sorted()
                .forEach(hall -> {
                    Duration mttr = mttrByHall.get(hall);

                    if (mttr == null) {
                        System.out.printf("%s: brak zakończonych awarii%n", hall);
                        return;
                    }

                    long totalMinutes = mttr.toMinutes();
                    long hours = totalMinutes / 60;
                    long minutes = totalMinutes % 60;

                    System.out.printf("%s: %d godz. %d min%n", hall, hours, minutes);
                });
    }
}