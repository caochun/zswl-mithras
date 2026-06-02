package cn.zswltech.mithras.third.providence.rsp;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author Jim
 * @version 1.0.0
 * @descripition:
 * @date 2024/12/24 15:28
 */
@Data
@Accessors(chain = true)
public class BillOverdueRsp implements Serializable {

    private Long id;

    // 机构名称
    private String orgName;
    // 机构编号
    private String orgCode;
    // 持续逾期开始时间
    private String overdueStartDate;
    // 截止时间
    private String busiDate;

}
