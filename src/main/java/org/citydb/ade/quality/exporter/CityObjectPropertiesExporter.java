package org.citydb.ade.quality.exporter;

import org.citydb.core.ade.exporter.ADEExporter;
import org.citydb.core.operation.exporter.CityGMLExportException;
import org.citydb.core.query.filter.projection.ProjectionFilter;
import org.citygml4j.ade.quality.QualityADEModule;
import org.citygml4j.ade.quality.model.ValidationResult;
import org.citygml4j.ade.quality.model.ValidationResultProperty;
import org.citygml4j.ade.quality.model.ValidationResultPropertyElement;
import org.citygml4j.model.citygml.core.AbstractCityObject;

import java.sql.SQLException;

public class CityObjectPropertiesExporter implements ADEExporter {
    private final ValidationResultExporter validationResultExporter;

    public CityObjectPropertiesExporter(ExportManager manager) throws SQLException {
        validationResultExporter = manager.getExporter(ValidationResultExporter.class);
    }

    public void doExport(AbstractCityObject parent, long parentId, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        if (projectionFilter.containsProperty("validationResult", QualityADEModule.v0_1_4.getNamespaceURI())) {
            for (ValidationResult validationResult : validationResultExporter.doExport(parentId)) {
                ValidationResultProperty property = new ValidationResultProperty(validationResult);
                ValidationResultPropertyElement propertyElement = new ValidationResultPropertyElement(property);
                parent.getGenericApplicationPropertyOfCityObject().add(propertyElement);
            }
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
    }
}
