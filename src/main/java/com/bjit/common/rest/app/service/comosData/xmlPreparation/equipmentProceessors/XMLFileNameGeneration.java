package com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors;


import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices.IFilenameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLP;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLVPMItem;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentChild;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;

@Component
//@Scope("prototype")
public class XMLFileNameGeneration implements IFilenameGenerator {
    private static final Logger XMLFileNameGeneration_LOGGER = Logger.getLogger(XMLFileNameGeneration.class);
    @Autowired XMLAttributeGenerator xmlAttributeGenerator;

    @Value("${comos.number.of.units.in.an.xml.file}")
    private int MAXIMUM_XML_CHILDREN_NUMBER;

    @Override
    public HashMap<String, String> setMillIdAndEquipmentId(String millId, String equipmentId, String prefix, Integer level) {
        HashMap<String, String> levelWiseFileName = new HashMap<>();
        String xmlFileName = "mill-id_" + millId + "_eq-id_" + equipmentId;
        levelWiseFileName.put("firstFileName", xmlFileName);
        xmlFileName += "_" + prefix + "_" + level + "_0001";
        levelWiseFileName.put(prefix + "_" + level, xmlFileName); // First file name should be like mill-id_119160_eq-id_119160.135_U_1_0001
        return levelWiseFileName;
    }

    @Override
    public String generateFileName(HashMap<String, String> levelWiseFileName, HashMap<String, RFLP> structureMap, int level, String prefix, EquipmentChild parent) {
        String xmlFileName;
        try {
            String levelCategory = prefix + "_" + level;

            RFLP rflp = structureMap.get(levelWiseFileName.get(levelCategory));
            List<RFLVPMItem> referenceIds = rflp.getLogicalReference().getId();

            int referenceSize = referenceIds.size();
            referenceSize = parent.getIsAParentItem() ? 1 : referenceSize;

            xmlFileName = xmlFilename(levelWiseFileName, structureMap, level, referenceSize, prefix);
        } catch (NullPointerException exp) {
            xmlFileName = xmlFilename(levelWiseFileName, structureMap, level, 0, prefix);
        }

        return xmlFileName;
    }

    private String xmlFilename(HashMap<String, String> levelWiseFileName, HashMap<String, RFLP> structureMap, Integer level, Integer size, String category) {
        String firstLevelFilename = levelWiseFileName.get("firstFileName");
        String levelCategory = category + "_" + level;
        String leveledFilename = levelWiseFileName.get(levelCategory);

        if (size < MAXIMUM_XML_CHILDREN_NUMBER) {
            if (leveledFilename == null || leveledFilename.isEmpty()) {
                leveledFilename = firstLevelFilename + "_" + levelCategory + "_" + String.format("%04d", 1);

                levelWiseFileName.put(levelCategory, leveledFilename);
                structureMap.put(leveledFilename, xmlAttributeGenerator.getRflp());
            }
        } else {
            if (leveledFilename == null || leveledFilename.isEmpty()) {
                leveledFilename = firstLevelFilename + "_" + levelCategory + "_" + String.format("%04d", 1);

                levelWiseFileName.put(levelCategory, leveledFilename);
                structureMap.put(leveledFilename, xmlAttributeGenerator.getRflp());
            } else {
                String listSize = null;
                try {
                    listSize = leveledFilename.split("_")[6];
                } catch (ArrayIndexOutOfBoundsException exp) {
                    listSize = "0000";
                }
                Integer lastData = Integer.parseInt(listSize) + 1;
                leveledFilename = firstLevelFilename + "_" + levelCategory + "_" + String.format("%04d", lastData);

                levelWiseFileName.put(levelCategory, leveledFilename);
            }

            structureMap.put(leveledFilename, xmlAttributeGenerator.getRflp());
        }
        XMLFileNameGeneration_LOGGER.debug("level wise Filename has been generated ");
        
        return leveledFilename;
    }
}
