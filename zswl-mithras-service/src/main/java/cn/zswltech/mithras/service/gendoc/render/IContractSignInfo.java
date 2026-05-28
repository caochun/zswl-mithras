package cn.zswltech.mithras.service.gendoc.render;

import java.util.Set;

/**
 * @author dingqi
 * @date 2024/12/2
 * @description
 */
public interface IContractSignInfo<T> {
    Set<Long> signatories(T t);

    /**
     * 合同面签App是否展示文件
     */
    boolean isShowFile(Object t);

    /**
     * 获取文件模版key
     */
    String getTemplateKey(Object t);
}
