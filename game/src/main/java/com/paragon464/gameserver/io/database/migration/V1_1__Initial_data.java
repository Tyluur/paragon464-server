package com.paragon464.gameserver.io.database.migration;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static com.google.common.io.Resources.getResource;

@Slf4j
public final class V1_1__Initial_data extends BaseJavaMigration {

    @Override
    public void migrate(final Context context) throws Exception {
        val copyManager = new CopyManager(context.getConnection().unwrap(BaseConnection.class));
        val resources = new Reflections("db.migration.V1_1_assets", new ResourcesScanner())
            .getResources(Pattern.compile(".*\\.csv"));

        if (resources == null || resources.isEmpty()) {
            throw new FlywayException("Unable to find expected CSV data for migration.");
        }

        long totalRows = 0L;
        for (val res : resources) {
            val tableName = res.substring(0, res.lastIndexOf('.')).substring(res.lastIndexOf('/') + 1);
            val queryString = "COPY %s FROM STDIN HEADER DELIMITER ',' CSV ENCODING 'UTF-8'";
            val reader = new BufferedReader(new InputStreamReader(getResource(res).openStream(),
                StandardCharsets.UTF_8));

            val result = copyManager.copyIn(String.format(queryString, tableName), reader);
            totalRows += result;
            log.debug("Inserted {} rows into table: {}.", result, tableName);
        }
        log.info("Inserted {} rows across {} tables.", totalRows, resources.size());
    }
}
