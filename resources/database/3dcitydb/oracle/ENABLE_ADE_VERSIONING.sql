-- This document was automatically created by the ADE-Manager tool of 3DCityDB (https://www.3dcitydb.org) on 2022-11-22 08:55:25 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- *********************************** Enable Versioning ********************************** 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 

exec DBMS_WM.EnableVersioning('qual_checking,qual_cityobject,qual_edge,qual_edgelist,qual_error,qual_error_1,qual_featurestatistics,qual_filter,qual_geometryerror,qual_globalparameters,qual_parameter,qual_polygonerror,qual_polygonidlist,qual_requirement,qual_ringerror,qual_semanticerror,qual_soliderror,qual_statistics,qual_validation,qual_validationplan,qual_validationresult','VIEW_WO_OVERWRITE');
