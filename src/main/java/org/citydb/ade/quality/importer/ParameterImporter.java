package org.citydb.ade.quality.importer;

import org.citydb.ade.quality.schema.ADESequence;
import org.citydb.ade.quality.schema.ADETable;
import org.citydb.ade.quality.schema.SchemaMapper;
import org.citydb.core.ade.importer.ADEImporter;
import org.citydb.core.ade.importer.CityGMLImportHelper;
import org.citydb.core.operation.importer.CityGMLImportException;
import org.citygml4j.ade.quality.model.AbstractDataType;
import org.citygml4j.ade.quality.model.Parameter;
import org.citygml4j.ade.quality.model.Requirement;
import org.citygml4j.ade.quality.model.ValidationPlan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class ParameterImporter implements ADEImporter {
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;

    private int batchCounter;

    public ParameterImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws SQLException {
        this.helper = helper;
        schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(schemaMapper.getTableName(ADETable.PARAMETER)) + " " +
                "(id, globalparameter_parameter_id, requirement_parameter_id, name, value, uom) " +
                "values (?, ?, ?, ?, ?, ?)");
    }

    public long doImport(Parameter parameter, AbstractDataType parent, long parentId) throws CityGMLImportException, SQLException {
        long objectId = helper.getNextSequenceValue(schemaMapper.getSequenceName(ADESequence.PARAMETER_SEQ));
        ps.setLong(1, objectId);

        if (parent instanceof ValidationPlan) {
            ps.setLong(2, parentId);
            ps.setNull(3, Types.BIGINT);
        } else if (parent instanceof Requirement) {
            ps.setNull(2, Types.BIGINT);
            ps.setLong(3, parentId);
        }

        ps.setString(4, parameter.getName());
        ps.setString(5, parameter.getValue());
        ps.setString(6, parameter.getUom());

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize()) {
            helper.executeBatch(schemaMapper.getTableName(ADETable.PARAMETER));
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
