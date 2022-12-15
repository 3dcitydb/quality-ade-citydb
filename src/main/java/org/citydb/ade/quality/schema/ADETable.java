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

package org.citydb.ade.quality.schema;

import org.citydb.ade.quality.importer.*;
import org.citydb.core.ade.importer.ADEImporter;

public enum ADETable {
    CHECKING(CheckingImporter.class),
    CITYOBJECT(CityObjectPropertiesImporter.class),
    EDGE(EdgeImporter.class),
    EDGELIST(EdgeListImporter.class),
    ERROR(ErrorImporter.class),
    ERROR_1(StatisticsErrorImporter.class),
    FEATURESTATISTICS(FeatureStatisticsImporter.class),
    FILTER(FilterImporter.class),
    GEOMETRYERROR(ErrorImporter.class),
    GLOBALPARAMETERS(GlobalParametersImporter.class),
    PARAMETER(ParameterImporter.class),
    POLYGONERROR(PolygonErrorImporter.class),
    POLYGONIDLIST(PolygonIdListImporter.class),
    REQUIREMENT(RequirementImporter.class),
    RINGERROR(RingErrorImporter.class),
    SEMANTICERROR(SemanticErrorImporter.class),
    SOLIDERROR(SolidErrorImporter.class),
    STATISTICS(StatisticsImporter.class),
    VALIDATION(ValidationImporter.class),
    VALIDATIONPLAN(ValidationPlanImporter.class),
    VALIDATIONRESULT(ValidationResultImporter.class);

    private final Class<? extends ADEImporter> importerClass;

    ADETable(Class<? extends ADEImporter> importerClass) {
        this.importerClass = importerClass;
    }

    public Class<? extends ADEImporter> getImporterClass() {
        return importerClass;
    }
}
