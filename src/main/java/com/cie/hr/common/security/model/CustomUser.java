package com.cie.hr.common.security.model;

import com.cie.hr.domain.entity.Employee;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Alexis TAMBIE
 * @created 04/05/2023
 * @project hr-cie
 */
public record CustomUser(Employee employee) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (this.employee.profile() == null) {
            return authorities;
        }
        authorities.add(new SimpleGrantedAuthority(this.employee.profile().getName()));
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.employee.password();
    }

    @Override
    public String getUsername() {
        return this.employee.email();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.employee.isNotLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.employee.isActive();
    }
}
