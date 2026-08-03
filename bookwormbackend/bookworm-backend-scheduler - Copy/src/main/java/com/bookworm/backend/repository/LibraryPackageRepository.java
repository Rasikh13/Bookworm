package com.bookworm.backend.repository;

import com.bookworm.backend.entity.LibraryPackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LibraryPackageRepository extends JpaRepository<LibraryPackage, Long> {
    List<LibraryPackage> findByIsActiveTrue();
}
