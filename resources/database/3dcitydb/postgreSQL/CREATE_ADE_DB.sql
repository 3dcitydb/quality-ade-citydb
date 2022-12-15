-- This document was automatically created by the ADE-Manager tool of 3DCityDB (https://www.3dcitydb.org) on 2022-11-22 08:55:25 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- *********************************** Create tables ************************************** 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- -------------------------------------------------------------------- 
-- qual_checking 
-- -------------------------------------------------------------------- 
CREATE TABLE qual_checking
(
    id BIGINT NOT NULL,
    featuretype VARCHAR(1000),
    filter_checking_id BIGINT,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- qual_cityobject 
-- -------------------------------------------------------------------- 
CREATE TABLE qual_cityobject
(
    id BIGINT NOT NULL,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- qual_edge 
-- -------------------------------------------------------------------- 
CREATE TABLE qual_edge
(
    id BIGINT NOT NULL,
    edgelist_edge_id BIGINT,
    from_ TEXT,
    to_ TEXT,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- qual_edgelist 
-- -------------------------------------------------------------------- 
CREATE TABLE qual_edgelist
(
    id BIGINT NOT NULL,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- qual_error 
-- -------------------------------------------------------------------- 
CREATE TABLE qual_error
(
    id BIGINT NOT NULL,
    objectclass_id INTEGER,
    validationresult_error_id BIGINT,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- qual_error_1 
-- -------------------------------------------------------------------- 
CREATE TABLE qual_error_1
(
    id BIGINT NOT NULL,
    name VARCHAR(1000),
    occurrences INTEGER,
    statistics_error_id BIGINT,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- qual_featurestatistics 
-- -------------------------------------------------------------------- 
CREATE TABLE qual_featurestatistics
(
    id BIGINT NOT NULL,
    numchecked INTEGER,
    numerrors INTEGER,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- qual_filter 
-- -------------------------------------------------------------------- 
CREATE TABLE qual_filter
(
    id BIGINT NOT NULL,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- qual_geometryerror 
-- -------------------------------------------------------------------- 
CREATE TABLE qual_geometryerror
(
    id BIGINT NOT NULL,
    objectclass_id INTEGER,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- qual_globalparameters 
-- -------------------------------------------------------------------- 
CREATE TABLE qual_globalparameters
(
    id BIGINT NOT NULL,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- qual_parameter 
-- -------------------------------------------------------------------- 
CREATE TABLE qual_parameter
(
    id BIGINT NOT NULL,
    globalparameter_parameter_id BIGINT,
    name VARCHAR(1000),
    requirement_parameter_id BIGINT,
    uom VARCHAR(1000),
    value VARCHAR(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- qual_polygonerror 
-- -------------------------------------------------------------------- 
CREATE TABLE qual_polygonerror
(
    id BIGINT NOT NULL,
    deviation NUMERIC,
    deviation_uom VARCHAR(1000),
    distance NUMERIC,
    distance_uom VARCHAR(1000),
    linearringid VARCHAR(1000),
    linearringid1 VARCHAR(1000),
    linearringid2 VARCHAR(1000),
    objectclass_id INTEGER,
    polygonid VARCHAR(1000),
    vertex TEXT,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- qual_polygonidlist 
-- -------------------------------------------------------------------- 
CREATE TABLE qual_polygonidlist
(
    id BIGINT NOT NULL,
    polygonid VARCHAR(1000),
    soliderror_component_id BIGINT,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- qual_requirement 
-- -------------------------------------------------------------------- 
CREATE TABLE qual_requirement
(
    id BIGINT NOT NULL,
    enabled NUMERIC,
    requirementtype VARCHAR(1000),
    validationpla_requirement_id BIGINT,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- qual_ringerror 
-- -------------------------------------------------------------------- 
CREATE TABLE qual_ringerror
(
    id BIGINT NOT NULL,
    edge1_id BIGINT,
    edge2_id BIGINT,
    linearringid VARCHAR(1000),
    objectclass_id INTEGER,
    type VARCHAR(1000),
    vertex1 TEXT,
    vertex2 TEXT,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- qual_semanticerror 
-- -------------------------------------------------------------------- 
CREATE TABLE qual_semanticerror
(
    id BIGINT NOT NULL,
    attributename VARCHAR(1000),
    childid VARCHAR(1000),
    generic NUMERIC,
    objectclass_id INTEGER,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- qual_soliderror 
-- -------------------------------------------------------------------- 
CREATE TABLE qual_soliderror
(
    id BIGINT NOT NULL,
    edges_id BIGINT,
    geometryid VARCHAR(1000),
    objectclass_id INTEGER,
    polygonid1 VARCHAR(1000),
    polygonid2 VARCHAR(1000),
    vertex TEXT,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- qual_statistics 
-- -------------------------------------------------------------------- 
CREATE TABLE qual_statistics
(
    id BIGINT NOT NULL,
    numerrorbridgeobjects_id BIGINT,
    numerrorbuildings_id BIGINT,
    numerrorlandobjects_id BIGINT,
    numerrortransportation_id BIGINT,
    numerrorvegetation_id BIGINT,
    numerrorwaterobjects_id BIGINT,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- qual_validation 
-- -------------------------------------------------------------------- 
CREATE TABLE qual_validation
(
    id BIGINT NOT NULL,
    statistics_id BIGINT,
    validationdate TIMESTAMP WITH TIME ZONE,
    validationplan_id BIGINT,
    validationsoftware VARCHAR(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- qual_validationplan 
-- -------------------------------------------------------------------- 
CREATE TABLE qual_validationplan
(
    id BIGINT NOT NULL,
    filter_id BIGINT,
    globalparameters_id BIGINT,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- qual_validationresult 
-- -------------------------------------------------------------------- 
CREATE TABLE qual_validationresult
(
    id BIGINT NOT NULL,
    cityobject_validationresu_id BIGINT,
    resulttype VARCHAR(1000),
    validationplanid_id BIGINT,
    PRIMARY KEY (id)
);

-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- *********************************** Create foreign keys ******************************** 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- -------------------------------------------------------------------- 
-- qual_checking 
-- -------------------------------------------------------------------- 
ALTER TABLE qual_checking ADD CONSTRAINT qual_checki_filte_check_fk FOREIGN KEY (filter_checking_id)
REFERENCES qual_filter (id);

-- -------------------------------------------------------------------- 
-- qual_cityobject 
-- -------------------------------------------------------------------- 
ALTER TABLE qual_cityobject ADD CONSTRAINT qual_cityobject_fk FOREIGN KEY (id)
REFERENCES cityobject (id);

-- -------------------------------------------------------------------- 
-- qual_edge 
-- -------------------------------------------------------------------- 
ALTER TABLE qual_edge ADD CONSTRAINT qual_edge_edgelist_edge_fk FOREIGN KEY (edgelist_edge_id)
REFERENCES qual_edgelist (id);

-- -------------------------------------------------------------------- 
-- qual_error 
-- -------------------------------------------------------------------- 
ALTER TABLE qual_error ADD CONSTRAINT qual_error_objectclass_fk FOREIGN KEY (objectclass_id)
REFERENCES objectclass (id);

ALTER TABLE qual_error ADD CONSTRAINT qual_error_valida_error_fk FOREIGN KEY (validationresult_error_id)
REFERENCES qual_validationresult (id);

-- -------------------------------------------------------------------- 
-- qual_error_1 
-- -------------------------------------------------------------------- 
ALTER TABLE qual_error_1 ADD CONSTRAINT qual_error_statis_error_fk FOREIGN KEY (statistics_error_id)
REFERENCES qual_statistics (id);

-- -------------------------------------------------------------------- 
-- qual_geometryerror 
-- -------------------------------------------------------------------- 
ALTER TABLE qual_geometryerror ADD CONSTRAINT qual_geometryerror_fk FOREIGN KEY (id)
REFERENCES qual_error (id);

ALTER TABLE qual_geometryerror ADD CONSTRAINT qual_geometrye_objectcl_fk FOREIGN KEY (objectclass_id)
REFERENCES objectclass (id);

-- -------------------------------------------------------------------- 
-- qual_parameter 
-- -------------------------------------------------------------------- 
ALTER TABLE qual_parameter ADD CONSTRAINT qual_parame_globa_param_fk FOREIGN KEY (globalparameter_parameter_id)
REFERENCES qual_globalparameters (id);

ALTER TABLE qual_parameter ADD CONSTRAINT qual_parame_requi_param_fk FOREIGN KEY (requirement_parameter_id)
REFERENCES qual_requirement (id);

-- -------------------------------------------------------------------- 
-- qual_polygonerror 
-- -------------------------------------------------------------------- 
ALTER TABLE qual_polygonerror ADD CONSTRAINT qual_polygoner_objectcl_fk FOREIGN KEY (objectclass_id)
REFERENCES objectclass (id);

ALTER TABLE qual_polygonerror ADD CONSTRAINT qual_polygonerror_fk FOREIGN KEY (id)
REFERENCES qual_geometryerror (id);

-- -------------------------------------------------------------------- 
-- qual_polygonidlist 
-- -------------------------------------------------------------------- 
ALTER TABLE qual_polygonidlist ADD CONSTRAINT qual_polygo_solid_compo_fk FOREIGN KEY (soliderror_component_id)
REFERENCES qual_soliderror (id);

-- -------------------------------------------------------------------- 
-- qual_requirement 
-- -------------------------------------------------------------------- 
ALTER TABLE qual_requirement ADD CONSTRAINT qual_requir_valid_requi_fk FOREIGN KEY (validationpla_requirement_id)
REFERENCES qual_validationplan (id);

-- -------------------------------------------------------------------- 
-- qual_ringerror 
-- -------------------------------------------------------------------- 
ALTER TABLE qual_ringerror ADD CONSTRAINT qual_ringerror_objectcl_fk FOREIGN KEY (objectclass_id)
REFERENCES objectclass (id);

ALTER TABLE qual_ringerror ADD CONSTRAINT qual_ringerror_fk FOREIGN KEY (id)
REFERENCES qual_geometryerror (id);

ALTER TABLE qual_ringerror ADD CONSTRAINT qual_ringerror_edge1_fk FOREIGN KEY (edge1_id)
REFERENCES qual_edge (id)
ON DELETE SET NULL;

ALTER TABLE qual_ringerror ADD CONSTRAINT qual_ringerror_edge2_fk FOREIGN KEY (edge2_id)
REFERENCES qual_edge (id)
ON DELETE SET NULL;

-- -------------------------------------------------------------------- 
-- qual_semanticerror 
-- -------------------------------------------------------------------- 
ALTER TABLE qual_semanticerror ADD CONSTRAINT qual_semanticerror_fk FOREIGN KEY (id)
REFERENCES qual_error (id);

ALTER TABLE qual_semanticerror ADD CONSTRAINT qual_semantice_objectcl_fk FOREIGN KEY (objectclass_id)
REFERENCES objectclass (id);

-- -------------------------------------------------------------------- 
-- qual_soliderror 
-- -------------------------------------------------------------------- 
ALTER TABLE qual_soliderror ADD CONSTRAINT qual_soliderro_objectcl_fk FOREIGN KEY (objectclass_id)
REFERENCES objectclass (id);

ALTER TABLE qual_soliderror ADD CONSTRAINT qual_soliderror_fk FOREIGN KEY (id)
REFERENCES qual_geometryerror (id);

ALTER TABLE qual_soliderror ADD CONSTRAINT qual_soliderror_edges_fk FOREIGN KEY (edges_id)
REFERENCES qual_edgelist (id)
ON DELETE SET NULL;

ALTER TABLE qual_soliderror ADD CONSTRAINT qual_soliderror_edges_fk_1 FOREIGN KEY (edges_id)
REFERENCES qual_edgelist (id)
ON DELETE SET NULL;

ALTER TABLE qual_soliderror ADD CONSTRAINT qual_soliderror_edges_fk_2 FOREIGN KEY (edges_id)
REFERENCES qual_edgelist (id)
ON DELETE SET NULL;

-- -------------------------------------------------------------------- 
-- qual_statistics 
-- -------------------------------------------------------------------- 
ALTER TABLE qual_statistics ADD CONSTRAINT qual_statistic_numerror_fk FOREIGN KEY (numerrorbuildings_id)
REFERENCES qual_featurestatistics (id)
ON DELETE SET NULL;

ALTER TABLE qual_statistics ADD CONSTRAINT qual_statisti_numerro_fk_1 FOREIGN KEY (numerrorvegetation_id)
REFERENCES qual_featurestatistics (id)
ON DELETE SET NULL;

ALTER TABLE qual_statistics ADD CONSTRAINT qual_statisti_numerro_fk_2 FOREIGN KEY (numerrorlandobjects_id)
REFERENCES qual_featurestatistics (id)
ON DELETE SET NULL;

ALTER TABLE qual_statistics ADD CONSTRAINT qual_statisti_numerro_fk_3 FOREIGN KEY (numerrorbridgeobjects_id)
REFERENCES qual_featurestatistics (id)
ON DELETE SET NULL;

ALTER TABLE qual_statistics ADD CONSTRAINT qual_statisti_numerro_fk_4 FOREIGN KEY (numerrorwaterobjects_id)
REFERENCES qual_featurestatistics (id)
ON DELETE SET NULL;

ALTER TABLE qual_statistics ADD CONSTRAINT qual_statisti_numerro_fk_5 FOREIGN KEY (numerrortransportation_id)
REFERENCES qual_featurestatistics (id)
ON DELETE SET NULL;

-- -------------------------------------------------------------------- 
-- qual_validation 
-- -------------------------------------------------------------------- 
ALTER TABLE qual_validation ADD CONSTRAINT qual_validation_fk FOREIGN KEY (id)
REFERENCES cityobject (id);

ALTER TABLE qual_validation ADD CONSTRAINT qual_validatio_statisti_fk FOREIGN KEY (statistics_id)
REFERENCES qual_statistics (id)
ON DELETE SET NULL;

ALTER TABLE qual_validation ADD CONSTRAINT qual_validati_validat_fk_1 FOREIGN KEY (validationplan_id)
REFERENCES qual_validationplan (id)
ON DELETE SET NULL;

-- -------------------------------------------------------------------- 
-- qual_validationplan 
-- -------------------------------------------------------------------- 
ALTER TABLE qual_validationplan ADD CONSTRAINT qual_validationp_filter_fk FOREIGN KEY (filter_id)
REFERENCES qual_filter (id)
ON DELETE SET NULL;

ALTER TABLE qual_validationplan ADD CONSTRAINT qual_validatio_globalpa_fk FOREIGN KEY (globalparameters_id)
REFERENCES qual_globalparameters (id)
ON DELETE SET NULL;

-- -------------------------------------------------------------------- 
-- qual_validationresult 
-- -------------------------------------------------------------------- 
ALTER TABLE qual_validationresult ADD CONSTRAINT qual_valida_cityo_valid_fk FOREIGN KEY (cityobject_validationresu_id)
REFERENCES qual_cityobject (id);

ALTER TABLE qual_validationresult ADD CONSTRAINT qual_validatio_validati_fk FOREIGN KEY (validationplanid_id)
REFERENCES qual_validation (id)
ON DELETE SET NULL;

-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- *********************************** Create Indexes ************************************* 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- -------------------------------------------------------------------- 
-- qual_checking 
-- -------------------------------------------------------------------- 
CREATE INDEX qual_check_filte_check_fkx ON qual_checking
    USING btree
    (
      filter_checking_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- qual_edge 
-- -------------------------------------------------------------------- 
CREATE INDEX qual_edge_edgelis_edge_fkx ON qual_edge
    USING btree
    (
      edgelist_edge_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- qual_error 
-- -------------------------------------------------------------------- 
CREATE INDEX qual_error_objectclass_fkx ON qual_error
    USING btree
    (
      objectclass_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX qual_error_valid_error_fkx ON qual_error
    USING btree
    (
      validationresult_error_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- qual_error_1 
-- -------------------------------------------------------------------- 
CREATE INDEX qual_error_stati_error_fkx ON qual_error_1
    USING btree
    (
      statistics_error_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- qual_geometryerror 
-- -------------------------------------------------------------------- 
CREATE INDEX qual_geometry_objectcl_fkx ON qual_geometryerror
    USING btree
    (
      objectclass_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- qual_parameter 
-- -------------------------------------------------------------------- 
CREATE INDEX qual_param_globa_param_fkx ON qual_parameter
    USING btree
    (
      globalparameter_parameter_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX qual_param_requi_param_fkx ON qual_parameter
    USING btree
    (
      requirement_parameter_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- qual_polygonerror 
-- -------------------------------------------------------------------- 
CREATE INDEX qual_polygone_objectcl_fkx ON qual_polygonerror
    USING btree
    (
      objectclass_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- qual_polygonidlist 
-- -------------------------------------------------------------------- 
CREATE INDEX qual_polyg_solid_compo_fkx ON qual_polygonidlist
    USING btree
    (
      soliderror_component_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- qual_requirement 
-- -------------------------------------------------------------------- 
CREATE INDEX qual_requi_valid_requi_fkx ON qual_requirement
    USING btree
    (
      validationpla_requirement_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- qual_ringerror 
-- -------------------------------------------------------------------- 
CREATE INDEX qual_ringerror_edge1_fkx ON qual_ringerror
    USING btree
    (
      edge1_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX qual_ringerror_edge2_fkx ON qual_ringerror
    USING btree
    (
      edge2_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX qual_ringerro_objectcl_fkx ON qual_ringerror
    USING btree
    (
      objectclass_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- qual_semanticerror 
-- -------------------------------------------------------------------- 
CREATE INDEX qual_semantic_objectcl_fkx ON qual_semanticerror
    USING btree
    (
      objectclass_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- qual_soliderror 
-- -------------------------------------------------------------------- 
CREATE INDEX qual_soliderror_edges_fkx ON qual_soliderror
    USING btree
    (
      edges_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX qual_soliderr_objectcl_fkx ON qual_soliderror
    USING btree
    (
      objectclass_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- qual_statistics 
-- -------------------------------------------------------------------- 
CREATE INDEX qual_statist_numerro_fkx_3 ON qual_statistics
    USING btree
    (
      numerrorbridgeobjects_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX qual_statisti_numerror_fkx ON qual_statistics
    USING btree
    (
      numerrorbuildings_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX qual_statist_numerro_fkx_2 ON qual_statistics
    USING btree
    (
      numerrorlandobjects_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX qual_statist_numerro_fkx_5 ON qual_statistics
    USING btree
    (
      numerrortransportation_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX qual_statist_numerro_fkx_1 ON qual_statistics
    USING btree
    (
      numerrorvegetation_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX qual_statist_numerro_fkx_4 ON qual_statistics
    USING btree
    (
      numerrorwaterobjects_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- qual_validation 
-- -------------------------------------------------------------------- 
CREATE INDEX qual_validati_statisti_fkx ON qual_validation
    USING btree
    (
      statistics_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX qual_validat_validat_fkx_1 ON qual_validation
    USING btree
    (
      validationplan_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- qual_validationplan 
-- -------------------------------------------------------------------- 
CREATE INDEX qual_validation_filter_fkx ON qual_validationplan
    USING btree
    (
      filter_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX qual_validati_globalpa_fkx ON qual_validationplan
    USING btree
    (
      globalparameters_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- qual_validationresult 
-- -------------------------------------------------------------------- 
CREATE INDEX qual_valid_cityo_valid_fkx ON qual_validationresult
    USING btree
    (
      cityobject_validationresu_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX qual_validati_validati_fkx ON qual_validationresult
    USING btree
    (
      validationplanid_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- *********************************** Create Sequences *********************************** 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 

CREATE SEQUENCE qual_error_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


CREATE SEQUENCE qual_checking_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


CREATE SEQUENCE qual_edge_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


CREATE SEQUENCE qual_edgelist_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


CREATE SEQUENCE qual_error_seq_1
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


CREATE SEQUENCE qual_featurestatistic_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


CREATE SEQUENCE qual_filter_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


CREATE SEQUENCE qual_polygonidlist_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


CREATE SEQUENCE qual_globalparameters_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


CREATE SEQUENCE qual_parameter_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


CREATE SEQUENCE qual_requirement_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


CREATE SEQUENCE qual_statistics_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


CREATE SEQUENCE qual_validationplan_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


CREATE SEQUENCE qual_validationresult_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


