package com.repository;

import com.common.Database;
import com.entities.Report;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SQLiteReportRepository implements ReportRepository {

    private final Database db;

    public SQLiteReportRepository(Database db) {
        this.db = db;
    }

    private Report mapRow(ResultSet rs) throws SQLException {
        String typeStr = rs.getString("type");
        Report.ReportType type = Report.ReportType.valueOf(typeStr);

        return new Report(
                rs.getInt("id"),
                type,
                rs.getLong("start_date"),
                rs.getLong("created_date"),
                rs.getString("report_data"));
    }

    @Override
    public Report findById(int id) {
        String sql = "SELECT * FROM reports WHERE id = ?";
        return db.queryOne(sql, rs -> mapRow(rs), id);
    }

    @Override
    public List<Report> findByType(Report.ReportType type) {
        String sql = "SELECT * FROM reports WHERE type = ? ORDER BY created_date DESC";
        return db.queryList(sql, rs -> mapRow(rs), type.name());
    }

    @Override
    public List<Report> findAll() {
        String sql = "SELECT * FROM reports ORDER BY created_date DESC";
        return db.queryList(sql, rs -> mapRow(rs));
    }

    @Override
    public List<Report> findByDateRange(long startDate, long endDate) {
        String sql = "SELECT * FROM reports WHERE start_date >= ? AND start_date <= ? ORDER BY created_date DESC";
        return db.queryList(sql, rs -> mapRow(rs), startDate, endDate);
    }

    @Override
    public void insert(Report report) {
        String sql = "INSERT INTO reports(type, start_date, end_date, created_date, sold_quantity, total_revenue) VALUES (?, ?, ?, ?, ?, ?)";
        int id = db.executeInsertReturnId(sql,
                report.getReportType().name(),
                report.getDataStart(),
                report.getDataStart(), // Using dataStart as end for now
                report.getDateGenerated(),
                0, // Default sold quantity
                0.0 // Default revenue
        );
        report.setId(id);
    }

    @Override
    public void update(Report report) {
        String sql = "UPDATE reports SET type=?, start_date=?, created_date=? WHERE id=?";
        db.executeUpdate(sql,
                report.getReportType().name(),
                report.getDataStart(),
                report.getDateGenerated(),
                report.getId());
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM reports WHERE id = ?";
        db.executeUpdate(sql, id);
    }

    @Override
    public Report findLatestByType(Report.ReportType type) {
        String sql = "SELECT * FROM reports WHERE type = ? ORDER BY created_date DESC LIMIT 1";
        return db.queryOne(sql, rs -> mapRow(rs), type.name());
    }
}