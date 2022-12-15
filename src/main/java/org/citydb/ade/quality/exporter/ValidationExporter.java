package org.citydb.ade.quality.exporter;

import org.citydb.ade.quality.schema.ADETable;
import org.citydb.core.ade.exporter.ADEExporter;
import org.citydb.core.ade.exporter.CityGMLExportHelper;
import org.citydb.core.operation.exporter.CityGMLExportException;
import org.citydb.core.query.filter.projection.CombinedProjectionFilter;
import org.citydb.core.query.filter.projection.ProjectionFilter;
import org.citydb.sqlbuilder.expression.PlaceHolder;
import org.citydb.sqlbuilder.schema.Table;
import org.citydb.sqlbuilder.select.Select;
import org.citydb.sqlbuilder.select.join.JoinFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonName;
import org.citygml4j.ade.quality.QualityADEModule;
import org.citygml4j.ade.quality.model.Error;
import org.citygml4j.ade.quality.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ValidationExporter implements ADEExporter {
    private final CityGMLExportHelper helper;
    private final PreparedStatement ps;
    private final String module;

    public ValidationExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws SQLException {
        this.helper = helper;

        String tableName = manager.getSchemaMapper().getTableName(ADETable.VALIDATION);
        CombinedProjectionFilter projectionFilter = helper.getCombinedProjectionFilter(tableName);
        module = QualityADEModule.v0_1_4.getNamespaceURI();

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Table validationPlan = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.VALIDATIONPLAN)));
        Table parameter = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.PARAMETER)));
        Table checking = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.CHECKING)));
        Table requirement = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.REQUIREMENT)));
        Table reqParameter = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.PARAMETER)));

        Select select = new Select().addProjection(table.getColumn("id"), table.getColumn("validationdate"),
                        table.getColumn("validationsoftware"), table.getColumn("validationplan_id"),
                        parameter.getColumn("id", "gp_id"), parameter.getColumn("name", "gp_name"),
                        parameter.getColumn("value", "gp_value"), parameter.getColumn("uom", "gp_uom"),
                        checking.getColumn("id", "cid"), checking.getColumn("featuretype"),
                        requirement.getColumn("id", "rid"), requirement.getColumn("requirementtype"),
                        requirement.getColumn("enabled"),
                        reqParameter.getColumn("id", "rp_id"), reqParameter.getColumn("name", "rp_name"),
                        reqParameter.getColumn("value", "rp_value"), reqParameter.getColumn("uom", "rp_uom"))
                .addJoin(JoinFactory.left(validationPlan, "id", ComparisonName.EQUAL_TO, table.getColumn("validationplan_id")))
                .addJoin(JoinFactory.left(checking, "filter_checking_id", ComparisonName.EQUAL_TO, validationPlan.getColumn("filter_id")))
                .addJoin(JoinFactory.left(parameter, "globalparameter_parameter_id", ComparisonName.EQUAL_TO, validationPlan.getColumn("globalparameters_id")))
                .addJoin(JoinFactory.left(requirement, "validationpla_requirement_id", ComparisonName.EQUAL_TO, validationPlan.getColumn("id")))
                .addJoin(JoinFactory.left(reqParameter, "requirement_parameter_id", ComparisonName.EQUAL_TO, requirement.getColumn("id")));

        if (projectionFilter.containsProperty("statistics", module)) {
            Table statistics = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.STATISTICS)));
            Table buildings = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.FEATURESTATISTICS)));
            Table bridges = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.FEATURESTATISTICS)));
            Table landObjects = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.FEATURESTATISTICS)));
            Table transportation = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.FEATURESTATISTICS)));
            Table vegetation = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.FEATURESTATISTICS)));
            Table waterObjects = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.FEATURESTATISTICS)));
            Table error = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.ERROR_1)));

            select.addProjection(table.getColumn("statistics_id"),
                            buildings.getColumn("numchecked", "bu_numchecked"), buildings.getColumn("numerrors", "bu_numerrors"),
                            bridges.getColumn("numchecked", "br_numchecked"), bridges.getColumn("numerrors", "br_numerrors"),
                            landObjects.getColumn("numchecked", "lo_numchecked"), landObjects.getColumn("numerrors", "lo_numerrors"),
                            transportation.getColumn("numchecked", "tr_numchecked"), transportation.getColumn("numerrors", "tr_numerrors"),
                            vegetation.getColumn("numchecked", "ve_numchecked"), vegetation.getColumn("numerrors", "ve_numerrors"),
                            waterObjects.getColumn("numchecked", "wo_numchecked"), waterObjects.getColumn("numerrors", "wo_numerrors"),
                            error.getColumn("id", "eid"), error.getColumn("name"), error.getColumn("occurrences"))
                    .addJoin(JoinFactory.left(statistics, "id", ComparisonName.EQUAL_TO, table.getColumn("statistics_id")))
                    .addJoin(JoinFactory.left(buildings, "id", ComparisonName.EQUAL_TO, statistics.getColumn("numerrorbuildings_id")))
                    .addJoin(JoinFactory.left(bridges, "id", ComparisonName.EQUAL_TO, statistics.getColumn("numerrorbridgeobjects_id")))
                    .addJoin(JoinFactory.left(landObjects, "id", ComparisonName.EQUAL_TO, statistics.getColumn("numerrorlandobjects_id")))
                    .addJoin(JoinFactory.left(transportation, "id", ComparisonName.EQUAL_TO, statistics.getColumn("numerrortransportation_id")))
                    .addJoin(JoinFactory.left(vegetation, "id", ComparisonName.EQUAL_TO, statistics.getColumn("numerrorvegetation_id")))
                    .addJoin(JoinFactory.left(waterObjects, "id", ComparisonName.EQUAL_TO, statistics.getColumn("numerrorwaterobjects_id")))
                    .addJoin(JoinFactory.left(error, "statistics_error_id", ComparisonName.EQUAL_TO, statistics.getColumn("id")));
        }

        select.addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        ps = connection.prepareStatement(select.toString());
    }

    public Validation doExport(long objectId, int objectClassId) throws CityGMLExportException, SQLException {
        ps.setLong(1, objectId);

        try (ResultSet rs = ps.executeQuery()) {
            Validation validation = null;
            ProjectionFilter projectionFilter = helper.getProjectionFilter(helper.getFeatureType(objectClassId));
            boolean initialized = false;

            Set<Long> globalParameterIds = new HashSet<>();
            Set<Long> checkingIds = new HashSet<>();
            Map<Long, Requirement> requirements = new HashMap<>();
            Set<Long> requirementParameterIds = new HashSet<>();
            Set<Long> errorIds = new HashSet<>();

            while (rs.next()) {
                if (!initialized) {
                    validation = helper.createObject(objectId, objectClassId, Validation.class);
                    if (validation == null) {
                        helper.logOrThrowErrorMessage("Failed to instantiate " +
                                helper.getObjectSignature(objectClassId, objectId) +
                                " as validation object.");
                        return null;
                    }

                    OffsetDateTime validationDate = rs.getObject("validationdate", OffsetDateTime.class);
                    if (!rs.wasNull()) {
                        validation.setValidationDate(validationDate.atZoneSameInstant(ZoneOffset.UTC));
                    }

                    validation.setValidationSoftware(rs.getString("validationsoftware"));

                    rs.getLong("validationplan_id");
                    if (!rs.wasNull()) {
                        validation.setValidationPlan(new ValidationPlan());
                    }

                    if (projectionFilter.containsProperty("statistics", module)) {
                        rs.getLong("statistics_id");
                        if (!rs.wasNull()) {
                            Statistics statistics = new Statistics();
                            statistics.setNumErrorBuildings(createFeatureStatistics("bu_numchecked", "bu_numerrors", rs));
                            statistics.setNumErrorBridgeObjects(createFeatureStatistics("br_numchecked", "br_numerrors", rs));
                            statistics.setNumErrorLandObjects(createFeatureStatistics("lo_numchecked", "lo_numerrors", rs));
                            statistics.setNumErrorTransportation(createFeatureStatistics("tr_numchecked", "tr_numerrors", rs));
                            statistics.setNumErrorVegetation(createFeatureStatistics("ve_numchecked", "ve_numerrors", rs));
                            statistics.setNumErrorWaterObjects(createFeatureStatistics("wo_numchecked", "wo_numerrors", rs));
                            validation.setStatistics(statistics);
                        }
                    }

                    initialized = true;
                }

                if (validation.getValidationPlan() != null) {
                    long globalParametersId = rs.getLong("gp_id");
                    if (!rs.wasNull() && globalParameterIds.add(globalParametersId)) {
                        Parameter globalParameter = createParameter("gp_name", "gp_value", "gp_uom", rs);
                        if (globalParameter != null) {
                            validation.getValidationPlan().getGlobalParameters().add(globalParameter);
                        }
                    }

                    long checkingId = rs.getLong("cid");
                    if (!rs.wasNull() && checkingIds.add(checkingId)) {
                        FeatureType featureType = FeatureType.fromValue(rs.getString("featuretype"));
                        if (featureType != null) {
                            Filter filter = validation.getValidationPlan().getFilter();
                            if (filter == null) {
                                filter = new Filter();
                                validation.getValidationPlan().setFilter(filter);
                            }

                            filter.getChecking().add(featureType);
                        }
                    }

                    Requirement requirement = null;
                    long requirementId = rs.getLong("rid");
                    if (!rs.wasNull()) {
                        requirement = requirements.get(requirementId);
                        if (requirement == null) {
                            RequirementType type = RequirementType.fromValue(rs.getString("requirementtype"));
                            if (type != null) {
                                requirement = new Requirement(type, rs.getBoolean("enabled"));
                                requirements.put(requirementId, requirement);
                            }
                        }
                    }

                    long requirementParameterId = rs.getLong("rp_id");
                    if (!rs.wasNull() && requirementParameterIds.add(requirementParameterId)) {
                        Parameter requirementParameter = createParameter("rp_name", "rp_value", "rp_uom", rs);
                        if (requirementParameter != null && requirement != null) {
                            requirement.getParameters().add(requirementParameter);
                        }
                    }
                }

                if (projectionFilter.containsProperty("statistics", module)) {
                    long errorId = rs.getLong("eid");
                    if (!rs.wasNull() && errorIds.add(errorId) && validation.getStatistics() != null) {
                        ErrorType name = ErrorType.fromValue(rs.getString("name"));
                        if (name != null) {
                            validation.getStatistics().getErrors().add(new Error(rs.getInt("occurrences"), name));
                        }
                    }
                }
            }

            if (!requirements.isEmpty() && validation.getValidationPlan() != null) {
                requirements.values().forEach(validation.getValidationPlan().getRequirements()::add);
            }

            return validation;
        }
    }

    private FeatureStatistics createFeatureStatistics(String numCheckedValue, String numErrorsValue, ResultSet rs) throws SQLException {
        int numChecked = rs.getInt(numCheckedValue);
        if (rs.wasNull()) {
            return null;
        }

        int numErrors = rs.getInt(numErrorsValue);
        if (rs.wasNull()) {
            return null;
        }

        return new FeatureStatistics(numChecked, numErrors);
    }

    private Parameter createParameter(String nameValue, String valueValue, String uomValue, ResultSet rs) throws SQLException {
        String name = rs.getString(nameValue);
        if (rs.wasNull()) {
            return null;
        }

        String value = rs.getString(valueValue);
        if (rs.wasNull()) {
            return null;
        }

        return new Parameter(name, value, rs.getString(uomValue));
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
