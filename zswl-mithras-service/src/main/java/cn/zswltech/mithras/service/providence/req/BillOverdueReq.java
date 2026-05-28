package cn.zswltech.mithras.service.providence.req;

import cn.zswltech.mithras.dto.PageReq;
import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;

/**
 * @author Jim
 * @version 1.0.0
 * @descripition:
 * @date 2024/12/24 15:33
 */
@Data
@Accessors(chain = true)
public class BillOverdueReq extends PageReq implements Serializable {
    // 名单截止日期
    private String busiDate;

    // 企业名称
    private String orgName;

}
