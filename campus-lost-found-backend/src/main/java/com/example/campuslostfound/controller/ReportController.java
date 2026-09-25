package com.example.campuslostfound.controller;

import com.example.campuslostfound.model.Report;
import com.example.campuslostfound.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<Report> createReport(
            @RequestBody Report report) {

        return ResponseEntity.ok(
                reportService.createReport(report)
        );
    }

    @GetMapping
    public ResponseEntity<List<Report>> getAllReports() {

        return ResponseEntity.ok(
                reportService.getAllReports()
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Report>> getReportsByUser(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                reportService.getReportsByUser(userId)
        );
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<Report>> getReportsByItem(
            @PathVariable Long itemId) {

        return ResponseEntity.ok(
                reportService.getReportsByItem(itemId)
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Report>> getReportsByStatus(
            @PathVariable String status) {

        return ResponseEntity.ok(
                reportService.getReportsByStatus(status)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Report> getReportById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                reportService.getReportById(id)
        );
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Report> updateReportStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return ResponseEntity.ok(
                reportService.updateReportStatus(id, status)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReport(
            @PathVariable Long id) {

        reportService.deleteReport(id);

        return ResponseEntity.ok("Report deleted successfully");
    }
}