package cn.zswltech.mithras.customer.externaldata.zhongdeng.application;

import cn.zswltech.mithras.dto.client.external.ExternalPageREQ;
import cn.zswltech.mithras.dto.client.external.zhongdeng.ZhongdengInfoAddREQ;
import cn.zswltech.mithras.dto.client.external.zhongdeng.ZhongdengInfoModifyREQ;
import cn.zswltech.mithras.dto.client.external.zhongdeng.ZhongdengInfoRemoveREQ;
import cn.zswltech.mithras.customer.externaldata.zhongdeng.model.ZhongdengInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 中登网
 *
 * @author wangchuanhao
 * @date 2022/6/21 3:00 PM
 */
public interface ZhongdengInfoService extends IService<ZhongdengInfo> {

    void add(ZhongdengInfoAddREQ req);

    void modify(ZhongdengInfoModifyREQ req);

    Page<ZhongdengInfo> list(ExternalPageREQ req);

    void remove(ZhongdengInfoRemoveREQ req);
}
