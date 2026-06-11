package cn.zswltech.mithras.application.orchestration.client.dto;

import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2024/9/19
 * @description 客户作为承租人的业务数据信息
 */
@Data
public class ClientAsLesseeInfoDTO {
    private List<ProjEstablishBaseInfo> projEstablishBaseInfoList;
    private List<ProjReviewBaseInfo> projReviewBaseInfoList;
    private List<ContractBaseInfo> contractBaseInfoList;
}
