/*
 * 3D City Database - The Open Source CityGML Database
 * https://www.3dcitydb.org/
 *
 * Copyright 2013 - 2024
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
import org.citygml4j.model.gml.measures.Angle;
import org.citygml4j.model.gml.measures.Length;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class PolygonErrorImporter implements ADEImporter {
    private final CityGMLImportHelper helper;
    private final ImportManager manager;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;

    private int batchCounter;

    public PolygonErrorImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws SQLException {
        this.helper = helper;
        this.manager = manager;
        schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(schemaMapper.getTableName(ADETable.POLYGONERROR)) + " " +
                "(id, objectclass_id, polygonid, deviation, deviation_uom, distance, distance_uom, " +
                "linearringid, linearringid1, linearringid2, vertex) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
    }

    public void doImport(AbstractPolygonError error, long objectId, int objectClassId, IdResolver resolver) throws CityGMLImportException, SQLException {
        ps.setLong(1, objectId);
        ps.setInt(2, objectClassId);
        ps.setString(3, resolver.resolveId(error.getPolygonId()));

        if (error instanceof PolygonNonPlanarNormalsDeviation) {
            PolygonNonPlanarNormalsDeviation polygonError = (PolygonNonPlanarNormalsDeviation) error;
            insert(polygonError.getDeviation(), null, null, null, null, null, resolver, ps);
        } else if (error instanceof PolygonOrientationRingsSame) {
            PolygonOrientationRingsSame polygonError = (PolygonOrientationRingsSame) error;
            insert(null, null, polygonError.getLinearRingId(), null, null, null, resolver, ps);
        } else if (error instanceof PolygonInnerRingsNested) {
            PolygonInnerRingsNested polygonError = (PolygonInnerRingsNested) error;
            insert(null, null, null, polygonError.getLinearRingId1(), polygonError.getLinearRingId2(), null,
                    resolver, ps);
        } else if (error instanceof PolygonNonPlanarDistancePlane) {
            PolygonNonPlanarDistancePlane polygonError = (PolygonNonPlanarDistancePlane) error;
            insert(null, polygonError.getDistance(), null, null, null, polygonError.getVertex(), resolver, ps);
        } else if (error instanceof PolygonIntersectingRings) {
            PolygonIntersectingRings polygonError = (PolygonIntersectingRings) error;
            insert(null, null, null, polygonError.getLinearRingId1(), polygonError.getLinearRingId2(), null,
                    resolver, ps);
        } else if (error instanceof PolygonHoleOutside) {
            PolygonHoleOutside polygonError = (PolygonHoleOutside) error;
            insert(null, null, polygonError.getLinearRingId(), null, null, null, resolver, ps);
        } else {
            insert(null, null, null, null, null, null, resolver, ps);
        }

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize()) {
            helper.executeBatch(schemaMapper.getTableName(ADETable.POLYGONERROR));
        }
    }

    private void insert(Angle angle, Length distance, String linearRingId, String linearRingId1, String linearRingId2,
                        DirectPosition vertex, IdResolver resolver, PreparedStatement ps) throws SQLException {
        if (angle != null && angle.isSetValue()) {
            ps.setDouble(4, angle.getValue());
            ps.setString(5, angle.getUom());
        } else {
            ps.setNull(4, Types.NUMERIC);
            ps.setNull(5, Types.VARCHAR);
        }

        if (distance != null && distance.isSetValue()) {
            ps.setDouble(6, distance.getValue());
            ps.setString(7, distance.getUom());
        } else {
            ps.setNull(6, Types.NUMERIC);
            ps.setNull(7, Types.VARCHAR);
        }

        ps.setString(8, resolver.resolveId(linearRingId));
        ps.setString(9, resolver.resolveId(linearRingId1));
        ps.setString(10, resolver.resolveId(linearRingId2));
        ps.setString(11, manager.getVertex(vertex));
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
