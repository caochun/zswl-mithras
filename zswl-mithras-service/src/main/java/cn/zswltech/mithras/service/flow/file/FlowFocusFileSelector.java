package cn.zswltech.mithras.service.flow.file;

import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.service.bo.FileBO;

import java.util.List;

/**
 * @author dingqi
 * @date 2024/2/5
 * @description
 */
public interface FlowFocusFileSelector {
    List<FileBO> listBizImportantFile(String businessKey, String version);

    List<FileBO> listMeetingDecisionFile(String businessKey, String version);

    ProcessModelTypeEnum processType();
}
