package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.mithras.payment.mapper.FtpAssessmentInfoMapper;
import cn.zswltech.mithras.payment.mapper.model.FtpAssessmentInfo;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;

@Component
public class FtpAssessmentInfoReader {

    @Resource
    private FtpAssessmentInfoMapper ftpAssessmentInfoMapper;

    public FtpAssessmentInfo findLatestEffect(LocalDate targetDate, Long receiptId) {
        return ftpAssessmentInfoMapper.selectOne(Wrappers.<FtpAssessmentInfo>lambdaQuery()
                .eq(FtpAssessmentInfo::getReceiptId, receiptId)
                .eq(FtpAssessmentInfo::getIsEffect, YesOrNoNumberEnum.YES.getCode())
                .le(FtpAssessmentInfo::getEffectDate, targetDate)
                .orderByDesc(FtpAssessmentInfo::getEffectDate)
                .orderByDesc(FtpAssessmentInfo::getId)
                .last("LIMIT 1"));
    }
}
