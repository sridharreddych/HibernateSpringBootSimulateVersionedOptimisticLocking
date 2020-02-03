package com.bookstore;

import com.bookstore.service.InventoryService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MainApplication {

    // Running the application should result in 
    // org.springframework.orm.ObjectOptimisticLockingFailureException
    
    private final InventoryService inventoryService;

    public MainApplication(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Bean
    public ApplicationRunner init() {
        return args -> {

            ExecutorService executor = Executors.newFixedThreadPool(2);
            executor.execute(inventoryService);
            // Thread.sleep(2000); -> adding a sleep here will break the transactions concurrency
            executor.execute(inventoryService);

            executor.shutdown();
            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        };
    }
}


/*
 * 
 * How To Simulate OptimisticLockException Shaped Via @Version

Note: Optimistic locking via @Version works for detached entities as well.

Description: This is a Spring Boot application that simulates a scenario that leads to an optimistic locking exception. So, running the application should end up with a Spring specific ObjectOptimisticLockingFailureException exception.

Key points:

set up versioned optimistic locking mechanism
rely on two concurrent threads that call the same @Transactional method used for updating data

 * 
 * 
 * 
 */
