package cn.zswltech.mithras.fund.directfinancing.excel.directfinancing;

import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/19 11:01
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FundDirectFinancingProductDetailExcelModel extends ExcelModel {
    /**
     * 证券代码
     */
    @SimpleExcelHeader(headerName = "证券代码", headerOrder = 10)
    private String securitiesCode;

    /**
     * 证券简称
     */
    @SimpleExcelHeader(headerName = "证券简称", headerOrder = 20)
    private String abbreviation;

    /**
     * 发行金额（万元）
     */
    @SimpleExcelHeader(headerName = "发行金额（万元）", headerOrder = 30)
    private String issuanceAmount;

    /**
     * 分层占比（%）
     */
    @SimpleExcelHeader(headerName = "分层占比（%）", headerOrder = 40)
    private String layeredProportion;

    /**
     * 还本方式
     */
    @SimpleExcelHeader(headerName = "还本方式", headerOrder = 50)
    private String repaymentMethod;

    /**
     * 发行利率
     */
    @SimpleExcelHeader(headerName = "发行利率", headerOrder = 60)
    private String issuanceRate;

    /**
     * 年付息次数
     */
    @SimpleExcelHeader(headerName = "年付息次数", headerOrder = 70)
    private String annualPayCount;

    /**
     * 起息日
     */
    @SimpleExcelHeader(headerName = "起息日", headerOrder = 80)
    private LocalDate valueDate;

    /**
     * 预计到期日
     */
    @SimpleExcelHeader(headerName = "预计到期日", headerOrder = 90)
    private LocalDate expectedExpirationDate;

    /**
     * 剩余本金余额（万元）
     */
    @SimpleExcelHeader(headerName = "剩余本金余额（万元）", headerOrder = 100)
    private String remainingPrincipal;

    /**
     * 评级
     */
    @SimpleExcelHeader(headerName = "评级", headerOrder = 110)
    private String rating;

    /**
     * 备注
     */
    @SimpleExcelHeader(headerName = "备注", headerOrder = 120)
    private String remark;
}
