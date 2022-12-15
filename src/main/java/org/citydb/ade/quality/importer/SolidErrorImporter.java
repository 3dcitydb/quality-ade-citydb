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
import java.util.List;

public class SolidErrorImporter implements ADEImporter {
    private final CityGMLImportHelper helper;
    private final ImportManager manager;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;
    private final EdgeListImporter edgeListImporter;
    private final PolygonIdListImporter polygonIdListImporter;

    private int batchCounter;

    public SolidErrorImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws SQLException {
        this.helper = helper;
        this.manager = manager;
        schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(schemaMapper.getTableName(ADETable.SOLIDERROR)) + " " +
                "(id, objectclass_id, geometryid, edges_id, polygonid1, polygonid2, vertex) " +
                "values (?, ?, ?, ?, ?, ?, ?)");

        edgeListImporter = manager.getImporter(EdgeListImporter.class);
        polygonIdListImporter = manager.getImporter(PolygonIdListImporter.class);
    }

    public void doImport(AbstractSolidError error, long objectId, int objectClassId, IdResolver resolver) throws CityGMLImportException, SQLException {
        ps.setLong(1, objectId);
        ps.setInt(2, objectClassId);
        ps.setString(3, resolver.resolveId(error.getGeometryId()));

        if (error instanceof SolidNonManifoldEdge) {
            SolidNonManifoldEdge solidError = (SolidNonManifoldEdge) error;
            insert(solidError.getEdges(), null, null, null, resolver, ps);
        } else if (error instanceof SolidNotClosed) {
            SolidNotClosed solidError = (SolidNotClosed) error;
            insert(solidError.getEdges(), null, null, null, resolver, ps);
        } else if (error instanceof SolidNonManifoldVertex) {
            SolidNonManifoldVertex solidError = (SolidNonManifoldVertex) error;
            insert(null, null, null, solidError.getVertex(), resolver, ps);
        } else if (error instanceof SolidPolygonWrongOrientation) {
            SolidPolygonWrongOrientation solidError = (SolidPolygonWrongOrientation) error;
            insert(solidError.getEdges(), null, null, null, resolver, ps);
        } else if (error instanceof SolidSelfIntersection) {
            SolidSelfIntersection solidError = (SolidSelfIntersection) error;
            insert(null, solidError.getPolygonId1(), solidError.getPolygonId2(), null, resolver, ps);
        } else {
            insert(null, null, null, null, resolver, ps);
        }

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize()) {
            helper.executeBatch(schemaMapper.getTableName(ADETable.SOLIDERROR));
        }

        if (error instanceof SolidMultipleConnectedComponents) {
            SolidMultipleConnectedComponents solidError = (SolidMultipleConnectedComponents) error;
            if (solidError.isSetComponents()) {
                for (PolygonIdList component : solidError.getComponents()) {
                    polygonIdListImporter.doImport(component, objectId, resolver);
                }
            }
        }
    }

    private void insert(List<Edge> edges, String polygonId1, String polygonId2, DirectPosition vertex,
                        IdResolver resolver, PreparedStatement ps) throws CityGMLImportException, SQLException {
        if (edges != null) {
            ps.setLong(4, edgeListImporter.doImport(edges));
        } else {
            ps.setNull(4, Types.BIGINT);
        }

        ps.setString(5, resolver.resolveId(polygonId1));
        ps.setString(6, resolver.resolveId(polygonId2));
        ps.setString(7, manager.getVertex(vertex));
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
