package com.example.sampleschool.io.repository;

import com.example.sampleschool.io.entity.RolesEntity;
import org.springframework.data.repository.CrudRepository;

public interface RolesRepository extends CrudRepository<RolesEntity, Long> {
    RolesEntity findByName(String name);
}
