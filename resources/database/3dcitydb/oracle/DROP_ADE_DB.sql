-- This document was automatically created by the ADE-Manager tool of 3DCityDB (https://www.3dcitydb.org) on 2022-11-22 08:55:25 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- *********************************** Drop foreign keys ********************************** 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- -------------------------------------------------------------------- 
-- qual_checking 
-- -------------------------------------------------------------------- 
ALTER TABLE qual_checking
    DROP CONSTRAINT qual_checki_filte_check_fk;

-- -------------------------------------------------------------------- 
-- qual_cityobject 
-- -------------------------------------------------------------------- 
ALTER TABLE qual_cityobject
    DROP CONSTRAINT qual_cityobject_fk;

-- -------------------------------------------------------------------- 
-- qual_edge 
-- -------------------------------------------------------------------- 
ALTER TABLE qual_edge
    DROP CONSTRAINT qual_edge_edgelist_edge_fk;

-- -------------------------------------------------------------------- 
-- qual_error 
-- -------------------------------------------------------------------- 
ALTER TABLE qual_error
    DROP CONSTRAINT qual_error_objectclass_fk;

ALTER TABLE qual_error
    DROP CONSTRAINT qual_error_valida_error_fk;

-- -------------------------------------------------------------------- 
-- qual_error_1 
-- -------------------------------------------------------------------- 
ALTER TABLE qual_error_1
    DROP CONSTRAINT qual_error_statis_error_fk;

-- -------------------------------------------------------------------- 
-- qual_geometryerror 
-- -------------------------------------------------------------------- 
ALTER TABLE qual_geometryerror
    DROP CONSTRAINT qual_geometryerror_fk;

ALTER TABLE qual_geometryerror
    DROP CONSTRAINT qual_geometrye_objectcl_fk;

-- -------------------------------------------------------------------- 
-- qual_parameter 
-- -------------------------------------------------------------------- 
ALTER TABLE qual_parameter
    DROP CONSTRAINT qual_parame_globa_param_fk;

ALTER TABLE qual_parameter
    DROP CONSTRAINT qual_parame_requi_param_fk;

-- -------------------------------------------------------------------- 
-- qual_polygonerror 
-- -------------------------------------------------------------------- 
ALTER TABLE qual_polygonerror
    DROP CONSTRAINT qual_polygoner_objectcl_fk;

ALTER TABLE qual_polygonerror
    DROP CONSTRAINT qual_polygonerror_fk;

-- -------------------------------------------------------------------- 
-- qual_polygonidlist 
-- -------------------------------------------------------------------- 
ALTER TABLE qual_polygonidlist
    DROP CONSTRAINT qual_polygo_solid_compo_fk;

-- -------------------------------------------------------------------- 
-- qual_requirement 
-- -------------------------------------------------------------------- 
ALTER TABLE qual_requirement
    DROP CONSTRAINT qual_requir_valid_requi_fk;

-- -------------------------------------------------------------------- 
-- qual_ringerror 
-- -------------------------------------------------------------------- 
ALTER TABLE qual_ringerror
    DROP CONSTRAINT qual_ringerror_objectcl_fk;

ALTER TABLE qual_ringerror
    DROP CONSTRAINT qual_ringerror_fk;

ALTER TABLE qual_ringerror
    DROP CONSTRAINT qual_ringerror_edge1_fk;

ALTER TABLE qual_ringerror
    DROP CONSTRAINT qual_ringerror_edge2_fk;

-- -------------------------------------------------------------------- 
-- qual_semanticerror 
-- -------------------------------------------------------------------- 
ALTER TABLE qual_semanticerror
    DROP CONSTRAINT qual_semanticerror_fk;

ALTER TABLE qual_semanticerror
    DROP CONSTRAINT qual_semantice_objectcl_fk;

-- -------------------------------------------------------------------- 
-- qual_soliderror 
-- -------------------------------------------------------------------- 
ALTER TABLE qual_soliderror
    DROP CONSTRAINT qual_soliderro_objectcl_fk;

ALTER TABLE qual_soliderror
    DROP CONSTRAINT qual_soliderror_fk;

ALTER TABLE qual_soliderror
    DROP CONSTRAINT qual_soliderror_edges_fk;

ALTER TABLE qual_soliderror
    DROP CONSTRAINT qual_soliderror_edges_fk_1;

ALTER TABLE qual_soliderror
    DROP CONSTRAINT qual_soliderror_edges_fk_2;

-- -------------------------------------------------------------------- 
-- qual_statistics 
-- -------------------------------------------------------------------- 
ALTER TABLE qual_statistics
    DROP CONSTRAINT qual_statistic_numerror_fk;

ALTER TABLE qual_statistics
    DROP CONSTRAINT qual_statisti_numerro_fk_1;

ALTER TABLE qual_statistics
    DROP CONSTRAINT qual_statisti_numerro_fk_2;

ALTER TABLE qual_statistics
    DROP CONSTRAINT qual_statisti_numerro_fk_3;

ALTER TABLE qual_statistics
    DROP CONSTRAINT qual_statisti_numerro_fk_4;

ALTER TABLE qual_statistics
    DROP CONSTRAINT qual_statisti_numerro_fk_5;

-- -------------------------------------------------------------------- 
-- qual_validation 
-- -------------------------------------------------------------------- 
ALTER TABLE qual_validation
    DROP CONSTRAINT qual_validation_fk;

ALTER TABLE qual_validation
    DROP CONSTRAINT qual_validatio_statisti_fk;

ALTER TABLE qual_validation
    DROP CONSTRAINT qual_validati_validat_fk_1;

-- -------------------------------------------------------------------- 
-- qual_validationplan 
-- -------------------------------------------------------------------- 
ALTER TABLE qual_validationplan
    DROP CONSTRAINT qual_validationp_filter_fk;

ALTER TABLE qual_validationplan
    DROP CONSTRAINT qual_validatio_globalpa_fk;

-- -------------------------------------------------------------------- 
-- qual_validationresult 
-- -------------------------------------------------------------------- 
ALTER TABLE qual_validationresult
    DROP CONSTRAINT qual_valida_cityo_valid_fk;

ALTER TABLE qual_validationresult
    DROP CONSTRAINT qual_validatio_validati_fk;

-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- *********************************** Drop tables *************************************** 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- -------------------------------------------------------------------- 
-- qual_checking 
-- -------------------------------------------------------------------- 
DROP TABLE qual_checking;

-- -------------------------------------------------------------------- 
-- qual_cityobject 
-- -------------------------------------------------------------------- 
DROP TABLE qual_cityobject;

-- -------------------------------------------------------------------- 
-- qual_edge 
-- -------------------------------------------------------------------- 
DROP TABLE qual_edge;

-- -------------------------------------------------------------------- 
-- qual_edgelist 
-- -------------------------------------------------------------------- 
DROP TABLE qual_edgelist;

-- -------------------------------------------------------------------- 
-- qual_error 
-- -------------------------------------------------------------------- 
DROP TABLE qual_error;

-- -------------------------------------------------------------------- 
-- qual_error_1 
-- -------------------------------------------------------------------- 
DROP TABLE qual_error_1;

-- -------------------------------------------------------------------- 
-- qual_featurestatistics 
-- -------------------------------------------------------------------- 
DROP TABLE qual_featurestatistics;

-- -------------------------------------------------------------------- 
-- qual_filter 
-- -------------------------------------------------------------------- 
DROP TABLE qual_filter;

-- -------------------------------------------------------------------- 
-- qual_geometryerror 
-- -------------------------------------------------------------------- 
DROP TABLE qual_geometryerror;

-- -------------------------------------------------------------------- 
-- qual_globalparameters 
-- -------------------------------------------------------------------- 
DROP TABLE qual_globalparameters;

-- -------------------------------------------------------------------- 
-- qual_parameter 
-- -------------------------------------------------------------------- 
DROP TABLE qual_parameter;

-- -------------------------------------------------------------------- 
-- qual_polygonerror 
-- -------------------------------------------------------------------- 
DROP TABLE qual_polygonerror;

-- -------------------------------------------------------------------- 
-- qual_polygonidlist 
-- -------------------------------------------------------------------- 
DROP TABLE qual_polygonidlist;

-- -------------------------------------------------------------------- 
-- qual_requirement 
-- -------------------------------------------------------------------- 
DROP TABLE qual_requirement;

-- -------------------------------------------------------------------- 
-- qual_ringerror 
-- -------------------------------------------------------------------- 
DROP TABLE qual_ringerror;

-- -------------------------------------------------------------------- 
-- qual_semanticerror 
-- -------------------------------------------------------------------- 
DROP TABLE qual_semanticerror;

-- -------------------------------------------------------------------- 
-- qual_soliderror 
-- -------------------------------------------------------------------- 
DROP TABLE qual_soliderror;

-- -------------------------------------------------------------------- 
-- qual_statistics 
-- -------------------------------------------------------------------- 
DROP TABLE qual_statistics;

-- -------------------------------------------------------------------- 
-- qual_validation 
-- -------------------------------------------------------------------- 
DROP TABLE qual_validation;

-- -------------------------------------------------------------------- 
-- qual_validationplan 
-- -------------------------------------------------------------------- 
DROP TABLE qual_validationplan;

-- -------------------------------------------------------------------- 
-- qual_validationresult 
-- -------------------------------------------------------------------- 
DROP TABLE qual_validationresult;

-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- *********************************** Drop Sequences ************************************* 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 

DROP SEQUENCE qual_error_seq;

DROP SEQUENCE qual_checking_seq;

DROP SEQUENCE qual_edge_seq;

DROP SEQUENCE qual_edgelist_seq;

DROP SEQUENCE qual_error_seq_1;

DROP SEQUENCE qual_featurestatistic_seq;

DROP SEQUENCE qual_filter_seq;

DROP SEQUENCE qual_polygonidlist_seq;

DROP SEQUENCE qual_globalparameters_seq;

DROP SEQUENCE qual_parameter_seq;

DROP SEQUENCE qual_requirement_seq;

DROP SEQUENCE qual_statistics_seq;

DROP SEQUENCE qual_validationplan_seq;

DROP SEQUENCE qual_validationresult_seq;

PURGE RECYCLEBIN;
