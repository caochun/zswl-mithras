package cn.zswltech.mithras.ftp.oldftp.lib.handler;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.ftp.oldftp.enums.FtpMonthlyInfoModule;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import cn.zswltech.mithras.foundation.persistence.tag.ILib;
import cn.zswltech.mithras.foundation.version.LibAbstractHandler;

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
    protected String businessModuleName() {
        return "FTP_MONTHLY_GUIDANCE";
    }

    public abstract FtpMonthlyInfoModule getSubModule();
}
