package com.hainv.tourbooking.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.hainv.tourbooking.domain.Permission;
import com.hainv.tourbooking.domain.Role;
import com.hainv.tourbooking.domain.User;
import com.hainv.tourbooking.repository.PermissionRepository;
import com.hainv.tourbooking.repository.RoleRepository;
import com.hainv.tourbooking.repository.UserRepository;
import com.hainv.tourbooking.util.constant.GenderEnum;

@Service
public class DatabaseInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(
            PermissionRepository permissionRepository,
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> START INIT DATABASE");
        long countPermissions = this.permissionRepository.count();
        long countRoles = this.roleRepository.count();
        long countUsers = this.userRepository.count();

        if (countPermissions == 0) {
            ArrayList<Permission> arr = new ArrayList<>();
            // 1. Module DESTINATIONS (Địa điểm)
            arr.add(new Permission("Create a destination", "/api/v1/destinations", "POST", "DESTINATIONS"));
            arr.add(new Permission("Update a destination", "/api/v1/destinations", "PUT", "DESTINATIONS"));
            arr.add(new Permission("Delete a destination", "/api/v1/destinations/{id}", "DELETE", "DESTINATIONS"));
            arr.add(new Permission("Get a destination by id", "/api/v1/destinations/{id}", "GET", "DESTINATIONS"));
            arr.add(new Permission("Get destinations with pagination", "/api/v1/destinations", "GET", "DESTINATIONS"));

            // 2. Module CATEGORIES (Loại Tour)
            arr.add(new Permission("Create a category", "/api/v1/categories", "POST", "CATEGORIES"));
            arr.add(new Permission("Update a category", "/api/v1/categories", "PUT", "CATEGORIES"));
            arr.add(new Permission("Delete a category", "/api/v1/categories/{id}", "DELETE", "CATEGORIES"));
            arr.add(new Permission("Get a category by id", "/api/v1/categories/{id}", "GET", "CATEGORIES"));
            arr.add(new Permission("Get categories with pagination", "/api/v1/categories", "GET", "CATEGORIES"));

            // 3. Module TOURS (Tour du lịch)
            arr.add(new Permission("Create a tour", "/api/v1/tours", "POST", "TOURS"));
            arr.add(new Permission("Update a tour", "/api/v1/tours", "PUT", "TOURS"));
            arr.add(new Permission("Delete a tour", "/api/v1/tours/{id}", "DELETE", "TOURS"));
            arr.add(new Permission("Get a tour by id", "/api/v1/tours/{id}", "GET", "TOURS"));
            arr.add(new Permission("Get tours with pagination", "/api/v1/tours", "GET", "TOURS"));

            // 4. Module TOUR_SCHEDULES (Lịch khởi hành)
            arr.add(new Permission("Create a schedule", "/api/v1/tour-schedules", "POST", "TOUR_SCHEDULES"));
            arr.add(new Permission("Update a schedule", "/api/v1/tour-schedules", "PUT", "TOUR_SCHEDULES"));
            arr.add(new Permission("Delete a schedule", "/api/v1/tour-schedules/{id}", "DELETE", "TOUR_SCHEDULES"));
            arr.add(new Permission("Get a schedule by id", "/api/v1/tour-schedules/{id}", "GET", "TOUR_SCHEDULES"));
            arr.add(new Permission("Get schedules with pagination", "/api/v1/tour-schedules", "GET", "TOUR_SCHEDULES"));

            // 5. Module BOOKINGS (Đơn đặt tour)
            arr.add(new Permission("Create a booking", "/api/v1/bookings", "POST", "BOOKINGS"));
            arr.add(new Permission("Update booking status", "/api/v1/bookings", "PUT", "BOOKINGS"));
            arr.add(new Permission("Get a booking by id", "/api/v1/bookings/{id}", "GET", "BOOKINGS"));
            arr.add(new Permission("Get user booking history", "/api/v1/bookings/by-user", "POST", "BOOKINGS"));
            arr.add(new Permission("Get bookings with pagination", "/api/v1/bookings", "GET", "BOOKINGS"));

            // 6. Module PAYMENTS (Thanh toán)
            arr.add(new Permission("Create a payment", "/api/v1/payments", "POST", "PAYMENTS"));
            arr.add(new Permission("Update a payment", "/api/v1/payments", "PUT", "PAYMENTS"));
            // arr.add(new Permission("Delete a payment", "/api/v1/payments/{id}", "DELETE",
            // "PAYMENTS"));
            arr.add(new Permission("Get a payment by id", "/api/v1/payments/{id}", "GET", "PAYMENTS"));
            arr.add(new Permission("Get payments with pagination", "/api/v1/payments", "GET", "PAYMENTS"));

            arr.add(new Permission("Create a permission", "/api/v1/permissions", "POST", "PERMISSIONS"));
            arr.add(new Permission("Update a permission", "/api/v1/permissions", "PUT", "PERMISSIONS"));
            arr.add(new Permission("Delete a permission", "/api/v1/permissions/{id}", "DELETE", "PERMISSIONS"));
            arr.add(new Permission("Get a permission by id", "/api/v1/permissions/{id}", "GET", "PERMISSIONS"));
            arr.add(new Permission("Get permissions with pagination", "/api/v1/permissions", "GET", "PERMISSIONS"));

            arr.add(new Permission("Create a role", "/api/v1/roles", "POST", "ROLES"));
            arr.add(new Permission("Update a role", "/api/v1/roles", "PUT", "ROLES"));
            arr.add(new Permission("Delete a role", "/api/v1/roles/{id}", "DELETE", "ROLES"));
            arr.add(new Permission("Get a role by id", "/api/v1/roles/{id}", "GET", "ROLES"));
            arr.add(new Permission("Get roles with pagination", "/api/v1/roles", "GET", "ROLES"));

            arr.add(new Permission("Create a user", "/api/v1/users", "POST", "USERS"));
            arr.add(new Permission("Update a user", "/api/v1/users", "PUT", "USERS"));
            arr.add(new Permission("Delete a user", "/api/v1/users/{id}", "DELETE", "USERS"));
            arr.add(new Permission("Get a user by id", "/api/v1/users/{id}", "GET", "USERS"));
            arr.add(new Permission("Get users with pagination", "/api/v1/users", "GET", "USERS"));

            arr.add(new Permission("Create a subscriber", "/api/v1/subscribers", "POST", "SUBSCRIBERS"));
            arr.add(new Permission("Update a subscriber", "/api/v1/subscribers", "PUT", "SUBSCRIBERS"));
            arr.add(new Permission("Delete a subscriber", "/api/v1/subscribers/{id}", "DELETE", "SUBSCRIBERS"));
            arr.add(new Permission("Get a subscriber by id", "/api/v1/subscribers/{id}", "GET", "SUBSCRIBERS"));
            arr.add(new Permission("Get subscribers with pagination", "/api/v1/subscribers", "GET", "SUBSCRIBERS"));

            arr.add(new Permission("Download a file", "/api/v1/files", "POST", "FILES"));
            arr.add(new Permission("Upload a file", "/api/v1/files", "GET", "FILES"));

            this.permissionRepository.saveAll(arr);
        }

        if (countRoles == 0) {
            List<Permission> allPermissions = this.permissionRepository.findAll();

            Role adminRole = new Role();
            adminRole.setName("SUPER_ADMIN");
            adminRole.setDescription("Admin thì full permissions");
            adminRole.setActive(true);
            adminRole.setPermissions(allPermissions);

            this.roleRepository.save(adminRole);
        }

        if (countUsers == 0) {
            User adminUser = new User();
            adminUser.setEmail("admin@gmail.com");
            adminUser.setAddress("hn");
            adminUser.setAge(25);
            adminUser.setGender(GenderEnum.MALE);
            adminUser.setName("I'm super admin");
            adminUser.setPassword(this.passwordEncoder.encode("123456"));

            Role adminRole = this.roleRepository.findByName("SUPER_ADMIN");
            if (adminRole != null) {
                adminUser.setRole(adminRole);
            }

            this.userRepository.save(adminUser);
        }

        if (countPermissions > 0 && countRoles > 0 && countUsers > 0) {
            System.out.println(">>> SKIP INIT DATABASE ~ ALREADY HAVE DATA...");
        } else
            System.out.println(">>> END INIT DATABASE");
    }

}