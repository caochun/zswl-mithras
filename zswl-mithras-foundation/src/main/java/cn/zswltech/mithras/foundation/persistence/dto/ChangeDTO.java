
package cn.zswltech.mithras.foundation.persistence.dto;

import lombok.Data;

/**
 * 数据是否发生变动 用于判断是否可提交版本、是否需要审批
 *
 * @author wangchuanhao
 * @date 2022/7/17 7:54 PM
 */
@Data
public class ChangeDTO {

    /**
     * 数据是否变动
     */
    private Boolean changeFlag = false;

    /**
     * 数据
     */
    private Boolean needApprovalChangeFlag = false;

    /**
     * 历史版本号
     */
    private String version;

}
