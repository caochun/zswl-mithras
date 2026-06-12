package cn.zswltech.mithras.application.orchestration.workflow.flow.file;

import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.projectprocess.application.model.FileBO;

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
