package org.example.warehouse;

import org.springframework.boot.SpringApplication;

public class TestWarehouseApplication {

    public static void main(String[] args) {
        SpringApplication.from(WarehouseApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
