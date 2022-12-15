package org.citydb.ade.quality.importer;

import org.citydb.ade.quality.schema.ADESequence;
import org.citydb.ade.quality.schema.ADETable;
import org.citydb.ade.quality.schema.SchemaMapper;
import org.citydb.core.ade.importer.ADEImporter;
import org.citydb.core.ade.importer.CityGMLImportHelper;
import org.citydb.core.operation.importer.CityGMLImportException;
import org.citygml4j.ade.quality.model.Requirement;
import org.citygml4j.ade.quality.model.ValidationPlan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class ValidationPlanImporter implements ADEImporter {
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;
    private final GlobalParametersImporter globalParametersImporter;
    private final FilterImporter filterImporter;
    private final RequirementImporter requirementImporter;

    private int batchCounter;

    public ValidationPlanImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws SQLException {
        this.helper = helper;
        schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(schemaMapper.getTableName(ADETable.VALIDATIONPLAN)) + " " +
                "(id, globalparameters_id, filter_id) " +
                "values (?, ?, ?)");

        globalParametersImporter = manager.getImporter(GlobalParametersImporter.class);
        filterImporter = manager.getImporter(FilterImporter.class);
        requirementImporter = manager.getImporter(RequirementImporter.class);
    }

    public long doImport(ValidationPlan validationPlan) throws CityGMLImportException, SQLException {
        long objectId = helper.getNextSequenceValue(schemaMapper.getSequenceName(ADESequence.VALIDATIONPLAN_SEQ));
        ps.setLong(1, objectId);

        if (validationPlan.isSetGlobalParameters()) {
            ps.setLong(2, globalParametersImporter.doImport(validationPlan.getGlobalParameters(), validationPlan));
        } else {
            ps.setNull(2, Types.BIGINT);
        }

        if (validationPlan.getFilter() != null) {
            ps.setLong(3, filterImporter.doImport(validationPlan.getFilter()));
        } else {
            ps.setNull(2, Types.BIGINT);
        }

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize()) {
            helper.executeBatch(schemaMapper.getTableName(ADETable.VALIDATIONPLAN));
        }

        if (validationPlan.isSetRequirements()) {
            for (Requirement requirement : validationPlan.getRequirements()) {
                requirementImporter.doImport(requirement, objectId);
            }
        }

        return objectId;
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
