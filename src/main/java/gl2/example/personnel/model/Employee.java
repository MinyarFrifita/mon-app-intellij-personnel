package gl2.example.personnel.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Le nom ne peut pas être vide")
    private String name;

    private String position;

    private double salary;

    @Email(message = "L'email doit être valide")
    private String email;

    // Constructeurs
    public Employee() {}

    public Employee(String name, String position, double salary, String email) {
        this.name = name;
        this.position = position;
        this.salary = salary;
        this.email = email;
    }

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}