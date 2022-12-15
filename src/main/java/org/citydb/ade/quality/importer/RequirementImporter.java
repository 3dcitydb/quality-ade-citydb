package org.citydb.ade.quality.importer;

import org.citydb.ade.quality.schema.ADESequence;
import org.citydb.ade.quality.schema.ADETable;
import org.citydb.ade.quality.schema.SchemaMapper;
import org.citydb.core.ade.importer.ADEImporter;
import org.citydb.core.ade.importer.CityGMLImportHelper;
import org.citydb.core.operation.importer.CityGMLImportException;
import org.citygml4j.ade.quality.model.Parameter;
import org.citygml4j.ade.quality.model.Requirement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RequirementImporter implements ADEImporter {
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;
    private final ParameterImporter parameterImporter;

    private int batchCounter;

    public RequirementImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws SQLException {
        this.helper = helper;
        schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(schemaMapper.getTableName(ADETable.REQUIREMENT)) + " " +
                "(id, validationpla_requirement_id, enabled, requirementtype) " +
                "values (?, ?, ?, ?)");

        parameterImporter = manager.getImporter(ParameterImporter.class);
    }

    public void doImport(Requirement requirement, long parentId) throws CityGMLImportException, SQLException {
        long objectId = helper.getNextSequenceValue(schemaMapper.getSequenceName(ADESequence.REQUIREMENT_SEQ));
        ps.setLong(1, objectId);
        ps.setLong(2, parentId);
        ps.setInt(3, requirement.isEnabled() ? 1 : 0);
        ps.setString(4, requirement.getRequirementType() != null ? requirement.getRequirementType().name() : null);

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize()) {
            helper.executeBatch(schemaMapper.getTableName(ADETable.REQUIREMENT));
        }

        if (requirement.isSetParameters()) {
            for (Parameter parameter : requirement.getParameters()) {
                parameterImporter.doImport(parameter, requirement, objectId);
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
