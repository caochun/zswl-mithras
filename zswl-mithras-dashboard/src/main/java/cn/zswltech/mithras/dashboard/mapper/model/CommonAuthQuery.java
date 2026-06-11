package cn.zswltech.mithras.dashboard.mapper.model;

import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author dingqi
 * @date 2024/6/16
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommonAuthQuery extends CommonLimitQuery {
    private List<Long> authBizDeptIds;
    private Long authCurrentUserId;
    private AccountVO accountVO;
}
