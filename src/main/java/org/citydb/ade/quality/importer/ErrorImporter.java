package org.citydb.ade.quality.importer;

import org.citydb.ade.quality.schema.ADESequence;
import org.citydb.ade.quality.schema.ADETable;
import org.citydb.ade.quality.schema.ObjectMapper;
import org.citydb.ade.quality.schema.SchemaMapper;
import org.citydb.core.ade.importer.ADEImporter;
import org.citydb.core.ade.importer.CityGMLImportHelper;
import org.citydb.core.operation.importer.CityGMLImportException;
import org.citygml4j.ade.quality.model.*;
import org.citygml4j.model.gml.feature.AbstractFeature;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ErrorImporter implements ADEImporter {
    private final CityGMLImportHelper helper;
    private final ObjectMapper objectMapper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement psError;
    private final PreparedStatement psGeometryError;
    private final RingErrorImporter ringErrorImporter;
    private final PolygonErrorImporter polygonErrorImporter;
    private final SolidErrorImporter solidErrorImporter;
    private final SemanticErrorImporter semanticErrorImporter;

    private int batchCounter;

    public ErrorImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws SQLException {
        this.helper = helper;
        objectMapper = manager.getObjectMapper();
        schemaMapper = manager.getSchemaMapper();

        psError = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(schemaMapper.getTableName(ADETable.ERROR)) + " " +
                "(id, objectclass_id, validationresult_error_id) " +
                "values (?, ?, ?)");

        psGeometryError = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(schemaMapper.getTableName(ADETable.GEOMETRYERROR)) + " " +
                "(id, objectclass_id) " +
                "values (?, ?)");

        ringErrorImporter = manager.getImporter(RingErrorImporter.class);
        polygonErrorImporter = manager.getImporter(PolygonErrorImporter.class);
        solidErrorImporter = manager.getImporter(SolidErrorImporter.class);
        semanticErrorImporter = manager.getImporter(SemanticErrorImporter.class);
    }

    public void doImport(AbstractError error, long parentId, AbstractFeature feature) throws CityGMLImportException, SQLException {
        long objectId = helper.getNextSequenceValue(schemaMapper.getSequenceName(ADESequence.ERROR_SEQ));
        int objectClassId = objectMapper.getObjectClassId(error);

        psError.setLong(1, objectId);
        psError.setInt(2, objectClassId);
        psError.setLong(3, parentId);
        psError.addBatch();

        IdResolver resolver = IdResolver.of(feature);
        if (error instanceof AbstractGeometryError) {
            psGeometryError.setLong(1, objectId);
            psGeometryError.setInt(2, objectClassId);
            psGeometryError.addBatch();

            if (error instanceof AbstractRingError) {
                ringErrorImporter.doImport((AbstractRingError) error, objectId, objectClassId, resolver);
            } else if (error instanceof AbstractPolygonError) {
                polygonErrorImporter.doImport((AbstractPolygonError) error, objectId, objectClassId, resolver);
            } else if (error instanceof AbstractSolidError) {
                solidErrorImporter.doImport((AbstractSolidError) error, objectId, objectClassId, resolver);
            }
        } else if (error instanceof AbstractSemanticError) {
            semanticErrorImporter.doImport((AbstractSemanticError) error, objectId, objectClassId, resolver);
        }

        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize()) {
            helper.executeBatch(schemaMapper.getTableName(ADETable.ERROR));
        }
    }

    @Override
    public void executeBatch() throws SQLException {
        if (batchCounter > 0) {
            psError.executeBatch();
            psGeometryError.executeBatch();
            batchCounter = 0;
        }
    }

    @Override
    public void close() throws SQLException {
        psError.close();
        psGeometryError.close();
    }
}
