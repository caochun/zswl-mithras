package cn.zswltech.mithras.third.financialshare.application.dto;

import cn.zswltech.mithras.third.enums.FinancialChangeStateENUM;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName FinancialCollectionVO
 * @Description 用于传输租金表至苍穹
 * @Author jackerhe
 * @Date 2023/4/3 1:01 下午
 * @Version 1.0
 **/
@Data
@Accessors(chain = true)
public class FinancialCollectionRentVO {

    //合同ID
    private Long contractId;

    private String contractCode;

    /**
     * 租金利率
     */
    private BigDecimal leaseRate;

    private FinancialChangeStateENUM changeState;

    //借据id
    private Long receiptId;

    //借据编号
    private String receiptCode;

    //借据下要新增的收款明细
    private List<SyncCqReqBody> addRentActual = new ArrayList<>();

    //借据下要更新的收款明细
    private List<SyncCqReqBody> updateRentActual = new ArrayList<>();

    //借据下要删除的收款明细
    private List<SyncCqReqBody> removeRentActual = new ArrayList<>();

}
