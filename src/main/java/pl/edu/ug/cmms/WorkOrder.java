package pl.edu.ug.cmms;

import java.time.LocalDateTime;
import java.util.Objects;

public record WorkOrder(
        String id,
        String machineId,
        WorkOrderType type,
        Priority priority,
        WorkOrderStatus status,
        LocalDateTime reportedAt,
        LocalDateTime closedAt) {

    public WorkOrder {
        requireText(id, "id");
        requireText(machineId, "machineId");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(priority, "priority");
        Objects.requireNonNull(status, "status");
        Objects.requireNonNull(reportedAt, "reportedAt");

        if (status == WorkOrderStatus.DONE && closedAt == null) {
            throw new IllegalArgumentException("Zrobiona praca musi mieć ustawioną datę zamknięcia");
        }
        if (closedAt != null && closedAt.isBefore(reportedAt)) {
            throw new IllegalArgumentException("zamknięcie nie może być przed zgłoszeniem");
        }
    }

    public enum Priority {
        LOW,
        NORMAL,
        HIGH,
        CRITICAL
    }

    public enum WorkOrderStatus {
        OPEN,
        IN_PROGRESS,
        DONE,
        CANCELLED
    }

    public enum WorkOrderType {
        PREVENTIVE,
        INSPECTION,
        BREAKDOWN
    }

    private static void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " nie może być puste");
        }
    }
}
