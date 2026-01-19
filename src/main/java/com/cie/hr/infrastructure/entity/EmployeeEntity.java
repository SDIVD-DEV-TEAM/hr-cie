package com.cie.hr.infrastructure.entity;

import com.cie.hr.common.adapter.AbstractEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


/**
 * @author Koty BLEU
 * @created 01/05/2023
 * @project hr
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "employees",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"employee_number"}, name = "uc_employees_employee_number")
        },
        indexes = {
                @Index(name = "idx_employees_profile_id", columnList = "profile_id"),
        }
)
public class EmployeeEntity extends AbstractEntity {
    @Column
    private String firstname;

    @Column
    private String lastname;

    @Column
    @Email
    private String email;

    @Column(name = "employee_number", unique = true)
    @Length(max = 7)
    private String employeeNumber;

    @ManyToOne
    @JoinColumn(name = "profile_id", nullable = false, foreignKey = @ForeignKey(name = "fk_employees_profile"))
    private ProfileEntity profile;

    @Column
    private Boolean active;

    @Column
    private Boolean isNotLocked;

    @Column
    private String password;

    @Column
    private LocalDateTime changeExpiredDate;

    @Column
    private Boolean isFirstConnect;

    @Column
    private Boolean sendAccountIdEmail;

    @Column
    private String token;

    @Column
    private LocalDateTime lastLoginDate;

    @Column
    private LocalDateTime expiredAt;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "employee_id", foreignKey = @ForeignKey(name = "fk_employees_password_store"))
    List<PasswordStoreEntity> passwordStores;

    @Column
    private Integer accessLevel;

    @Column
    private LocalDateTime lastFailedAttempt;

    @Column
    private Integer failedAttempts;

    @OneToOne(mappedBy = "employee")
    private JobEntity job;

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFullName() {
        return "%s %s".formatted(this.firstname, this.lastname);
    }
}
