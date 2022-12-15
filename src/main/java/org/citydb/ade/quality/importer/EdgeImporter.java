package org.citydb.ade.quality.importer;

import org.citydb.ade.quality.schema.ADESequence;
import org.citydb.ade.quality.schema.ADETable;
import org.citydb.ade.quality.schema.SchemaMapper;
import org.citydb.core.ade.importer.ADEImporter;
import org.citydb.core.ade.importer.CityGMLImportHelper;
import org.citydb.core.operation.importer.CityGMLImportException;
import org.citygml4j.ade.quality.model.Edge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class EdgeImporter implements ADEImporter {
    private final CityGMLImportHelper helper;
    private final ImportManager manager;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;

    private int batchCounter;

    public EdgeImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws SQLException {
        this.helper = helper;
        this.manager = manager;
        schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(schemaMapper.getTableName(ADETable.EDGE)) + " " +
                "(id, edgelist_edge_id, from_, to_) " +
                "values (?, ?, ?, ?)");
    }

    public long doImport(Edge edge, long parentId) throws CityGMLImportException, SQLException {
        long objectId = helper.getNextSequenceValue(schemaMapper.getSequenceName(ADESequence.EDGE_SEQ));
        ps.setLong(1, objectId);

        if (parentId != 0) {
            ps.setLong(2, parentId);
        } else {
            ps.setNull(2, Types.BIGINT);
        }

        ps.setString(3, manager.getVertex(edge.getFrom()));
        ps.setString(4, manager.getVertex(edge.getTo()));

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize()) {
            helper.executeBatch(schemaMapper.getTableName(ADETable.EDGE));
        }

        return objectId;
    }

    public long doImport(Edge edge) throws CityGMLImportException, SQLException {
        return doImport(edge, 0);
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
