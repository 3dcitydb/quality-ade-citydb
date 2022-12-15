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

import org.citydb.core.util.CoreConstants;
import org.citygml4j.model.gml.base.AbstractGML;
import org.citygml4j.model.gml.feature.AbstractFeature;
import org.citygml4j.model.gml.geometry.primitives.*;
import org.citygml4j.util.walker.GMLWalker;

import java.util.HashMap;
import java.util.Map;

public class IdResolver {
    private final Map<String, String> ids = new HashMap<>();

    private IdResolver() {
    }

    public static IdResolver of(AbstractFeature feature) {
        return new IdResolver().process(feature);
    }

    public String resolveId(String identifier) {
        if (identifier != null) {
            String id = getIdFromReference(identifier);
            return ids.getOrDefault(id, id);
        } else {
            return null;
        }
    }

    private IdResolver process(AbstractFeature feature) {
        feature.accept(new GMLWalker() {
            @Override
            public void visit(AbstractGML object) {
                if (object.isSetId()) {
                    String originalId = (String) object.getLocalProperty(CoreConstants.OBJECT_ORIGINAL_GMLID);
                    if (originalId != null) {
                        ids.put(originalId, object.getId());
                    }
                }
            }

            @Override
            public void visit(Polygon polygon) {
                if (polygon.isSetId()) {
                    if (polygon.isSetExterior()
                            && polygon.getExterior().isSetRing()
                            && isValid(polygon.getExterior().getRing())) {
                        process(polygon.getExterior().getRing(), polygon.getId(), 0);

                        int index = 1;
                        if (polygon.isSetInterior()) {
                            for (AbstractRingProperty property : polygon.getInterior()) {
                                if (property.isSetRing() && isValid(property.getRing())) {
                                    process(property.getRing(), polygon.getId(), index++);
                                }
                            }
                        }
                    }
                }

                super.visit(polygon);
            }

            @Override
            public void visit(Surface surface) {
                if (surface.isSetId()) {
                    SurfacePatchArrayProperty property = surface.getPatches();
                    if (property != null && property.isSetSurfacePatch()) {
                        for (AbstractSurfacePatch patch : property.getSurfacePatch()) {
                            Polygon polygon = new Polygon();
                            polygon.setId(surface.getId());

                            if (patch instanceof Rectangle) {
                                Rectangle rectangle = (Rectangle) patch;
                                polygon = new Polygon();
                                polygon.setExterior(rectangle.getExterior());
                            } else if (patch instanceof Triangle) {
                                Triangle triangle = (Triangle) patch;
                                polygon = new Polygon();
                                polygon.setExterior(triangle.getExterior());
                            } else if (patch instanceof PolygonPatch) {
                                PolygonPatch polygonPatch = (PolygonPatch) patch;
                                polygon.setExterior(polygonPatch.getExterior());
                                polygon.setInterior(polygonPatch.getInterior());
                            }

                            visit(polygon);
                        }
                    }
                }

                super.visit(surface);
            }

            private boolean isValid(AbstractRing ring) {
                return ring.toList3d().size() / 3 > 3;
            }

            private void process(AbstractRing ring, String polygonId, int index) {
                if (ring.isSetId()) {
                    ids.put(ring.getId(), polygonId + "_" + index + "_");
                }
            }
        });

        return this;
    }

    private String getIdFromReference(String reference) {
        int index = reference.lastIndexOf("#");
        return index != -1 ? reference.substring(index + 1) : reference;
    }
}
