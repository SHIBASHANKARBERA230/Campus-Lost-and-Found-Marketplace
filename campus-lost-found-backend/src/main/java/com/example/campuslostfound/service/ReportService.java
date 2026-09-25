package com.example.campuslostfound.service;

import com.example.campuslostfound.model.Report;
import com.example.campuslostfound.repository.ReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public Report createReport(Report report) {

        if (report.getStatus() == null || report.getStatus().isEmpty()) {
            report.setStatus("PENDING");
        }

        return reportRepository.save(report);
    }

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public List<Report> getReportsByUser(Long userId) {
        return reportRepository.findByUserId(userId);
    }

    public List<Report> getReportsByItem(Long itemId) {
        return reportRepository.findByItemId(itemId);
    }

    public List<Report> getReportsByStatus(String status) {
        return reportRepository.findByStatus(status);
    }

    public Report getReportById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));
    }

    public Report updateReportStatus(Long id, String status) {

        Report report = getReportById(id);

        report.setStatus(status);

        return reportRepository.save(report);
    }

    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }
}