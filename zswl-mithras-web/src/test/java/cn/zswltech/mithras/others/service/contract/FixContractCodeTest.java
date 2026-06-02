package cn.zswltech.mithras.others.service.contract;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractBaseInfoLibMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfoLib;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

/**
 * 修复合同主编号数据
 *
 * @author wangchuanhao
 * @date 2022/10/12 4:35 PM
 */
public class FixContractCodeTest extends ApplicationTest {

    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ContractBaseInfoLibMapper contractBaseInfoLibMapper;

    @Test
    public void test() {
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoMapper.selectList(Wrappers.lambdaQuery());
        List<ContractBaseInfoLib> contractBaseInfoLibList = contractBaseInfoLibMapper.selectList(Wrappers.lambdaQuery());
        contractBaseInfoList.forEach(c -> {
            if (StringUtils.isBlank(c.getContractCode())){
                return;
            }
            String contractCode = c.getContractCode();
            if (contractCode.indexOf(")") == -1) {
                return;
            }
            Integer year = Integer.valueOf(contractCode.substring(contractCode.indexOf("【")+1, contractCode.indexOf("】")));
            String[] ss = contractCode.substring(contractCode.indexOf("(")+1,contractCode.indexOf(")")).split("-");
            Integer sequence = Integer.valueOf(ss[ss.length-1]);
            c.setContractYear(year);
            c.setSequence(sequence);
            contractBaseInfoMapper.updateById(c);
        });
        contractBaseInfoLibList.forEach(c -> {
            if (StringUtils.isBlank(c.getContractCode())){
                return;
            }
            String contractCode = c.getContractCode();

            if (contractCode.indexOf(")") == -1) {
                return;
            }
            Integer year = Integer.valueOf(contractCode.substring(contractCode.indexOf("【")+1, contractCode.indexOf("】")));
            String[] ss = contractCode.substring(c.getContractCode().indexOf("(")+1,contractCode.indexOf(")")).split("-");
            Integer sequence = Integer.valueOf(ss[ss.length-1]);
            c.setContractYear(year);
            c.setSequence(sequence);
            contractBaseInfoLibMapper.updateById(c);
        });
    }

}
