package cn.zswltech.mithras.service.mapper.ftp;

import cn.zswltech.mithras.dto.ftp.FtpIncomeBaseInfoListREQ;
import cn.zswltech.mithras.service.mapper.model.ftp.FtpIncomeBaseInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @description 资金管理-融资管理-ftp收益表
* @author vico
* @date 2025-07-15
*/
public interface FtpIncomeBaseInfoMapper extends BaseMapper<FtpIncomeBaseInfo> {

    Page<FtpIncomeBaseInfo> getFundFinancingIds(Page<FtpIncomeBaseInfo> page,
                                  @Param("req") FtpIncomeBaseInfoListREQ req);

    List<FtpIncomeBaseInfo> listFtpIncome(@Param("req") FtpIncomeBaseInfoListREQ req);

    Long countFinancingAmountByDate(@Param("req") FtpIncomeBaseInfoListREQ req);

}