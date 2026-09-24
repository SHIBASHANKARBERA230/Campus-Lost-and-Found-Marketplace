package com.example.campuslostfound.repository;

import com.example.campuslostfound.model.Claim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    List<Claim> findByUserId(Long userId);

    List<Claim> findByItemId(Long itemId);

    List<Claim> findByStatus(String status);
}