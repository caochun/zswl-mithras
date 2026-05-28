package cn.zswltech.mithras.dto.leaseholdproperty;

import cn.zswltech.mithras.dto.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author yangxiong
 * @since 2023-08-14
 * @description 租赁物类型请求参数实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LeaseholdPropertyREQ extends PageReq implements Serializable {
    private static final long serialVersionUID = -6688139010735046320L;
}
