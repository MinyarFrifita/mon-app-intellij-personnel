package gl2.example.personnel.controller;

import gl2.example.personnel.model.Employee;
import gl2.example.personnel.service.EmployeeService;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;

    // GET - Récupérer tous les employés
    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.findAll();
    }

    // GET - Récupérer un employé par ID
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        Optional<Employee> employee = employeeService.findById(id);
        return employee.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST - Ajouter un nouvel employé
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody Employee employee) {
        Employee savedEmployee = employeeService.save(employee);
        return ResponseEntity.ok(savedEmployee);
    }

    // PUT - Mettre à jour un employé (remplace entièrement)
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @Valid @RequestBody Employee employee) {
        Optional<Employee> existingEmployee = employeeService.findById(id);
        if (existingEmployee.isPresent()) {
            employee.setId(id);
            Employee updatedEmployee = employeeService.save(employee);
            return ResponseEntity.ok(updatedEmployee);
        }
        return ResponseEntity.notFound().build();
    }

    // PATCH - Mettre à jour partiellement un employé
    @PatchMapping("/{id}")
    public ResponseEntity<Employee> patchEmployee(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Optional<Employee> optionalEmployee = employeeService.findById(id);
        if (!optionalEmployee.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Employee employee = optionalEmployee.get();

        // Appliquer les modifications partielles
        if (updates.containsKey("name")) {
            String name = (String) updates.get("name");
            // Vérifier manuellement la validation @NotEmpty pour le champ name
            if (name == null || name.trim().isEmpty()) {
                Map<String, String> errors = new HashMap<>();
                errors.put("name", "Le nom ne peut pas être vide");
                logger.info("Validation error for name: {}", errors);
                throw new ValidationException(String.valueOf(errors)); // Utilisation de notre ValidationException personnalisée
            }
            employee.setName(name);
        }
        if (updates.containsKey("position")) {
            employee.setPosition((String) updates.get("position"));
        }
        if (updates.containsKey("salary")) {
            employee.setSalary(((Number) updates.get("salary")).doubleValue());
        }
        if (updates.containsKey("email")) {
            String email = (String) updates.get("email");
            // Vérifier manuellement la validation @Email pour le champ email
            if (email != null && !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                Map<String, String> errors = new HashMap<>();
                errors.put("email", "L'email doit être valide");
                logger.info("Validation error for email: {}", errors);
                throw new ValidationException(String.valueOf(errors)); // Utilisation de notre ValidationException personnalisée
            }
            employee.setEmail(email);
        }

        Employee updatedEmployee = employeeService.save(employee);
        return ResponseEntity.ok(updatedEmployee);
    }

    // DELETE - Supprimer un employé
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        Optional<Employee> employee = employeeService.findById(id);
        if (employee.isPresent()) {
            employeeService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Gestion des erreurs de validation pour POST/PUT
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        logger.info("Validation error in POST/PUT: {}", errors);
        return ResponseEntity.badRequest().body(errors);
    }

    // Gestion des erreurs de validation pour PATCH
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Class<? extends ValidationException>> handleValidationException(ValidationException ex) {
        logger.info("Handling ValidationException: {}", ex.getClass());
        return ResponseEntity.badRequest().body(ex.getClass());
    }
}