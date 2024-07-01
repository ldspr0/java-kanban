public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");
        TaskManager.init();

        // Test Сценарии
        TestFactory.createTestData();
        System.out.print("Get Data Test: ");
        System.out.println(TestFactory.testGetData() ? "Passed" : "Error");

        System.out.print("Update Data Test: ");
        System.out.println(TestFactory.testUpdateData() ? "Passed" : "Error");

        System.out.print("Remove Data Test: ");
        System.out.println(TestFactory.testRemoveData() ? "Passed" : "Error");
    }
}
