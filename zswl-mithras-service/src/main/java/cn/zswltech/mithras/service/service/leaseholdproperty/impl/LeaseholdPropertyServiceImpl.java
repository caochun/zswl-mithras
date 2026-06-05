package cn.zswltech.mithras.service.service.leaseholdproperty.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseholdPropertyREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseholdPropertyRSP;
import cn.zswltech.mithras.leaseholdproperty.excel.importer.LeaseholdPropertyExcelImporter;
import cn.zswltech.mithras.leaseholdproperty.excel.model.LeaseholdPropertyExcelModel;
import cn.zswltech.mithras.leaseholdproperty.infrastructure.persistence.mapper.LeaseholdPropertyMapper;
import cn.zswltech.mithras.leaseholdproperty.infrastructure.persistence.mapper.model.LeaseholdProperty;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.basedata.service.leaseholdproperty.GeneralDictionaryService;
import cn.zswltech.mithras.service.service.leaseholdproperty.LeaseholdPropertyService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yangxiong
 * @description 针对表【leasehold_property(租赁物类型表)】的数据库操作Service实现
 * @createDate 2023-08-14 11:20:35
 */
@Service
public class LeaseholdPropertyServiceImpl extends ServiceImpl<LeaseholdPropertyMapper, LeaseholdProperty>
        implements LeaseholdPropertyService {

    private static final String OTHER = "其他租赁物";

    @Resource
    private Id2NameService id2NameService;

    @Resource
    private GeneralDictionaryService generalDictionaryService;

    @Resource
    private LeaseholdPropertyExcelImporter leaseholdPropertyExcelImporter;

    @Override
    public PageR<LeaseholdPropertyRSP> pageList(LeaseholdPropertyREQ param) {
        Page<LeaseholdProperty> leaseholdPropertyPage = new Page<>();
        leaseholdPropertyPage.setCurrent(param.getPage()).setSize(param.getPageSize());
        //后续有新的条件再考虑加wrapper
        Page<LeaseholdProperty> page = this.page(leaseholdPropertyPage);
        //没有数据直接返回空
        if (CollUtil.isEmpty(page.getRecords())) {
            return PageR.empty(param.getPage(), param.getPageSize());
        }
        List<LeaseholdPropertyRSP> result = new ArrayList<>(param.getPageSize());
        Map<Long, String> userMap = getUserMap(page);
        fillData(page.getRecords(), result, userMap);
        return PageR.of(result, page.getTotal(), page.getCurrent(), param.getPageSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void excelImport(InputStream inputStream) {
        //先删除原有数据
        remove(null);
        List<LeaseholdPropertyExcelModel> excelModels = leaseholdPropertyExcelImporter.parse(inputStream);
        Assert.notEmpty(excelModels, () -> MithrasException.newException("导入数据为空"));
        List<LeaseholdProperty> list = new ArrayList<>();
        //只取一次用户信息，提高效率
        AccountVO loginInfo = AccountUtil.getLoginInfo();
        List<String> nameList = generalDictionaryService.getNameList();
        for (LeaseholdPropertyExcelModel model : excelModels) {
            //如果合同编号不存在直接舍弃
            if (CharSequenceUtil.isEmpty(model.getContractCode())) {
                continue;
            }
            list.add(LeaseholdProperty.builder()
                    .contractCode(processingStr(model.getContractCode()))
                    .type(nameList.contains(model.getType()) ? model.getType() : OTHER)
                    .updateBy(loginInfo.getId())
                    .createBy(loginInfo.getId())
                    .build());
        }
        saveBatch(list);
    }

    private String processingStr(String str) {
        return CharSequenceUtil.trim(str).replace("（", "(").replace("）", ")");
    }

    private Map<Long, String> getUserMap(Page<LeaseholdProperty> page) {
        //查询修改人相关信息，可不要
        List<Long> createIds = page.getRecords().stream().map(LeaseholdProperty::getCreateBy).collect(Collectors.toList());
        List<Long> updateIds = page.getRecords().stream().map(LeaseholdProperty::getUpdateBy).collect(Collectors.toList());
        HashSet<Long> ids = new HashSet<>(createIds);
        ids.addAll(updateIds);
        return id2NameService.sysUserId2Name(ids);
    }

    /**
     * @param records 分页数据
     * @param result  结果数据
     * @param userMap 用户信息映射
     */
    private void fillData(List<LeaseholdProperty> records, List<LeaseholdPropertyRSP> result, Map<Long, String> userMap) {
        records.forEach(dto -> {
            LeaseholdPropertyRSP rsp = new LeaseholdPropertyRSP();
            rsp.setContractCode(dto.getContractCode());
            rsp.setId(dto.getId());
            rsp.setCreateBy(userMap.get(dto.getCreateBy()));
            rsp.setUpdateBy(userMap.get(dto.getUpdateBy()));
            rsp.setType(dto.getType());
            rsp.setUpdateTime(DateUtil.format(dto.getUpdateTime(), cn.zswltech.mithras.basedata.util.DateUtil.DATE_TIME_PATTERN));
            rsp.setCreateTime(DateUtil.format(dto.getCreateTime(), cn.zswltech.mithras.basedata.util.DateUtil.DATE_TIME_PATTERN));
            result.add(rsp);
        });
    }
}




