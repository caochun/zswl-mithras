package cn.zswltech.mithras.dto.version;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 客户数据版本对比
 *
 * @author wangchuanhao
 * @date 2022/6/30 11:31 PM
 */
@Data
public class CommonVersionDiffBO<T> {

    private Boolean moduleChanged = Boolean.FALSE;

    /**
     * 各模块数据的RSP
     * {
     * name: "aaa"
     * }
     */
    private List<T> beforeData;

    /**
     * 各模块数据的RSP 分字段处理成这种格式
     * {
     * name: {
     * value: 'aaa',
     * isChange: true,
     * },
     * }
     */
    private List<Map<String, DiffValue>> afterData;
}
