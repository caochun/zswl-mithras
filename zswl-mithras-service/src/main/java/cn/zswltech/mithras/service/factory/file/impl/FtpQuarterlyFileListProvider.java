package cn.zswltech.mithras.service.factory.file.impl;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.file.FileListREQ;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.ftp.enums.FtpQuarterlyMaterialsEnum;
import cn.zswltech.mithras.service.factory.file.AbstractFileListProvider;
import cn.zswltech.mithras.service.factory.file.bo.FileListExtQuery;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * ftp季度
 *
 * @author wangchuanhao
 * @date 2023/2/6 2:18 PM
 */
@Component
public class FtpQuarterlyFileListProvider extends AbstractFileListProvider {

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.FTP_QUARTERLY_GUIDANCE;
    }

    @Override
    public PageR<FileListRSP> list(FileListREQ req) {
        FileListExtQuery extQuery = new FileListExtQuery();
        extQuery.setMaterialsTypes(ListUtil.toList(FtpQuarterlyMaterialsEnum.DEFAULT.name(), FtpQuarterlyMaterialsEnum.SUPPLEMENT.name()));
        return list(req, extQuery);
    }

    @Override
    protected int getGroupFileSort(FileListRSP rsp) {
        return Optional.ofNullable(FtpQuarterlyMaterialsEnum.getByName(rsp.getMaterialsType())).map(FtpQuarterlyMaterialsEnum::getSort).orElse(Integer.MAX_VALUE);
    }

}
