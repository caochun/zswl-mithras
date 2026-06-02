package cn.zswltech.mithras.ftp.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.zswltech.mithras.ftp.model.FtpIncomeDetailRecord;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
* @description 资金管理-融资管理-ftp收益记录表
* @author vico
* @date 2025-07-15
*/
public interface FtpIncomeDetailRecordMapper extends BaseMapper<FtpIncomeDetailRecord> {
    List<FtpIncomeDetailRecord> countByDate(@Param("from") LocalDate from, @Param("to") LocalDate to, @Param("ftpIncomeIds")List<Long> ftpIncomeIds);
}