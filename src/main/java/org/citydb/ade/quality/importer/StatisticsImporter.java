package org.citydb.ade.quality.importer;

import org.citydb.ade.quality.schema.ADESequence;
import org.citydb.ade.quality.schema.ADETable;
import org.citydb.ade.quality.schema.SchemaMapper;
import org.citydb.core.ade.importer.ADEImporter;
import org.citydb.core.ade.importer.CityGMLImportHelper;
import org.citydb.core.operation.importer.CityGMLImportException;
import org.citygml4j.ade.quality.model.Error;
import org.citygml4j.ade.quality.model.FeatureStatistics;
import org.citygml4j.ade.quality.model.Statistics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class StatisticsImporter implements ADEImporter {
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;
    private final FeatureStatisticsImporter featureStatisticsImporter;
    private final StatisticsErrorImporter statisticsErrorImporter;

    private int batchCounter;

    public StatisticsImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws SQLException {
        this.helper = helper;
        schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(schemaMapper.getTableName(ADETable.STATISTICS)) + " " +
                "(id, numerrorbuildings_id, numerrorbridgeobjects_id, numerrorlandobjects_id, " +
                "numerrortransportation_id, numerrorvegetation_id, numerrorwaterobjects_id) " +
                "values (?, ?, ?, ?, ?, ?, ?)");

        featureStatisticsImporter = manager.getImporter(FeatureStatisticsImporter.class);
        statisticsErrorImporter = manager.getImporter(StatisticsErrorImporter.class);
    }

    public long doImport(Statistics statistics) throws CityGMLImportException, SQLException {
        long objectId = helper.getNextSequenceValue(schemaMapper.getSequenceName(ADESequence.STATISTICS_SEQ));
        ps.setLong(1, objectId);

        insert(2, statistics.getNumErrorBuildings(), ps);
        insert(3, statistics.getNumErrorBridgeObjects(), ps);
        insert(4, statistics.getNumErrorLandObjects(), ps);
        insert(5, statistics.getNumErrorTransportation(), ps);
        insert(6, statistics.getNumErrorVegetation(), ps);
        insert(7, statistics.getNumErrorWaterObjects(), ps);

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize()) {
            helper.executeBatch(schemaMapper.getTableName(ADETable.STATISTICS));
        }

        if (statistics.isSetErrors()) {
            for (Error error : statistics.getErrors()) {
                statisticsErrorImporter.doImport(error, objectId);
            }
        }

        return objectId;
    }

    private void insert(int index, FeatureStatistics featureStatistics, PreparedStatement ps) throws CityGMLImportException, SQLException {
        if (featureStatistics != null) {
            ps.setLong(index, featureStatisticsImporter.doImport(featureStatistics));
        } else {
            ps.setNull(index, Types.BIGINT);
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
