package cn.zswltech.mithras.creditreport.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * 征信报送 辅助 mapper
 *
 * @author wangchuanhao
 * @date 2022/10/14 1:38 PM
 */
public interface CreditReportMapper {

    /**
     * 用于判断该客户是否还需要上报征信
     * （租赁、转租赁）判断已有核销完毕的每个合同的最新版本的数据中的主承租人 = 该客户 && 征信 = 1
     * （保理）判断已有核销完毕的每个合同的最新版本的数据中的第一债权人 = 该客户 && 征信 = 1
     * @param clientId
     * @return
     */
    int countReportClient(@Param("clientId") Long clientId);

    /**
     * 非直租的客户表过滤
     * @param clientId
     * @return
     */
    int countReportClientNew(@Param("clientId") Long clientId);

}
