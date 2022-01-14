package com.example.sampleschool.io.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Entity
@Table(name = "authorities")
public class AuthorityEntity implements Serializable {

    private static final long serialVersionUID = -5533511901388340509L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, length = 50)
    private String name;

    @ManyToMany(mappedBy = "authorities")
    private Collection<RolesEntity> roles;

    public AuthorityEntity() {
    }

    public AuthorityEntity(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<RolesEntity> getRoles() {
        return roles;
    }

    public void setRoles(Collection<RolesEntity> roles) {
        this.roles = roles;
    }
}
