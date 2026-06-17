package cn.zswltech.mithras.application.orchestration.document.file.impl;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.file.FileListREQ;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.ftp.oldftp.enums.FtpMonthlyMaterialsEnum;
import cn.zswltech.mithras.application.orchestration.document.file.AbstractFileListProvider;
import cn.zswltech.mithras.document.file.query.FileListExtQuery;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * ftp月度
 *
 * @author wangchuanhao
 * @date 2023/2/6 2:18 PM
 */
@Component
public class FtpMonthlyFileListProvider extends AbstractFileListProvider {

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.FTP_MONTHLY_GUIDANCE;
    }

    @Override
    public PageR<FileListRSP> list(FileListREQ req) {
        FileListExtQuery extQuery = new FileListExtQuery();
        extQuery.setMaterialsTypes(ListUtil.toList(FtpMonthlyMaterialsEnum.DEFAULT.name(), FtpMonthlyMaterialsEnum.SUPPLEMENT.name()));
        return list(req, extQuery);
    }

    @Override
    protected int getGroupFileSort(FileListRSP rsp) {
        return Optional.ofNullable(FtpMonthlyMaterialsEnum.getByName(rsp.getMaterialsType())).map(FtpMonthlyMaterialsEnum::getSort).orElse(Integer.MAX_VALUE);
    }

}
