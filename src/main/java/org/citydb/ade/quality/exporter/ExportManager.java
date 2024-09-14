/*
 * 3D City Database - The Open Source CityGML Database
 * https://www.3dcitydb.org/
 *
 * Copyright 2013 - 2024
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

import org.citydb.ade.quality.schema.ADETable;
import org.citydb.ade.quality.schema.ObjectMapper;
import org.citydb.ade.quality.schema.SchemaMapper;
import org.citydb.core.ade.exporter.ADEExportManager;
import org.citydb.core.ade.exporter.ADEExporter;
import org.citydb.core.ade.exporter.CityGMLExportHelper;
import org.citydb.core.database.schema.mapping.AbstractObjectType;
import org.citydb.core.database.schema.mapping.FeatureType;
import org.citydb.core.operation.exporter.CityGMLExportException;
import org.citydb.core.query.filter.projection.ProjectionFilter;
import org.citygml4j.model.citygml.ade.binding.ADEModelObject;
import org.citygml4j.model.citygml.core.AbstractCityObject;
import org.citygml4j.model.gml.feature.AbstractFeature;
import org.citygml4j.model.gml.geometry.primitives.DirectPosition;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExportManager implements ADEExportManager {
    private final ObjectMapper objectMapper;
    private final SchemaMapper schemaMapper;
    private final Map<Class<? extends ADEExporter>, ADEExporter> exporters = new HashMap<>();

    private Connection connection;
    private CityGMLExportHelper helper;

    public ExportManager(ObjectMapper objectMapper, SchemaMapper schemaMapper) {
        this.objectMapper = objectMapper;
        this.schemaMapper = schemaMapper;
    }

    @Override
    public void init(Connection connection, CityGMLExportHelper helper) throws CityGMLExportException, SQLException {
        this.connection = connection;
        this.helper = helper;
    }

    @Override
    public void exportObject(ADEModelObject object, long objectId, AbstractObjectType<?> objectType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
    }

    @Override
    public void exportGenericApplicationProperties(String adeHookTable, AbstractFeature parent, long parentId, FeatureType parentType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        if (adeHookTable.equals(schemaMapper.getTableName(ADETable.CITYOBJECT)) && parent instanceof AbstractCityObject) {
            getExporter(CityObjectPropertiesExporter.class).doExport((AbstractCityObject) parent, parentId, projectionFilter);
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        for (ADEExporter exporter : exporters.values()) {
            exporter.close();
        }
    }

    ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    SchemaMapper getSchemaMapper() {
        return schemaMapper;
    }

    DirectPosition getVertex(String vertex) {
        if (vertex != null) {
            List<Double> coordinates = new ArrayList<>();
            for (String coordinate : vertex.split(" ")) {
                try {
                    coordinates.add(Double.parseDouble(coordinate));
                } catch (NumberFormatException e) {
                    //
                }
            }

            if (coordinates.size() == 3) {
                DirectPosition position = new DirectPosition();
                position.setValue(coordinates);
                return position;
            }
        }

        return null;
    }

    <T extends ADEExporter> T getExporter(Class<T> type) throws SQLException {
        ADEExporter exporter = exporters.get(type);
        if (exporter == null) {
            if (type == CityObjectPropertiesExporter.class) {
                exporter = new CityObjectPropertiesExporter(this);
            } else if (type == ValidationExporter.class) {
                exporter = new ValidationExporter(connection, helper, this);
            } else if (type == ValidationResultExporter.class) {
                exporter = new ValidationResultExporter(connection, helper, this);
            }

            if (exporter == null) {
                throw new SQLException("Failed to build ADE exporter of type " + type.getName() + ".");
            }

            exporters.put(type, exporter);
        }

        return type.cast(exporter);
    }
}
