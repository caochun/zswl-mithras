package cn.zswltech.mithras.ftp.newftp.service.lib;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.newftp.NewFtpDetailReq;
import cn.zswltech.mithras.ftp.newftp.lib.impl.NewFtpMonthlyGuidanceLibHandler;
import cn.zswltech.mithras.ftp.newftp.mapper.lib.NewFtpMonthlyGuidanceLibMapper;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpMonthlyGuidanceDraft;
import cn.zswltech.mithras.ftp.newftp.model.lib.NewFtpMonthlyGuidanceLib;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description ftp报价表
 * @date 2023-05-21
 */
@Service
public class NewFtpMonthlyGuidanceLibService
        extends ServiceImpl<NewFtpMonthlyGuidanceLibMapper, NewFtpMonthlyGuidanceLib> {

    @Resource
    private NewFtpMonthlyGuidanceLibHandler newFtpMonthlyGuidanceLibHandler;
    public List<NewFtpMonthlyGuidanceDraft> list(NewFtpDetailReq req){
        List<NewFtpMonthlyGuidanceLib> newFtpMonthlyGuidanceLibs = baseMapper.selectList(Wrappers.<NewFtpMonthlyGuidanceLib>lambdaQuery()
                .eq(NewFtpMonthlyGuidanceLib::getFtpId, req.getMainId())
                .eq(NewFtpMonthlyGuidanceLib::getVersion, req.getVersion()));
       return ObjectUtil.isEmpty(newFtpMonthlyGuidanceLibs) ? null : newFtpMonthlyGuidanceLibs.stream().map(newFtpMonthlyGuidanceLibHandler::actualLib2Entity).collect(Collectors.toList());
    }

}