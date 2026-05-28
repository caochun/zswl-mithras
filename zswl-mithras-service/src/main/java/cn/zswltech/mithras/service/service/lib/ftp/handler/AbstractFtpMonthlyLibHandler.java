package cn.zswltech.mithras.service.service.lib.ftp.handler;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.ftp.FtpMonthlyInfoModule;
import cn.zswltech.mithras.service.enums.ftp.FtpQuarterlyInfoModule;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import cn.zswltech.mithras.service.mapper.tag.ILib;
import cn.zswltech.mithras.service.service.lib.LibAbstractHandler;

import java.util.Collections;
import java.util.Set;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/11 10:54
 */
public abstract class AbstractFtpMonthlyLibHandler<LIB extends ILib, ENTITY extends IEntity, RSP extends ListBaseRSP>
        extends LibAbstractHandler<LIB, ENTITY, RSP> {
    @Override
    public String libMainIdFieldName() {
        return "guidance_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "guidance_id";
    }

    @Override
    public Set<String> compareIgnoreFieldNames() {
        return Collections.emptySet();
    }

    @Override
    public BusinessModuleEnum businessModuleEnum() {
        return BusinessModuleEnum.FTP_MONTHLY_GUIDANCE;
    }

    public abstract FtpMonthlyInfoModule getSubModule();
}
