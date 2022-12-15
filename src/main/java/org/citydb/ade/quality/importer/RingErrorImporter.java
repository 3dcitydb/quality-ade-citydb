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
import org.citygml4j.ade.quality.model.*;
import org.citygml4j.model.gml.geometry.primitives.DirectPosition;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class RingErrorImporter implements ADEImporter {
    private final CityGMLImportHelper helper;
    private final ImportManager manager;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;
    private final EdgeImporter edgeImporter;

    private int batchCounter;

    public RingErrorImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws SQLException {
        this.helper = helper;
        this.manager = manager;
        schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(schemaMapper.getTableName(ADETable.RINGERROR)) + " " +
                "(id, objectclass_id, linearringid, edge1_id, edge2_id, vertex1, vertex2, type) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?)");

        edgeImporter = manager.getImporter(EdgeImporter.class);
    }

    public void doImport(AbstractRingError error, long objectId, int objectClassId, IdResolver resolver) throws CityGMLImportException, SQLException {
        ps.setLong(1, objectId);
        ps.setInt(2, objectClassId);
        ps.setString(3, resolver.resolveId(error.getLinearRingId()));

        if (error instanceof RingConsecutivePointsSame) {
            RingConsecutivePointsSame ringError = (RingConsecutivePointsSame) error;
            insert(null, null, ringError.getVertex1(), ringError.getVertex2(), null, ps);
        } else if (error instanceof RingSelfIntersection) {
            RingSelfIntersection ringError = (RingSelfIntersection) error;
            insert(ringError.getEdge1(), ringError.getEdge2(), ringError.getVertex1(), ringError.getVertex2(),
                    ringError.getType(), ps);
        } else {
            insert(null, null, null, null, null, ps);
        }

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize()) {
            helper.executeBatch(schemaMapper.getTableName(ADETable.RINGERROR));
        }
    }

    private void insert(Edge edge1, Edge edge2, DirectPosition vertex1, DirectPosition vertex2,
                        RingSelfIntersectionType type, PreparedStatement ps) throws CityGMLImportException, SQLException {
        if (edge1 != null) {
            ps.setLong(4, edgeImporter.doImport(edge1));
        } else {
            ps.setNull(4, Types.BIGINT);
        }

        if (edge2 != null) {
            ps.setLong(5, edgeImporter.doImport(edge2));
        } else {
            ps.setNull(5, Types.BIGINT);
        }

        ps.setString(6, manager.getVertex(vertex1));
        ps.setString(7, manager.getVertex(vertex2));
        ps.setString(8, type != null ? type.name() : null);
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
