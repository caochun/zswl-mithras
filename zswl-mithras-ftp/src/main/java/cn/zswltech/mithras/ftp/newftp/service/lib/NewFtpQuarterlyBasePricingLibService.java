package cn.zswltech.mithras.ftp.newftp.service.lib;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.newftp.NewFtpDetailReq;
import cn.zswltech.mithras.ftp.newftp.lib.impl.NewFtpQuarterlyBasePricingLibHandler;
import cn.zswltech.mithras.ftp.newftp.mapper.lib.NewFtpQuarterlyBasePricingLibMapper;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpQuarterlyBasePricingDraft;
import cn.zswltech.mithras.ftp.newftp.model.lib.NewFtpQuarterlyBasePricingLib;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 季度指导基础定价
 * @date 2023-05-21
 */
@Service
public class NewFtpQuarterlyBasePricingLibService
        extends ServiceImpl<NewFtpQuarterlyBasePricingLibMapper, NewFtpQuarterlyBasePricingLib> {

    @Resource
    private NewFtpQuarterlyBasePricingLibHandler newFtpQuarterlyBasePricingLibHandler;

    public List<NewFtpQuarterlyBasePricingDraft> list(NewFtpDetailReq req){
        List<NewFtpQuarterlyBasePricingLib> newFtpQuarterlyBasePricingLibs = baseMapper.selectList(Wrappers.<NewFtpQuarterlyBasePricingLib>lambdaQuery()
                .eq(NewFtpQuarterlyBasePricingLib::getFtpId, req.getMainId())
                .eq(NewFtpQuarterlyBasePricingLib::getVersion, req.getVersion()));
        return ObjectUtil.isEmpty(newFtpQuarterlyBasePricingLibs) ? null : newFtpQuarterlyBasePricingLibs.stream().map(newFtpQuarterlyBasePricingLibHandler::actualLib2Entity).collect(Collectors.toList());
    }

}