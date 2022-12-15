package org.citydb.ade.quality;

import org.citydb.ade.quality.exporter.ExportManager;
import org.citydb.ade.quality.importer.ImportManager;
import org.citydb.ade.quality.schema.ObjectMapper;
import org.citydb.ade.quality.schema.SchemaMapper;
import org.citydb.core.ade.ADEExtension;
import org.citydb.core.ade.ADEObjectMapper;
import org.citydb.core.ade.exporter.ADEExportManager;
import org.citydb.core.ade.importer.ADEImportManager;
import org.citydb.core.database.schema.mapping.SchemaMapping;
import org.citydb.gui.ImpExpLauncher;
import org.citygml4j.ade.quality.QualityADEContext;
import org.citygml4j.model.citygml.ade.binding.ADEContext;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class QualityADEExtension extends ADEExtension {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SchemaMapper schemaMapper = new SchemaMapper();
    private final QualityADEContext context = new QualityADEContext();

    public static void main(String[] args) {
        QualityADEExtension adeExtension = new QualityADEExtension();
        adeExtension.setBasePath(Paths.get("resources/database").toAbsolutePath());
        new ImpExpLauncher().withArgs(args)
                .withADEExtension(adeExtension)
                .start();
    }

    @Override
    public void init(SchemaMapping schemaMapping) {
        objectMapper.populateObjectClassIds(schemaMapping);
        schemaMapper.populateSchemaNames(schemaMapping.getMetadata().getDBPrefix());
    }

    @Override
    public List<ADEContext> getADEContexts() {
        return Collections.singletonList(context);
    }

    @Override
    public ADEObjectMapper getADEObjectMapper() {
        return objectMapper;
    }

    @Override
    public ADEImportManager createADEImportManager() {
        return new ImportManager(this, objectMapper, schemaMapper);
    }

    @Override
    public ADEExportManager createADEExportManager() {
        return new ExportManager(objectMapper, schemaMapper);
    }
}
