/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.export.facade;

import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.utilities.BusinessObjectUtility;
import com.bjit.common.rest.app.service.controller.export.contracts.IExpand;
import com.bjit.common.rest.app.service.controller.export.contracts.IExport;
import com.bjit.common.rest.app.service.controller.export.factories.ExpandFactory;
import com.bjit.common.rest.app.service.controller.export.factories.ExportFactory;
import com.bjit.common.rest.app.service.controller.export.factories.MapFileDirectoryFactory;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.mapper.mapproject.builder.MapperBuilder;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.ExpansionWithSelect;
import matrix.util.MatrixException;
import org.apache.log4j.Logger;

/**
 *
 * @author BJIT
 */
public class ExportFacade {

    private static final Logger BOM_EXPAND_LOGGER = Logger.getLogger(ExportFacade.class);

    public List<Map<String, String>> processExport(Context context, TNR tnr, String exportType, BusinessObjectOperations businessObjectOperations) throws ClassNotFoundException, IllegalAccessException, InstantiationException, MatrixException, IOException, Exception {
        try {
            BOM_EXPAND_LOGGER.info("Instantiating 'IExpand' object from 'ExpandFactory'");
            IExpand expansionObject = ExpandFactory.getExpansionObject(exportType);

            BOM_EXPAND_LOGGER.info("Instantiating 'MapperBuilder'");
            MapperBuilder mapperBuilder = new MapperBuilder();
            BOM_EXPAND_LOGGER.info("Getting mapper files directory");
            String mapFile = MapFileDirectoryFactory.getMapFileDirectory(exportType);
            BOM_EXPAND_LOGGER.debug("Mapper files absolute path is : " + mapFile);

            BOM_EXPAND_LOGGER.info("Searching for objcts existence");
            
            BusinessObjectUtility businessObjectUtility = businessObjectOperations.getBusinessObjectUtility();
            
            String objectId = businessObjectUtility.searchByTypeName(context, tnr.getType(), tnr.getName());
            BOM_EXPAND_LOGGER.debug("Object id is : " + objectId);

            if (NullOrEmptyChecker.isNullOrEmpty(objectId)) {
                String error = "Type : '" + tnr.getType() + "' Name : '" + tnr.getName() + "' is not present in the system";
                BOM_EXPAND_LOGGER.error(error);
                throw new RuntimeException(error);
            }

            BusinessObject businessObject = businessObjectOperations.getObject(objectId);

            IExpand expandObject = expansionObject.__init__(MapperBuilder.XML, mapperBuilder, mapFile);
            ExpansionWithSelect expandedData = expandObject.getExpandedData(context, businessObject);

            IExport exportProcessor = ExportFactory.getExportProcessor(exportType);
            List<Map<String, String>> expandedResult = exportProcessor.__init__(expandObject, expandedData).process();
            return expandedResult;

        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException exp) {
            BOM_EXPAND_LOGGER.error(exp);
            throw exp;
        } catch (MatrixException | RuntimeException | IOException exp) {
            BOM_EXPAND_LOGGER.error(exp);
            throw exp;
        } catch (Exception exp) {
            BOM_EXPAND_LOGGER.error(exp);
            throw exp;
        }
    }
}
