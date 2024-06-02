package jm.task.core.jdbc;

import jm.task.core.jdbc.service.UserService;
import jm.task.core.jdbc.service.UserServiceImpl;

public class Main {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();

        userService.createUsersTable();

//        userService.dropUsersTable();

        userService.saveUser("Дмитрий", "Сурсин", (byte) 25);
        userService.saveUser("Иван", "Иванов", (byte) 30);
        userService.saveUser("Руслан", "Баженов", (byte) 50);
        userService.saveUser("Виталий", "Гогунский", (byte) 100);
        userService.getAllUsers().forEach(System.out::println);
//
//        userService.removeUserById(2);

        userService.cleanUsersTable();

    }
}
