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
import org.citydb.core.ade.exporter.ADEExporter;
import org.citydb.core.ade.exporter.CityGMLExportHelper;
import org.citydb.core.database.schema.mapping.MappingConstants;
import org.citydb.core.operation.exporter.CityGMLExportException;
import org.citydb.core.operation.exporter.util.AttributeValueSplitter;
import org.citydb.core.operation.exporter.util.SplitValue;
import org.citydb.sqlbuilder.expression.PlaceHolder;
import org.citydb.sqlbuilder.schema.Table;
import org.citydb.sqlbuilder.select.Select;
import org.citydb.sqlbuilder.select.join.JoinFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonName;
import org.citygml4j.ade.quality.model.*;
import org.citygml4j.model.gml.base.Reference;
import org.citygml4j.model.gml.geometry.primitives.DirectPosition;
import org.citygml4j.model.gml.measures.Angle;
import org.citygml4j.model.gml.measures.Length;
import org.citygml4j.util.gmlid.DefaultGMLIdManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ValidationResultExporter implements ADEExporter {
    private final CityGMLExportHelper helper;
    private final ExportManager manager;
    private final ObjectMapper objectMapper;
    private final PreparedStatement ps;
    private final ValidationExporter validationExporter;
    private final AttributeValueSplitter valueSplitter;

    public ValidationResultExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws SQLException {
        this.helper = helper;
        this.manager = manager;
        objectMapper = manager.getObjectMapper();

        Table table = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.VALIDATIONRESULT)));
        Table error = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.ERROR)));
        Table ringError = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.RINGERROR)));
        Table ringEdge1 = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.EDGE)));
        Table ringEdge2 = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.EDGE)));
        Table polygonError = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.POLYGONERROR)));
        Table solidError = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.SOLIDERROR)));
        Table solidEdge = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.EDGE)));
        Table polygonIdList = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.POLYGONIDLIST)));
        Table semanticError = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.SEMANTICERROR)));
        Table cityObject = new Table(helper.getTableNameWithSchema(MappingConstants.CITYOBJECT));

        Select select = new Select().addProjection(table.getColumn("id"), table.getColumn("resulttype"),
                        table.getColumn("validationplanid_id", "vid"), cityObject.getColumn("gmlid"),
                        error.getColumn("id", "eid"), error.getColumn("objectclass_id"),
                        ringError.getColumn("linearringid", "r_linearringid"), ringError.getColumn("type"),
                        ringError.getColumn("vertex1"), ringError.getColumn("vertex2"),
                        ringEdge1.getColumn("from_", "edge1_from"), ringEdge1.getColumn("to_", "edge1_to"),
                        ringEdge2.getColumn("from_", "edge2_from"), ringEdge2.getColumn("to_", "edge2_to"),
                        polygonError.getColumn("polygonid"), polygonError.getColumn("linearringid", "p_linearringid"),
                        polygonError.getColumn("deviation"), polygonError.getColumn("deviation_uom"),
                        polygonError.getColumn("distance"), polygonError.getColumn("distance_uom"),
                        polygonError.getColumn("linearringid1"), polygonError.getColumn("linearringid2"),
                        polygonError.getColumn("vertex", "p_vertex"),
                        solidError.getColumn("geometryid"), solidError.getColumn("polygonid1"),
                        solidError.getColumn("polygonid2"), solidError.getColumn("vertex", "s_vertex"),
                        solidEdge.getColumn("id", "s_eid"), solidEdge.getColumn("from_", "s_edge_from"),
                        solidEdge.getColumn("to_", "s_edge_to"),
                        polygonIdList.getColumn("id", "s_pid"), polygonIdList.getColumn("polygonid", "s_polygonid"),
                        semanticError.getColumn("attributename"), semanticError.getColumn("childid"),
                        semanticError.getColumn("generic"))
                .addJoin(JoinFactory.left(error, "validationresult_error_id", ComparisonName.EQUAL_TO, table.getColumn("id")))
                .addJoin(JoinFactory.left(ringError, "id", ComparisonName.EQUAL_TO, error.getColumn("id")))
                .addJoin(JoinFactory.left(polygonError, "id", ComparisonName.EQUAL_TO, error.getColumn("id")))
                .addJoin(JoinFactory.left(solidError, "id", ComparisonName.EQUAL_TO, error.getColumn("id")))
                .addJoin(JoinFactory.left(semanticError, "id", ComparisonName.EQUAL_TO, error.getColumn("id")))
                .addJoin(JoinFactory.left(ringEdge1, "id", ComparisonName.EQUAL_TO, ringError.getColumn("edge1_id")))
                .addJoin(JoinFactory.left(ringEdge2, "id", ComparisonName.EQUAL_TO, ringError.getColumn("edge2_id")))
                .addJoin(JoinFactory.left(solidEdge, "edgelist_edge_id", ComparisonName.EQUAL_TO, solidError.getColumn("edges_id")))
                .addJoin(JoinFactory.left(polygonIdList, "soliderror_component_id", ComparisonName.EQUAL_TO, solidError.getColumn("id")))
                .addJoin(JoinFactory.left(cityObject, "id", ComparisonName.EQUAL_TO, table.getColumn("validationplanid_id")));

        select.addSelection(ComparisonFactory.equalTo(table.getColumn("cityobject_validationresu_id"), new PlaceHolder<>()));
        ps = connection.prepareStatement(select.toString());

        validationExporter = manager.getExporter(ValidationExporter.class);
        valueSplitter = helper.getAttributeValueSplitter();
    }

    public Collection<ValidationResult> doExport(long parentId) throws CityGMLExportException, SQLException {
        ps.setLong(1, parentId);
        try (ResultSet rs = ps.executeQuery()) {
            long currentValidationResultId = 0;
            ValidationResult validationResult = null;
            Map<Long, ValidationResult> validationResults = new LinkedHashMap<>();

            long currentErrorId = 0;
            AbstractError error = null;
            Map<Long, AbstractError> errors = new LinkedHashMap<>();

            Map<Long, Set<Long>> solidEdges = new LinkedHashMap<>();
            Map<Long, Set<Long>> solidPolygons = new LinkedHashMap<>();

            while (rs.next()) {
                long validationResultId = rs.getLong("id");
                if (validationResultId != currentValidationResultId || validationResult == null) {
                    currentValidationResultId = validationResultId;
                    validationResult = validationResults.get(validationResultId);
                    if (validationResult == null) {
                        validationResult = new ValidationResult();
                        validationResult.setResultType(ResultType.fromValue(rs.getString("resulttype")));

                        long validationId = rs.getLong("vid");
                        if (!rs.wasNull()) {
                            String gmlId = rs.getString("gmlid");
                            int objectClassId = objectMapper.getObjectClassId(Validation.class);
                            if (gmlId == null || !helper.lookupAndPutObjectId(gmlId, validationId, objectClassId)) {
                                Validation validation = validationExporter.doExport(validationId, objectClassId);
                                if (validation != null) {
                                    if (gmlId == null) {
                                        gmlId = DefaultGMLIdManager.getInstance().generateUUID();
                                        validation.setId(gmlId);
                                    }

                                    helper.exportAsGlobalFeature(validation);
                                }
                            }

                            validationResult.setValidationPlanID(new Reference("#" + helper.replaceObjectId(gmlId)));
                        }

                        validationResults.put(validationResultId, validationResult);
                    }
                }

                long errorId = rs.getLong("eid");
                if (rs.wasNull()) {
                    continue;
                }

                if (errorId != currentErrorId || error == null) {
                    currentErrorId = errorId;
                    error = errors.get(errorId);
                    if (error == null) {
                        int objectClassId = rs.getInt("objectclass_id");
                        error = objectMapper.createObject(objectClassId, AbstractError.class);

                        if (error instanceof AbstractRingError) {
                            if (error instanceof RingConsecutivePointsSame) {
                                initialize((RingConsecutivePointsSame) error, rs);
                            } else if (error instanceof RingSelfIntersection) {
                                initialize((RingSelfIntersection) error, rs);
                            } else {
                                initialize((AbstractRingError) error, rs);
                            }
                        } else if (error instanceof AbstractPolygonError) {
                            if (error instanceof PolygonNonPlanarNormalsDeviation) {
                                initialize((PolygonNonPlanarNormalsDeviation) error, rs);
                            } else if (error instanceof PolygonOrientationRingsSame) {
                                initialize((PolygonOrientationRingsSame) error, rs);
                            } else if (error instanceof PolygonInnerRingsNested) {
                                initialize((PolygonInnerRingsNested) error, rs);
                            } else if (error instanceof PolygonNonPlanarDistancePlane) {
                                initialize((PolygonNonPlanarDistancePlane) error, rs);
                            } else if (error instanceof PolygonIntersectingRings) {
                                initialize((PolygonIntersectingRings) error, rs);
                            } else if (error instanceof PolygonHoleOutside) {
                                initialize((PolygonHoleOutside) error, rs);
                            } else {
                                initialize((AbstractPolygonError) error, rs);
                            }
                        } else if (error instanceof AbstractSolidError) {
                            if (error instanceof SolidNonManifoldVertex) {
                                initialize((SolidNonManifoldVertex) error, rs);
                            } else if (error instanceof SolidSelfIntersection) {
                                initialize((SolidSelfIntersection) error, rs);
                            } else {
                                initialize((AbstractSolidError) error, rs);
                            }
                        } else if (error instanceof SemanticAttributeWrongValue) {
                            initialize((SemanticAttributeWrongValue) error, rs);
                        } else if (error instanceof SemanticAttributeMissing) {
                            initialize((SemanticAttributeMissing) error, rs);
                        }

                        validationResult.getErrors().add(error);
                        errors.put(errorId, error);
                    }
                }

                if (error instanceof SolidNonManifoldEdge) {
                    long edgeId = rs.getLong("s_eid");
                    if (!rs.wasNull()
                            && solidEdges.computeIfAbsent(errorId, v -> new HashSet<>()).add(edgeId)) {
                        Edge edge = createEdge(rs.getString("s_edge_from"), rs.getString("s_edge_to"));
                        if (edge != null) {
                            ((SolidNonManifoldEdge) error).getEdges().add(edge);
                        }
                    }
                } else if (error instanceof SolidNotClosed) {
                    long edgeId = rs.getLong("s_eid");
                    if (!rs.wasNull()
                            && solidEdges.computeIfAbsent(errorId, v -> new HashSet<>()).add(edgeId)) {
                        Edge edge = createEdge(rs.getString("s_edge_from"), rs.getString("s_edge_to"));
                        if (edge != null) {
                            ((SolidNotClosed) error).getEdges().add(edge);
                        }
                    }
                } else if (error instanceof SolidMultipleConnectedComponents) {
                    long polygonListId = rs.getLong("s_pid");
                    if (!rs.wasNull()
                            && solidPolygons.computeIfAbsent(errorId, v -> new HashSet<>()).add(polygonListId)) {
                        List<SplitValue> values = valueSplitter.split(rs.getString("s_polygonid"));
                        if (!values.isEmpty()) {
                            PolygonIdList component = new PolygonIdList();
                            values.forEach(value -> component.getPolygonIds()
                                    .add(helper.replaceObjectId(value.result(0))));
                            ((SolidMultipleConnectedComponents) error).getComponents().add(component);
                        }
                    }
                } else if (error instanceof SolidPolygonWrongOrientation) {
                    long edgeId = rs.getLong("s_eid");
                    if (!rs.wasNull()
                            && solidEdges.computeIfAbsent(errorId, v -> new HashSet<>()).add(edgeId)) {
                        Edge edge = createEdge(rs.getString("s_edge_from"), rs.getString("s_edge_to"));
                        if (edge != null) {
                            ((SolidPolygonWrongOrientation) error).getEdges().add(edge);
                        }
                    }
                }
            }

            return validationResults.values();
        }
    }

    private void initialize(AbstractRingError error, ResultSet rs) throws SQLException {
        String linearRingId = helper.replaceObjectId(rs.getString("r_linearringid"));
        error.setLinearRingId(linearRingId);
    }

    private void initialize(RingConsecutivePointsSame error, ResultSet rs) throws SQLException {
        initialize((AbstractRingError) error, rs);
        error.setVertex1(manager.getVertex(rs.getString("vertex1")));
        error.setVertex2(manager.getVertex(rs.getString("vertex2")));
    }

    private void initialize(RingSelfIntersection error, ResultSet rs) throws SQLException {
        initialize((AbstractRingError) error, rs);
        error.setVertex1(manager.getVertex(rs.getString("vertex1")));
        error.setVertex2(manager.getVertex(rs.getString("vertex2")));
        error.setType(RingSelfIntersectionType.fromValue(rs.getString("type")));

        Edge edge1 = createEdge(rs.getString("edge1_from"), rs.getString("edge1_to"));
        if (edge1 != null) {
            error.setEdge1(edge1);
        }

        Edge edge2 = createEdge(rs.getString("edge2_from"), rs.getString("edge2_to"));
        if (edge2 != null) {
            error.setEdge2(edge2);
        }
    }

    private void initialize(AbstractPolygonError error, ResultSet rs) throws SQLException {
        String polygonId = helper.replaceObjectId(rs.getString("polygonid"));
        error.setPolygonId(polygonId);
    }

    private void initialize(PolygonNonPlanarNormalsDeviation error, ResultSet rs) throws SQLException {
        initialize((AbstractPolygonError) error, rs);
        double deviation = rs.getDouble("deviation");
        if (!rs.wasNull()) {
            Angle angle = new Angle(deviation);
            angle.setUom(rs.getString("deviation_uom"));
            error.setDeviation(angle);
        }
    }

    private void initialize(PolygonOrientationRingsSame error, ResultSet rs) throws SQLException {
        initialize((AbstractPolygonError) error, rs);
        error.setLinearRingId(helper.replaceObjectId(rs.getString("p_linearringid")));
    }

    private void initialize(PolygonInnerRingsNested error, ResultSet rs) throws SQLException {
        initialize((AbstractPolygonError) error, rs);
        error.setLinearRingId1(helper.replaceObjectId(rs.getString("linearringid1")));
        error.setLinearRingId2(helper.replaceObjectId(rs.getString("linearringid2")));
    }

    private void initialize(PolygonNonPlanarDistancePlane error, ResultSet rs) throws SQLException {
        initialize((AbstractPolygonError) error, rs);
        double distance = rs.getDouble("distance");
        if (!rs.wasNull()) {
            Length length = new Length(distance);
            length.setUom(rs.getString("distance_uom"));
            error.setDistance(length);
        }

        error.setVertex(manager.getVertex(rs.getString("p_vertex")));
    }

    private void initialize(PolygonIntersectingRings error, ResultSet rs) throws SQLException {
        initialize((AbstractPolygonError) error, rs);
        error.setLinearRingId1(helper.replaceObjectId(rs.getString("linearringid1")));
        error.setLinearRingId2(helper.replaceObjectId(rs.getString("linearringid2")));
    }

    private void initialize(PolygonHoleOutside error, ResultSet rs) throws SQLException {
        initialize((AbstractPolygonError) error, rs);
        error.setLinearRingId(helper.replaceObjectId(rs.getString("p_linearringid")));
    }

    private void initialize(AbstractSolidError error, ResultSet rs) throws SQLException {
        String geometryId = helper.replaceObjectId(rs.getString("geometryid"));
        error.setGeometryId(geometryId);
    }

    private void initialize(SolidNonManifoldVertex error, ResultSet rs) throws SQLException {
        initialize((AbstractSolidError) error, rs);
        error.setVertex(manager.getVertex("s_vertex"));
    }

    private void initialize(SolidSelfIntersection error, ResultSet rs) throws SQLException {
        initialize((AbstractSolidError) error, rs);
        error.setPolygonId1(helper.replaceObjectId(rs.getString("polygonid1")));
        error.setPolygonId2(helper.replaceObjectId(rs.getString("polygonid2")));
    }

    private void initialize(SemanticAttributeWrongValue error, ResultSet rs) throws SQLException {
        error.setAttributeName(rs.getString("attributename"));
        error.setChildId(helper.replaceObjectId(rs.getString("childid")));
        error.setGeneric(rs.getBoolean("generic"));
    }

    private void initialize(SemanticAttributeMissing error, ResultSet rs) throws SQLException {
        error.setAttributeName(rs.getString("attributename"));
        error.setChildId(helper.replaceObjectId(rs.getString("childid")));
        error.setGeneric(rs.getBoolean("generic"));
    }

    private Edge createEdge(String fromValue, String toValue) {
        if (fromValue != null && toValue != null) {
            DirectPosition from = manager.getVertex(fromValue);
            DirectPosition to = manager.getVertex(toValue);
            if (from != null && to != null) {
                return new Edge(from, to);
            }
        }

        return null;
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
