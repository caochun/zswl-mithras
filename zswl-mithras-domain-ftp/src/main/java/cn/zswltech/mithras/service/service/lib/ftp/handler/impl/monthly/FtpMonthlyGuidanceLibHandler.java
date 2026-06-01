package cn.zswltech.mithras.service.service.lib.ftp.handler.impl.monthly;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.ftp.FtpMonthlyGuidanceDetailRsp;
import cn.zswltech.mithras.service.enums.ftp.FtpMonthlyInfoModule;
import cn.zswltech.mithras.service.mapper.model.ftp.FtpMonthlyGuidance;
import cn.zswltech.mithras.service.mapper.model.ftp.FtpMonthlyGuidanceLib;
import cn.zswltech.mithras.service.service.lib.ftp.handler.AbstractFtpMonthlyLibHandler;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/11 16:56
 */
@Service
public class FtpMonthlyGuidanceLibHandler
        extends AbstractFtpMonthlyLibHandler<FtpMonthlyGuidanceLib, FtpMonthlyGuidance, FtpMonthlyGuidanceDetailRsp> {

    @Override
    public Set<String> compareIgnoreFieldNames() {
        HashSet<String> fields = new HashSet<>();
        fields.add("guidanceProcessStatus");
        fields.add("guidanceRecordStatus");
        return fields;
    }
    @Override
    protected FtpMonthlyGuidanceLib entity2Lib(FtpMonthlyGuidance f) {
        return BeanUtil.copyProperties(f, FtpMonthlyGuidanceLib.class);
    }

    @Override
    protected FtpMonthlyGuidance lib2Entity(FtpMonthlyGuidanceLib t) {
        return BeanUtil.copyProperties(t, FtpMonthlyGuidance.class);
    }

    @Override
    protected FtpMonthlyGuidanceDetailRsp lib2Rsp(FtpMonthlyGuidanceLib f) {
        FtpMonthlyGuidanceDetailRsp rsp = BeanUtil.copyProperties(f, FtpMonthlyGuidanceDetailRsp.class);
        rsp.setId(f.getOriginId());
        return rsp;
    }

    @Override
    public FtpMonthlyInfoModule getSubModule() {
        return FtpMonthlyInfoModule.GUIDANCE;
    }
    @Override
    public String libMainIdFieldName() {
        return "origin_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "id";
    }
}
