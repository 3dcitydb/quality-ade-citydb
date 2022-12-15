package org.citydb.ade.quality.importer;

import org.citydb.ade.quality.schema.ADESequence;
import org.citydb.ade.quality.schema.ADETable;
import org.citydb.ade.quality.schema.SchemaMapper;
import org.citydb.core.ade.importer.ADEImporter;
import org.citydb.core.ade.importer.CityGMLImportHelper;
import org.citydb.core.operation.importer.CityGMLImportException;
import org.citygml4j.ade.quality.model.AbstractError;
import org.citygml4j.ade.quality.model.ValidationResult;
import org.citygml4j.model.gml.feature.AbstractFeature;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ValidationResultImporter implements ADEImporter {
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;
    private final ErrorImporter errorImporter;

    private int batchCounter;

    public ValidationResultImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws SQLException {
        this.helper = helper;
        schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(schemaMapper.getTableName(ADETable.VALIDATIONRESULT)) + " " +
                "(id, cityobject_validationresu_id, resulttype) " +
                "values (?, ?, ?)");

        errorImporter = manager.getImporter(ErrorImporter.class);
    }

    public void doImport(ValidationResult validationResult, AbstractFeature parent, long parentId) throws CityGMLImportException, SQLException {
        long objectId = helper.getNextSequenceValue(schemaMapper.getSequenceName(ADESequence.VALIDATIONRESULT_SEQ));
        ps.setLong(1, objectId);
        ps.setLong(2, parentId);
        ps.setString(3, validationResult.getResultType() != null ?
                validationResult.getResultType().name() :
                null);

        if (validationResult.getValidationPlanID() != null
                && validationResult.getValidationPlanID().isSetHref()) {
            helper.propagateObjectXlink(schemaMapper.getTableName(ADETable.VALIDATIONRESULT),
                    objectId, validationResult.getValidationPlanID().getHref(), "validationplanid_id");
        }

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize()) {
            helper.executeBatch(schemaMapper.getTableName(ADETable.VALIDATIONRESULT));
        }

        if (validationResult.isSetErrors()) {
            for (AbstractError error : validationResult.getErrors()) {
                errorImporter.doImport(error, objectId, parent);
            }
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
