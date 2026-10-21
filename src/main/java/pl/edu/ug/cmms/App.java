package pl.edu.ug.cmms;

import java.util.List;

public class App {
    public static void main(String[] args) {
        List<Machine> machines = SampleData.machines();
        List<WorkOrder> workOrders = SampleData.workOrders();

        System.out.printf("Maszyny (%d):%n", machines.size());
        machines.forEach(System.out::println);
        System.out.println();

        System.out.printf("Zlecenia serwisowe (%d):%n", workOrders.size());
        workOrders.forEach(System.out::println);
        System.out.println();

        MaintenanceReports.printOpenOrdersByPriority(workOrders);
        System.out.println();
        MaintenanceReports.printTopMachinesByBreakdowns(machines, workOrders);
        System.out.println();
        MaintenanceReports.printMttrByHall(machines, workOrders);
    }
}