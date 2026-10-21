package pl.edu.ug.cmms;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MaintenanceReportsTest {

    @Test
    void calculatesMttrForCompletedBreakdownsByHall() {
        List<Machine> machines = List.of(
                new Machine(
                        "M-01",
                        "Maszyna A",
                        Machine.MachineType.CNC_LATHE,
                        "A",
                        LocalDate.of(2020, 1, 1)),
                new Machine(
                        "M-02",
                        "Maszyna B",
                        Machine.MachineType.CONVEYOR,
                        "B",
                        LocalDate.of(2020, 1, 1)));

        List<WorkOrder> workOrders = List.of(
                order("WO-01", "M-01", WorkOrder.WorkOrderStatus.DONE,
                        "2026-01-01T08:00", "2026-01-01T10:10:40"),
                order("WO-02", "M-01", WorkOrder.WorkOrderStatus.DONE,
                        "2026-01-02T08:00", "2026-01-02T09:29:20"),
                order("WO-03", "M-02", WorkOrder.WorkOrderStatus.DONE,
                        "2026-01-03T08:00", "2026-01-03T08:35:59"),
                order("WO-04", "M-01", WorkOrder.WorkOrderStatus.CANCELLED,
                        "2026-01-04T08:00", "2026-01-04T18:00"),
                order("WO-05", "M-02", WorkOrder.WorkOrderStatus.OPEN,
                        "2026-01-05T08:00", null));

        Map<String, Duration> result =
                MaintenanceReports.calculateMttrByHall(machines, workOrders);

        assertEquals(Duration.ofMinutes(110), result.get("A"));
        assertEquals(Duration.ofSeconds(35 * 60 + 59), result.get("B"));
    }

    private static WorkOrder order(
            String id,
            String machineId,
            WorkOrder.WorkOrderStatus status,
            String reportedAt,
            String closedAt) {

        return new WorkOrder(
                id,
                machineId,
                WorkOrder.WorkOrderType.BREAKDOWN,
                WorkOrder.Priority.HIGH,
                status,
                LocalDateTime.parse(reportedAt),
                closedAt == null ? null : LocalDateTime.parse(closedAt));
    }
}
