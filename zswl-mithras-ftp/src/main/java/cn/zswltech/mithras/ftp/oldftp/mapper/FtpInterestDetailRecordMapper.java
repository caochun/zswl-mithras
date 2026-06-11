package cn.zswltech.mithras.ftp.oldftp.mapper;

import cn.zswltech.mithras.ftp.oldftp.model.FtpInterestDetailRecord;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2023/5/16
 * @description
 */
public interface FtpInterestDetailRecordMapper extends CustomBaseMapper<FtpInterestDetailRecord> {
    @Select("select ifnull(sum(ifnull(cash_interest,0)+ifnull(bill_interest,0)),0) from ftp_interest_detail_record where ftp_interest_id = #{ftpInterestId} and interest_date >= #{startDate} and interest_date <= #{endDate}")
    long sumTotalInterestBetweenTargetRange(@Param("ftpInterestId") Long ftpInterestId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Select("select ifnull(sum(ifnull(cash_occupy,0)),0) from ftp_interest_detail_record where ftp_interest_id = #{ftpInterestId} and interest_date >= #{startDate} and interest_date <= #{endDate} and cash_ftp > 0")
    long sumTotalCashOccupyBetweenTargetRange(@Param("ftpInterestId") Long ftpInterestId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Select("select ifnull(sum(ifnull(bill_occupy,0)),0) from ftp_interest_detail_record where ftp_interest_id = #{ftpInterestId} and interest_date >= #{startDate} and interest_date <= #{endDate} and bill_ftp > 0")
    long sumTotalBillOccupyBetweenTargetRange(@Param("ftpInterestId") Long ftpInterestId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
