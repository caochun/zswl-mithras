package cn.zswltech.mithras.third.tianyancha.infrastructure.client.req;

import lombok.Data;

/**
 * 天眼查接口 通用请求
 *
 * @author wangchuanhao
 * @date 2022/6/20 3:09 PM
 */
@Data
public class TycBaseReq {

    /**
     * 最大20
     */
    private Integer pageSize = 20;

    private Integer pageNum = 1;

    /**
     * 搜索关键字（公司名称、公司id、注册号或社会统⼀信⽤代码）
     */
    private String keyword;

}
