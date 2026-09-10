package com.ridwantaufik.allobank.repository;

import com.ridwantaufik.allobank.entity.BillGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BillGroupRepository extends JpaRepository<BillGroup, UUID> {
}