package cn.zswltech.mithras.dto.associationreport;
import lombok.Data;
import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description 金融协会报送-最大10家客户（含集团）集中度统计表(流程节点记录版本表)
 * @author hspcadmin
 * @date 2025-09-14
 */
@Data
@ApiModel("金融协会报送-最大10家客户（含集团）集中度统计表(流程节点记录版本表)列表-请求体")
public class AssociationTop10ClientConcentrationLibListREQ extends PageReq {

}
