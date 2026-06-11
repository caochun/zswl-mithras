package cn.zswltech.mithras.ftp.newftp.lib;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.ftp.newftp.enums.NewFtpSubModule;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import cn.zswltech.mithras.foundation.persistence.tag.ILib;
import cn.zswltech.mithras.foundation.version.LibAbstractHandler;
import cn.zswltech.mithras.ftp.newftp.model.NewFtpBaseInfo;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;

/**
 * 客户版本处理器
 *
 * @author wangchuanhao
 * @date 2022/7/19 10:49 PM
 */
public abstract class NewFtpLibAbstractHandler<LIB extends ILib, ENTITY extends IEntity, RSP extends ListBaseRSP> extends LibAbstractHandler<LIB, ENTITY, RSP> {

    @Transactional(rollbackFor = Exception.class)
    public void flushData(String version, NewFtpBaseInfo newFtp, boolean needClearLast, Integer versionType) {
        flushData(version, newFtp.getId(), needClearLast, versionType);
    }

    /**
     * 审批拒绝 把版本表最新的数据还原到 临时表
     *
     * @param newFtp
     * @param version
     */
    @Transactional(rollbackFor = Exception.class)
    public void reset(NewFtpBaseInfo newFtp, String version) {
        reset(newFtp.getId(), version);
    }

    @Override
    public String libMainIdFieldName() {
        return "ftp_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "ftp_id";
    }

    public abstract NewFtpSubModule getSubModule();

    public abstract boolean needHandle(Long mainId);


    @Override
    public Set<String> compareIgnoreFieldNames() {
        return Collections.EMPTY_SET;
    }

    @Override
    protected String businessModuleName() {
        return "NEW_FTP_GUIDANCE";
    }

}
