package cn.zswltech.mithras.application.orchestration.document.onlyoffice.impl;

import cn.zswltech.mithras.dto.onlyoffice.DocDetailRSP;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.document.onlyoffice.OoBizHandler;
import cn.zswltech.mithras.document.mapper.model.MaterialsList;
import org.springframework.stereotype.Component;



@Component
public class ArchivesOoBizHandlerImpl implements OoBizHandler {

    @Override
    public void handle(DocDetailRSP docDetailRSP, MaterialsList materialsList) {
            docDetailRSP.getDocument().getPermissions().setDownload(false);
            docDetailRSP.getDocument().getPermissions().setPrint(false);
            docDetailRSP.getDocument().getPermissions().setEdit(false);
            docDetailRSP.getEditorConfig().setMode("view");
    }

    @Override
    public String getBusinessModule() {
        return BusinessModuleEnum.ARCHIVES.name();
    }




}
