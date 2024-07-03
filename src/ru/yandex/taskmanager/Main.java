package ru.yandex.taskmanager;

import ru.yandex.taskmanager.service.TaskManager;
import ru.yandex.taskmanager.tests.TestFactory;

public class Main {

    public static void main(String[] args) {
        /*
        В случае когда TaskManager был утилитарным классом, все айдишники и данные о тасках были общие.
        Если делать через экземпляр класса, то для программы можно будет запустить несколько
        разных версий таск менеджеров. Имхо, выбор реализации больше зависит от способов/целей использования.
         */

        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();

        // Это очень хорошо, что скоро будем проходить полноценные тесты, а пока я буду писать их как могу, ок?
        // Не захотелось все, что я описал в тестах, вываливать сюда.
        // Test Сценарии
        TestFactory.createTestData(taskManager);
        System.out.print("Get Data Test: ");
        System.out.println(TestFactory.testGetData(taskManager) ? "Passed" : "Error");

        System.out.print("Update Data Test: ");
        System.out.println(TestFactory.testUpdateData(taskManager) ? "Passed" : "Error");

        System.out.print("Remove Data Test: ");
        System.out.println(TestFactory.testRemoveData(taskManager) ? "Passed" : "Error");
    }
}
