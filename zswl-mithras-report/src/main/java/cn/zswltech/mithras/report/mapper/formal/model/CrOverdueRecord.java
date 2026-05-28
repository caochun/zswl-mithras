package cn.zswltech.mithras.report.mapper.formal.model;

import cn.zswltech.mithras.report.mapper.base.model.CrOverdueRecordBase;
import lombok.*;
import lombok.experimental.Accessors;


/**
 * @description 征信报送-逾期信息表
 * @author wang
 * @date 2022-10-08
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class CrOverdueRecord extends CrOverdueRecordBase {

}
