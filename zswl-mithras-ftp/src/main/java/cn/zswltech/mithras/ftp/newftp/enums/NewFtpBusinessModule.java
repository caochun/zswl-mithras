package cn.zswltech.mithras.ftp.newftp.enums;

import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum NewFtpBusinessModule {

    NEW_FTP_GUIDANCE(Arrays.asList(
            ProcessModelTypeEnum.FtpMonthlyGuidanceCreateFlow.name(),
            ProcessModelTypeEnum.FtpMonthlyGuidanceModifyFlow.name()));

    private final List<String> modelKeyList;
}
