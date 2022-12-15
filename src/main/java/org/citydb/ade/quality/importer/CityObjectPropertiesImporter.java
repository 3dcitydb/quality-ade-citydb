package org.citydb.ade.quality.importer;

import org.citydb.ade.quality.schema.ADETable;
import org.citydb.ade.quality.schema.SchemaMapper;
import org.citydb.core.ade.importer.ADEImporter;
import org.citydb.core.ade.importer.ADEPropertyCollection;
import org.citydb.core.ade.importer.CityGMLImportHelper;
import org.citydb.core.operation.importer.CityGMLImportException;
import org.citygml4j.ade.quality.model.ValidationResult;
import org.citygml4j.ade.quality.model.ValidationResultPropertyElement;
import org.citygml4j.model.gml.feature.AbstractFeature;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CityObjectPropertiesImporter implements ADEImporter {
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;
    private final ValidationResultImporter validationResultImporter;

    private int batchCounter;

    public CityObjectPropertiesImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws SQLException {
        this.helper = helper;
        schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(schemaMapper.getTableName(ADETable.CITYOBJECT)) + " " +
                "(id) values (?)");

        validationResultImporter = manager.getImporter(ValidationResultImporter.class);
    }

    public void doImport(ADEPropertyCollection properties, AbstractFeature parent, long parentId) throws CityGMLImportException, SQLException {
        ps.setLong(1, parentId);

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize()) {
            helper.executeBatch(schemaMapper.getTableName(ADETable.CITYOBJECT));
        }

        if (properties.contains(ValidationResultPropertyElement.class)) {
            for (ValidationResultPropertyElement propertyElement : properties.getAll(ValidationResultPropertyElement.class)) {
                ValidationResult validationResult = propertyElement.getValue().getObject();
                if (validationResult != null) {
                    validationResultImporter.doImport(validationResult, parent, parentId);
                }
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
