package cn.zswltech.mithras.ftp.newftp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum NewFtpBusinessModule {

    NEW_FTP_GUIDANCE(Arrays.asList(
            NewFtpWorkflowKey.FTP_MONTHLY_GUIDANCE_CREATE,
            NewFtpWorkflowKey.FTP_MONTHLY_GUIDANCE_MODIFY));

    private final List<String> modelKeyList;
}
