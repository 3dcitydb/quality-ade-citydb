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
