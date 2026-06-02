package cn.zswltech.mithras.service.service.ftp;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.ftp.FtpGuidanceIdReq;
import cn.zswltech.mithras.dto.ftp.FtpMonthlyValuationRsp;
import cn.zswltech.mithras.service.convert.ftp.FtpMonthlyGuidanceConverter;
import cn.zswltech.mithras.ftp.mapper.FtpMonthlyValuationMapper;
import cn.zswltech.mithras.ftp.mapper.lib.FtpMonthlyValuationLibMapper;
import cn.zswltech.mithras.ftp.model.FtpMonthlyValuation;
import cn.zswltech.mithras.ftp.model.FtpMonthlyValuationLib;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 月度计价指导
 * @date 2023-01-10
 */
@Service
public class FtpMonthlyValuationService extends ServiceImpl<FtpMonthlyValuationMapper, FtpMonthlyValuation> {
    @Resource
    private FtpMonthlyValuationLibMapper libMapper;
    @Resource
    private FtpMonthlyGuidanceConverter converter;

    public List<FtpMonthlyValuationRsp> detailValuation(FtpGuidanceIdReq req) {
        if(ObjectUtil.isNotEmpty(req.getVersion())){
            List<FtpMonthlyValuationLib> vals = libMapper.selectList(Wrappers.<FtpMonthlyValuationLib>lambdaQuery()
                    .eq(FtpMonthlyValuationLib::getVersion, req.getVersion())
                    .eq(FtpMonthlyValuation::getGuidanceId, req.getId()));
            List<FtpMonthlyValuationRsp> rspList = vals.stream().map(valuationLib -> {
                FtpMonthlyValuationRsp valuationRsp = converter.valuationLib2Rsp(valuationLib);
                valuationRsp.setId(valuationLib.getOriginId());
                return valuationRsp;
            }).collect(Collectors.toList());

            return rspList;
        }else {
            return converter.valuationEntity2Rsp(baseMapper.selectList(Wrappers.<FtpMonthlyValuation>lambdaQuery()
                    .eq(FtpMonthlyValuation::getGuidanceId, req.getId())));
        }
    }
    public List<FtpMonthlyValuation> selectByGuidance(Long id) {
        return baseMapper.selectList(Wrappers.<FtpMonthlyValuation>lambdaQuery()
                .eq(FtpMonthlyValuation::getGuidanceId, id));

    }
}