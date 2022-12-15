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

package org.citydb.ade.quality.importer;

import org.citydb.ade.quality.schema.ADETable;
import org.citydb.ade.quality.schema.ObjectMapper;
import org.citydb.ade.quality.schema.SchemaMapper;
import org.citydb.core.ade.ADEExtension;
import org.citydb.core.ade.importer.*;
import org.citydb.core.database.schema.mapping.AbstractObjectType;
import org.citydb.core.database.schema.mapping.FeatureType;
import org.citydb.core.operation.importer.CityGMLImportException;
import org.citygml4j.ade.quality.model.Validation;
import org.citygml4j.ade.quality.model.ValidationResultPropertyElement;
import org.citygml4j.model.citygml.ade.binding.ADEModelObject;
import org.citygml4j.model.citygml.core.AbstractCityObject;
import org.citygml4j.model.gml.feature.AbstractFeature;
import org.citygml4j.model.gml.geometry.primitives.DirectPosition;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ImportManager implements ADEImportManager {
    private final ADEExtension adeExtension;
    private final ObjectMapper objectMapper;
    private final SchemaMapper schemaMapper;
    private final Map<Class<? extends ADEImporter>, ADEImporter> importers = new HashMap<>();

    private Connection connection;
    private CityGMLImportHelper helper;

    public ImportManager(ADEExtension adeExtension, ObjectMapper objectMapper, SchemaMapper schemaMapper) {
        this.adeExtension = adeExtension;
        this.objectMapper = objectMapper;
        this.schemaMapper = schemaMapper;
    }

    @Override
    public void init(Connection connection, CityGMLImportHelper helper) {
        this.connection = connection;
        this.helper = helper;
    }

    @Override
    public void importObject(ADEModelObject object, long objectId, AbstractObjectType<?> objectType, ForeignKeys foreignKeys) throws CityGMLImportException, SQLException {
        if (object instanceof Validation) {
            getImporter(ValidationImporter.class).doImport((Validation) object, objectId, objectType, foreignKeys);
        }
    }

    @Override
    public void importGenericApplicationProperties(ADEPropertyCollection properties, AbstractFeature parent, long parentId, FeatureType parentType) throws CityGMLImportException, SQLException {
        if (parent instanceof AbstractCityObject && properties.contains(ValidationResultPropertyElement.class)) {
            getImporter(CityObjectPropertiesImporter.class).doImport(properties, parent, parentId);
        }
    }

    @Override
    public void executeBatch(String tableName) throws CityGMLImportException, SQLException {
        ADETable adeTable = schemaMapper.fromTableName(tableName);
        if (adeTable != null) {
            ADEImporter importer = importers.get(adeTable.getImporterClass());
            if (importer != null) {
                importer.executeBatch();
            }
        } else {
            throw new CityGMLImportException("The table " + tableName + " is not managed by the ADE extension for '" +
                    adeExtension.getMetadata().getIdentifier() + "'.");
        }
    }

    @Override
    public void close() throws CityGMLImportException, SQLException {
        for (ADEImporter importer : importers.values()) {
            importer.close();
        }
    }

    ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    SchemaMapper getSchemaMapper() {
        return schemaMapper;
    }

    String getVertex(DirectPosition position) {
        return position != null ? position.toList3d().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" ")) : null;
    }

    <T extends ADEImporter> T getImporter(Class<T> type) throws SQLException {
        ADEImporter importer = importers.get(type);
        if (importer == null) {
            if (type == CheckingImporter.class) {
                importer = new CheckingImporter(connection, helper, this);
            } else if (type == CityObjectPropertiesImporter.class) {
                importer = new CityObjectPropertiesImporter(connection, helper, this);
            } else if (type == EdgeImporter.class) {
                importer = new EdgeImporter(connection, helper, this);
            } else if (type == EdgeListImporter.class) {
                importer = new EdgeListImporter(connection, helper, this);
            } else if (type == ErrorImporter.class) {
                importer = new ErrorImporter(connection, helper, this);
            } else if (type == FeatureStatisticsImporter.class) {
                importer = new FeatureStatisticsImporter(connection, helper, this);
            } else if (type == FilterImporter.class) {
                importer = new FilterImporter(connection, helper, this);
            } else if (type == GlobalParametersImporter.class) {
                importer = new GlobalParametersImporter(connection, helper, this);
            } else if (type == ParameterImporter.class) {
                importer = new ParameterImporter(connection, helper, this);
            } else if (type == PolygonErrorImporter.class) {
                importer = new PolygonErrorImporter(connection, helper, this);
            } else if (type == PolygonIdListImporter.class) {
                importer = new PolygonIdListImporter(connection, helper, this);
            } else if (type == RequirementImporter.class) {
                importer = new RequirementImporter(connection, helper, this);
            } else if (type == RingErrorImporter.class) {
                importer = new RingErrorImporter(connection, helper, this);
            } else if (type == SemanticErrorImporter.class) {
                importer = new SemanticErrorImporter(connection, helper, this);
            } else if (type == SolidErrorImporter.class) {
                importer = new SolidErrorImporter(connection, helper, this);
            } else if (type == StatisticsErrorImporter.class) {
                importer = new StatisticsErrorImporter(connection, helper, this);
            } else if (type == StatisticsImporter.class) {
                importer = new StatisticsImporter(connection, helper, this);
            } else if (type == ValidationImporter.class) {
                importer = new ValidationImporter(connection, helper, this);
            } else if (type == ValidationPlanImporter.class) {
                importer = new ValidationPlanImporter(connection, helper, this);
            } else if (type == ValidationResultImporter.class) {
                importer = new ValidationResultImporter(connection, helper, this);
            }

            if (importer == null) {
                throw new SQLException("Failed to build ADE importer of type " + type.getName() + ".");
            }

            importers.put(type, importer);
        }

        return type.cast(importer);
    }
}
