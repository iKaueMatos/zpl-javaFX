package com.novasoftware.spreadsheet.application.repository;

import com.novasoftware.spreadsheet.domain.model.ImportHistory;

import java.util.List;

public interface ImportHistoryRepository {
    boolean insert(ImportHistory importHistory);
    List<ImportHistory> getAll();
    boolean update(ImportHistory importHistory);
}
