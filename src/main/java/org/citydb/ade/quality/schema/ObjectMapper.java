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

import org.citydb.core.ade.ADEObjectMapper;
import org.citydb.core.database.schema.mapping.AbstractObjectType;
import org.citydb.core.database.schema.mapping.ComplexType;
import org.citydb.core.database.schema.mapping.SchemaMapping;
import org.citygml4j.ade.quality.model.*;
import org.citygml4j.model.citygml.ade.binding.ADEModelObject;
import org.citygml4j.model.gml.base.AbstractGML;
import org.citygml4j.model.module.citygml.CityGMLVersion;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ObjectMapper implements ADEObjectMapper {
    private final Map<Class<? extends ADEModelObject>, Integer> objectClassIds = new HashMap<>();

    public void populateObjectClassIds(SchemaMapping schemaMapping) {
        for (AbstractObjectType<?> type : schemaMapping.getAbstractObjectTypes()) {
            if ("Validation".equals(type.getPath())) {
                objectClassIds.put(Validation.class, type.getObjectClassId());
            }
        }

        for (ComplexType type : schemaMapping.getComplexTypes()) {
            int objectClassId = type.getObjectClassId();
            switch (type.getPath()) {
                case "GE_R_CONSECUTIVE_POINTS_SAME":
                    objectClassIds.put(RingConsecutivePointsSame.class, objectClassId);
                    break;
                case "GE_R_NOT_CLOSED":
                    objectClassIds.put(RingNotClosed.class, objectClassId);
                    break;
                case "GE_R_SELF_INTERSECTION":
                    objectClassIds.put(RingSelfIntersection.class, objectClassId);
                    break;
                case "GE_R_TOO_FEW_POINTS":
                    objectClassIds.put(RingTooFewPoints.class, objectClassId);
                    break;
                case "GE_P_HOLE_OUTSIDE":
                    objectClassIds.put(PolygonHoleOutside.class, objectClassId);
                    break;
                case "GE_P_INNER_RINGS_NESTED":
                    objectClassIds.put(PolygonInnerRingsNested.class, objectClassId);
                    break;
                case "GE_P_INTERIOR_DISCONNECTED":
                    objectClassIds.put(PolygonInteriorDisconnected.class, objectClassId);
                    break;
                case "GE_P_INTERSECTING_RINGS":
                    objectClassIds.put(PolygonIntersectingRings.class, objectClassId);
                    break;
                case "GE_P_NON_PLANAR_POLYGON_DISTANCE_PLANE":
                    objectClassIds.put(PolygonNonPlanarDistancePlane.class, objectClassId);
                    break;
                case "GE_P_NON_PLANAR_POLYGON_NORMALS_DEVIATION":
                    objectClassIds.put(PolygonNonPlanarNormalsDeviation.class, objectClassId);
                    break;
                case "GE_P_ORIENTATION_RINGS_SAME":
                    objectClassIds.put(PolygonOrientationRingsSame.class, objectClassId);
                    break;
                case "GE_S_ALL_POLYGONS_WRONG_ORIENTATION":
                    objectClassIds.put(SolidAllPolygonsWrongOrientation.class, objectClassId);
                    break;
                case "GE_S_MULTIPLE_CONNECTED_COMPONENTS":
                    objectClassIds.put(SolidMultipleConnectedComponents.class, objectClassId);
                    break;
                case "GE_S_NON_MANIFOLD_EDGE":
                    objectClassIds.put(SolidNonManifoldEdge.class, objectClassId);
                    break;
                case "GE_S_NON_MANIFOLD_VERTEX":
                    objectClassIds.put(SolidNonManifoldVertex.class, objectClassId);
                    break;
                case "GE_S_NOT_CLOSED":
                    objectClassIds.put(SolidNotClosed.class, objectClassId);
                    break;
                case "GE_S_POLYGON_WRONG_ORIENTATION":
                    objectClassIds.put(SolidPolygonWrongOrientation.class, objectClassId);
                    break;
                case "GE_S_SELF_INTERSECTION":
                    objectClassIds.put(SolidSelfIntersection.class, objectClassId);
                    break;
                case "GE_S_TOO_FEW_POLYGONS":
                    objectClassIds.put(SolidTooFewPolygons.class, objectClassId);
                    break;
                case "SE_ATTRIBUTE_MISSING":
                    objectClassIds.put(SemanticAttributeMissing.class, objectClassId);
                    break;
                case "SE_ATTRIBUTE_WRONG_VALUE":
                    objectClassIds.put(SemanticAttributeWrongValue.class, objectClassId);
                    break;
            }
        }
    }

    @Override
    public AbstractGML createObject(int objectClassId, CityGMLVersion version) {
        if (version == CityGMLVersion.v2_0_0) {
            for (Map.Entry<Class<? extends ADEModelObject>, Integer> entry : objectClassIds.entrySet()) {
                if (entry.getValue() == objectClassId && AbstractGML.class.isAssignableFrom(entry.getKey())) {
                    try {
                        return (AbstractGML) entry.getKey().getDeclaredConstructor().newInstance();
                    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                             InvocationTargetException e) {
                        //
                    }
                }
            }
        }

        return null;
    }

    public <T extends ADEModelObject> T createObject(int objectClassId, Class<T> type) {
        for (Map.Entry<Class<? extends ADEModelObject>, Integer> entry : objectClassIds.entrySet()) {
            if (entry.getValue() == objectClassId && type.isAssignableFrom(entry.getKey())) {
                try {
                    return type.cast(entry.getKey().getDeclaredConstructor().newInstance());
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                         InvocationTargetException e) {
                    //
                }
            }
        }

        return null;
    }

    @Override
    public int getObjectClassId(Class<? extends AbstractGML> adeObjectClass) {
        return objectClassIds.getOrDefault(adeObjectClass, 0);
    }

    public int getObjectClassId(ADEModelObject modelObject) {
        return objectClassIds.getOrDefault(modelObject.getClass(), 0);
    }
}
