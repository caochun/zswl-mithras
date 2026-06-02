package cn.zswltech.mithras.contract.overdue.domain.share.diff;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/14 16:14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Diff {

    private DiffType type;

    private Object oldValue;

    private Object newValue;
}
