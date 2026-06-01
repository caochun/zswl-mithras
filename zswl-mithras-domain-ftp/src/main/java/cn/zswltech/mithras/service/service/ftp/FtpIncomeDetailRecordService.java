package cn.zswltech.mithras.service.service.ftp;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.ftp.FtpIncomeDetailRecordRemoveREQ;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.mapper.ftp.FtpIncomeDetailRecordMapper;
import cn.zswltech.mithras.service.mapper.model.ftp.FtpIncomeDetailRecord;
import cn.zswltech.mithras.service.others.MithrasException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @description 资金管理-融资管理-ftp收益记录表
* @author vico
* @date 2025-07-15
*/
@Service
public class FtpIncomeDetailRecordService extends ServiceImpl<FtpIncomeDetailRecordMapper, FtpIncomeDetailRecord> {

    @Resource
    private FtpIncomeDetailRecordMapper ftpIncomeDetailRecordMapper;

    public Map<Long, List<FtpIncomeDetailRecord>> listByFtpIncomeIds(List<Long> ftpIncomeIds) {
        return this.baseMapper.selectList(Wrappers.<FtpIncomeDetailRecord>lambdaQuery()
                .in(FtpIncomeDetailRecord::getFtpIncomeId, ftpIncomeIds)
        .orderByDesc(FtpIncomeDetailRecord::getInterestDate)).stream().collect(Collectors.groupingBy(FtpIncomeDetailRecord::getFtpIncomeId));
    }


    public List<FtpIncomeDetailRecord> listByFtpIncomeId(Long ftpIncomeId, LocalDate updateTime) {
        return this.baseMapper.selectList(Wrappers.<FtpIncomeDetailRecord>lambdaQuery()
        .eq(FtpIncomeDetailRecord::getFtpIncomeId, ftpIncomeId)
        .ge(ObjectUtil.isNotEmpty(updateTime), FtpIncomeDetailRecord::getInterestDate, updateTime));
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(FtpIncomeDetailRecordRemoveREQ req) {
        FtpIncomeDetailRecord originalInfo = ftpIncomeDetailRecordMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        ftpIncomeDetailRecordMapper.deleteById(req.getId());
    }

    public Map<Long, Long> countByDate(LocalDate from, LocalDate to, List<Long> ftpIncomeIds) {
        return ftpIncomeDetailRecordMapper.countByDate(from, to, ftpIncomeIds).stream().collect(Collectors.toMap(FtpIncomeDetailRecord::getFtpIncomeId, FtpIncomeDetailRecord::getFtpIncome, (a, b) -> a));
    }
}