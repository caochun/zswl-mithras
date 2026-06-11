package cn.zswltech.mithras.application.orchestration.client.dto;

import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
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
