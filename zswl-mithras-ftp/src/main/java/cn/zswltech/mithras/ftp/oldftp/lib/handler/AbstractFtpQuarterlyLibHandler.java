package cn.zswltech.mithras.ftp.oldftp.lib.handler;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.ftp.oldftp.enums.FtpQuarterlyInfoModule;
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
public abstract class AbstractFtpQuarterlyLibHandler<LIB extends ILib, ENTITY extends IEntity, RSP extends ListBaseRSP>
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
    protected String businessModuleName() {
        return "FTP_QUARTERLY_GUIDANCE";
    }

    public abstract FtpQuarterlyInfoModule getSubModule();
}
