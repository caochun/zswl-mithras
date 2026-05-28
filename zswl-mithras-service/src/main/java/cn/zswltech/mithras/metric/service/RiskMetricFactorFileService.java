package cn.zswltech.mithras.metric.service;

import cn.zswltech.mithras.metric.enums.risk.index.RiskMetricFactorTable;
import cn.zswltech.mithras.metric.mapper.RiskMetricFactorFileMapper;
import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactorFile;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author yibin
 */
@Slf4j
@Service
public class RiskMetricFactorFileService extends ServiceImpl<RiskMetricFactorFileMapper, RiskMetricFactorFile> {
    public void trySaveOrUpdate(RiskMetricFactorTable table, LocalDate date) {
        LambdaQueryWrapper<RiskMetricFactorFile> query = Wrappers.lambdaQuery();
        query.eq(RiskMetricFactorFile::getSheetName, table.name());
        query.eq(RiskMetricFactorFile::getSheetDate, date);
        RiskMetricFactorFile exist = this.getOne(query);
        if (Objects.nonNull(exist)) {
            exist.setFileId(-1L);
            exist.setUpdateTime(LocalDateTime.now());
            this.updateById(exist);
        } else {
            RiskMetricFactorFile toSave = new RiskMetricFactorFile();
            toSave.setSheetName(table.name());
            toSave.setSheetDate(date);
            toSave.setFileId(-1L);
            this.save(toSave);
        }
    }
}
