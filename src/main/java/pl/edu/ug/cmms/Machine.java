package pl.edu.ug.cmms;

import java.time.LocalDate;
import java.util.Objects;

public record Machine(String id, String name, MachineType type, String hall, LocalDate installationDate) {

    public Machine {
        requireText(id, "id");
        requireText(name, "name");
        Objects.requireNonNull(type, "type");
        requireText(hall, "hall");
        Objects.requireNonNull(installationDate, "installationDate");
    }

    public enum MachineType {
        CNC_LATHE,
        CONVEYOR,
        MILLING_MACHINE,
        WELDING_ROBOT,
        INJECTION_MOLDER,
        HYDRAULIC_PRESS,
        COMPRESSOR
    }

    private static void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " nie może być puste");
        }
    }
}
