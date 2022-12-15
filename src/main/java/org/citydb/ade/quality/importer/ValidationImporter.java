/*
 * 3D City Database - The Open Source CityGML Database
 * https://www.3dcitydb.org/
 *
 * Copyright 2013 - 2022
 * Chair of Geoinformatics
 * Technical University of Munich, Germany
 * https://www.lrg.tum.de/gis/
 *
 * The 3D City Database is jointly developed with the following
 * cooperation partners:
 *
 * Virtual City Systems, Berlin <https://vc.systems/>
 * M.O.S.S. Computer Grafik Systeme GmbH, Taufkirchen <http://www.moss.de/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.citydb.ade.quality.importer;

import org.citydb.ade.quality.schema.ADETable;
import org.citydb.core.ade.importer.ADEImporter;
import org.citydb.core.ade.importer.CityGMLImportHelper;
import org.citydb.core.ade.importer.ForeignKeys;
import org.citydb.core.database.schema.mapping.AbstractObjectType;
import org.citydb.core.operation.importer.CityGMLImportException;
import org.citygml4j.ade.quality.model.Validation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class ValidationImporter implements ADEImporter {
    private final CityGMLImportHelper helper;
    private final PreparedStatement ps;
    private final StatisticsImporter statisticsImporter;
    private final ValidationPlanImporter validationPlanImporter;

    private int batchCounter;

    public ValidationImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws SQLException {
        this.helper = helper;

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.VALIDATION)) + " " +
                "(id, validationdate, validationsoftware, statistics_id, validationplan_id) " +
                "values (?, ?, ?, ?, ?)");

        statisticsImporter = manager.getImporter(StatisticsImporter.class);
        validationPlanImporter = manager.getImporter(ValidationPlanImporter.class);
    }

    public void doImport(Validation validation, long objectId, AbstractObjectType<?> objectType, ForeignKeys foreignKeys) throws CityGMLImportException, SQLException {
        ps.setLong(1, objectId);

        if (validation.getValidationDate() != null) {
            ps.setObject(2, validation.getValidationDate().toOffsetDateTime());
        } else {
            ps.setNull(2, Types.TIMESTAMP);
        }

        ps.setString(3, validation.getValidationSoftware());

        if (validation.getStatistics() != null) {
            ps.setLong(4, statisticsImporter.doImport(validation.getStatistics()));
        } else {
            ps.setNull(4, Types.BIGINT);
        }

        if (validation.getValidationPlan() != null) {
            ps.setLong(5, validationPlanImporter.doImport(validation.getValidationPlan()));
        } else {
            ps.setNull(5, Types.BIGINT);
        }

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize()) {
            helper.executeBatch(objectType);
        }
    }

    @Override
    public void executeBatch() throws SQLException {
        if (batchCounter > 0) {
            ps.executeBatch();
            batchCounter = 0;
        }
    }

    @Override
    public void close() throws SQLException {
        ps.close();
    }
}
