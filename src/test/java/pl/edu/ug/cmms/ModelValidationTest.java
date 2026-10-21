package pl.edu.ug.cmms;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ModelValidationTest {

    @Test
    void rejectsMachineWithBlankId() {
        assertThrows(IllegalArgumentException.class, () ->
                new Machine(
                        " ",
                        "Tokarka",
                        Machine.MachineType.CNC_LATHE,
                        "A",
                        LocalDate.of(2020, 1, 1)));
    }

    @Test
    void rejectsWorkOrderWithBlankId() {
        assertThrows(IllegalArgumentException.class, () ->
                new WorkOrder(
                        "",
                        "M-01",
                        WorkOrder.WorkOrderType.BREAKDOWN,
                        WorkOrder.Priority.HIGH,
                        WorkOrder.WorkOrderStatus.OPEN,
                        LocalDateTime.of(2026, 1, 1, 10, 0),
                        null));
    }

    @Test
    void rejectsWorkOrderWithBlankMachineId() {
        assertThrows(IllegalArgumentException.class, () ->
                new WorkOrder(
                        "WO-01",
                        " ",
                        WorkOrder.WorkOrderType.BREAKDOWN,
                        WorkOrder.Priority.HIGH,
                        WorkOrder.WorkOrderStatus.OPEN,
                        LocalDateTime.of(2026, 1, 1, 10, 0),
                        null));
    }

    @Test
    void rejectsDoneWorkOrderWithoutClosingDate() {
        assertThrows(IllegalArgumentException.class, () ->
                new WorkOrder(
                        "WO-01",
                        "M-01",
                        WorkOrder.WorkOrderType.BREAKDOWN,
                        WorkOrder.Priority.HIGH,
                        WorkOrder.WorkOrderStatus.DONE,
                        LocalDateTime.of(2026, 1, 1, 10, 0),
                        null));
    }

    @Test
    void rejectsClosingDateBeforeReportedDate() {
        assertThrows(IllegalArgumentException.class, () ->
                new WorkOrder(
                        "WO-01",
                        "M-01",
                        WorkOrder.WorkOrderType.BREAKDOWN,
                        WorkOrder.Priority.HIGH,
                        WorkOrder.WorkOrderStatus.DONE,
                        LocalDateTime.of(2026, 1, 1, 10, 0),
                        LocalDateTime.of(2026, 1, 1, 9, 59)));
    }
}
