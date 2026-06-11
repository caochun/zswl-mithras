package cn.zswltech.mithras.dto.fund.financing;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @author dingqi
 * @date 2023/2/20
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("间融授信关联表-返回体")
public class FundFinancingCreditRefDetailRSP extends ListBaseRSP {


    private Long id;

    /**
     * 间融合同id
     */
    private Long financingId;

    /**
     * 授信id
     */
    private Long creditId;

    /**
     * 机构id
     */
    private Long organizationId;


}
