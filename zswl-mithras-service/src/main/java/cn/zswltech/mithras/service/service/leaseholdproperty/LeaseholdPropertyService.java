package cn.zswltech.mithras.service.service.leaseholdproperty;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseholdPropertyREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseholdPropertyRSP;
import cn.zswltech.mithras.leaseholdproperty.infrastructure.persistence.mapper.model.LeaseholdProperty;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.InputStream;

/**
* @author yangxiong
* @description 针对表【leasehold_property(租赁物类型表)】的数据库操作Service
* @createDate 2023-08-14 11:20:35
*/
public interface LeaseholdPropertyService extends IService<LeaseholdProperty> {

    /**
     *  租赁物类型分页查询列表
     * @param param LeaseholdPropertyREQ
     * @return R<PageR<LeaseholdPropertyRSP>>
     */
    PageR<LeaseholdPropertyRSP> pageList(LeaseholdPropertyREQ param);

    /**
     *  租赁物类型文件导入
     * @param inputStream 输入流
     */
    void excelImport(InputStream inputStream);
}
