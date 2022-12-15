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
import org.citydb.ade.quality.schema.SchemaMapper;
import org.citydb.core.ade.importer.ADEImporter;
import org.citydb.core.ade.importer.CityGMLImportHelper;
import org.citydb.core.operation.importer.CityGMLImportException;
import org.citygml4j.ade.quality.model.AbstractSemanticError;
import org.citygml4j.ade.quality.model.SemanticAttributeMissing;
import org.citygml4j.ade.quality.model.SemanticAttributeWrongValue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class SemanticErrorImporter implements ADEImporter {
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;

    private int batchCounter;

    public SemanticErrorImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws SQLException {
        this.helper = helper;
        schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(schemaMapper.getTableName(ADETable.SEMANTICERROR)) + " " +
                "(id, objectclass_id, attributename, childid, generic) " +
                "values (?, ?, ?, ?, ?)");
    }

    public void doImport(AbstractSemanticError error, long objectId, int objectClassId, IdResolver resolver) throws CityGMLImportException, SQLException {
        ps.setLong(1, objectId);
        ps.setInt(2, objectClassId);

        if (error instanceof SemanticAttributeWrongValue) {
            SemanticAttributeWrongValue semanticError = (SemanticAttributeWrongValue) error;
            insert(semanticError.getAttributeName(), semanticError.getChildId(), semanticError.isGeneric(),
                    resolver, ps);
        } else if (error instanceof SemanticAttributeMissing) {
            SemanticAttributeMissing semanticError = (SemanticAttributeMissing) error;
            insert(semanticError.getAttributeName(), semanticError.getChildId(), semanticError.isGeneric(),
                    resolver, ps);
        } else {
            insert(null, null, null, resolver, ps);
        }

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize()) {
            helper.executeBatch(schemaMapper.getTableName(ADETable.SEMANTICERROR));
        }
    }

    private void insert(String attributeName, String childId, Boolean isGeneric, IdResolver resolver,
                        PreparedStatement ps) throws SQLException {
        ps.setString(3, attributeName);
        ps.setString(4, resolver.resolveId(childId));

        if (isGeneric != null) {
            ps.setInt(5, isGeneric ? 1 : 0);
        } else {
            ps.setNull(5, Types.NUMERIC);
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
