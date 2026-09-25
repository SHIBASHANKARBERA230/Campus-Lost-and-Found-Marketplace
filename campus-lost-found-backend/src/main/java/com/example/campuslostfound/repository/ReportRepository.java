package com.example.campuslostfound.repository;

import com.example.campuslostfound.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByUserId(Long userId);

    List<Report> findByItemId(Long itemId);

    List<Report> findByStatus(String status);
}