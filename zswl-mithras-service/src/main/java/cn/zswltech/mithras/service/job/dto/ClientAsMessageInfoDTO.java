package cn.zswltech.mithras.service.job.dto;

import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import lombok.Data;

import java.util.List;

/**
 * @author zhouning
 * @date 2024/9/25
 * @description 客户作为承租人的业务数据信息
 */
@Data
public class ClientAsMessageInfoDTO {
    private List<ProjEstablishBaseInfo> projEstablishBaseInfoList;
    private List<ProjReviewBaseInfo> projReviewBaseInfoList;
    private List<ContractBaseInfo> contractBaseInfoList;
    private List<PaymentBaseInfo> paymentBaseInfoList;
    private List<PaymentActualDetail> paymentActualDetailList;
}
