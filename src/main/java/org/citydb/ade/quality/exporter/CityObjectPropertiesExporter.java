/*
 * 3D City Database - The Open Source CityGML Database
 * https://www.3dcitydb.org/
 *
 * Copyright 2013 - 2022
 * Chair of Geoinformatics
 * Technical University of Munich, Germany
 * https://www.lrg.tum.de/gis/
 *
 * The 3D City Database is jointly developed with the following
 * cooperation partners:
 *
 * Virtual City Systems, Berlin <https://vc.systems/>
 * M.O.S.S. Computer Grafik Systeme GmbH, Taufkirchen <http://www.moss.de/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
