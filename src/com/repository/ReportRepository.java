package com.repository;

import com.entities.Report;
import java.util.List;

public interface ReportRepository {

    Report findById(int id);

    List<Report> findByType(Report.ReportType type);

    List<Report> findAll();

    List<Report> findByDateRange(long startDate, long endDate);

    void insert(Report report);

    void update(Report report);

    void delete(int id);

    Report findLatestByType(Report.ReportType type);
}
