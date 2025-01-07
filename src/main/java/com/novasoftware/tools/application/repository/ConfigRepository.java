package com.novasoftware.tools.application.repository;

import com.novasoftware.tools.domain.model.Config;

public interface ConfigRepository {
    void saveConfig(Config config);
    void updateConfig(Config config);
}
