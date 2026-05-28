package cn.zswltech.mithras.dto.version;

import cn.zswltech.mithras.dto.PageReq;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Data
@Accessors(chain = true)
public class CommonVersionListREQ extends PageReq {
    /**
     * 主数据ID
     */
    private Long mainId;
    /**
     * 模块类型
     */
    private String module;
}
