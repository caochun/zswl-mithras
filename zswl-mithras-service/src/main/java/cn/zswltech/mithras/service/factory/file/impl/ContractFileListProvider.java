package cn.zswltech.mithras.service.factory.file.impl;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.file.FileListREQ;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.file.ext.FileListREQContractExt;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.contract.ContractFileQueryType;
import cn.zswltech.mithras.service.enums.contract.ContractTypeEnum;
import cn.zswltech.mithras.service.factory.file.AbstractFileListProvider;
import cn.zswltech.mithras.service.factory.file.bo.FileListExtQuery;
import cn.zswltech.mithras.service.others.MithrasException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 合同模块
 *
 * @author wangchuanhao
 * @date 2023/2/6 3:03 PM
 */
@Component
public class ContractFileListProvider extends AbstractFileListProvider {

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.CONTRACT;
    }

    @Override
    public List<Pair<String, List<FileListRSP>>> listGroup(FileListREQ req) {
        if (req.getExt() == null) {
            throw new MithrasException("缺少必填参数：文件查询类型");
        }
        FileListREQContractExt extREQ = JSONObject.parseObject(JSON.toJSONString(req.getExt()), FileListREQContractExt.class);
        List<String> materialsTypeList = Optional.ofNullable(ContractFileQueryType.of(extREQ.getQueryType())).map(ContractFileQueryType::getMaterialsTypeList).orElseThrow(() -> new MithrasException("文件查询类型错误"));
        FileListExtQuery extQuery = new FileListExtQuery();
        extQuery.setMaterialsTypes(materialsTypeList);
        return listGroup(req, extQuery);
    }

    @Override
    public PageR<FileListRSP> list(FileListREQ req) {
        if (req.getExt() == null) {
            throw new MithrasException("缺少必填参数：文件查询类型");
        }
        FileListREQContractExt extREQ = JSONObject.parseObject(JSON.toJSONString(req.getExt()), FileListREQContractExt.class);
        List<String> materialsTypeList = Optional.ofNullable(ContractFileQueryType.of(extREQ.getQueryType())).map(ContractFileQueryType::getMaterialsTypeList).orElseThrow(() -> new MithrasException("文件查询类型错误"));
        FileListExtQuery extQuery = new FileListExtQuery();
        extQuery.setMaterialsTypes(materialsTypeList);
        return list(req, extQuery);
    }

    /**
     * 获取文件分组顺序
     * @param rsp
     * @return
     */
    @Override
    protected int getGroupFileSort(FileListRSP rsp) {
        return Optional.ofNullable(ContractTypeEnum.getByName(rsp.getMaterialsType())).map(ContractTypeEnum::getSort).orElse(Integer.MAX_VALUE);
    }

}
