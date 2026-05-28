package cn.zswltech.mithras.others.render.baoli;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.gendoc.render.contract.baoli.wz.WzBaoLiBizRender;
import cn.zswltech.mithras.service.gendoc.render.contract.baoli.wz.WzBaoLiTransferConfirmRender;
import cn.zswltech.mithras.service.gendoc.render.contract.baoli.wz.WzBaoLiTransferNotifyRender;
import cn.zswltech.mithras.service.gendoc.render.contract.baoli.wz.WzBaoLiTransferRegisterProtocolRender;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author yibin
 */

public class 保理无追RenderTest extends ApplicationTest {
    private static final Path basePath = Paths.get(System.getProperty("user.home"), "Desktop");


    @SneakyThrows
    @Test
    public void test4() {
        ContractBaseInfo baseInfo = getBean(ContractBaseInfoService.class).getOne(
                Wrappers.<ContractBaseInfo>lambdaQuery().eq(ContractBaseInfo::getContractCode, "浙商租【2023】保理字第(B-0008)号"));
        FileOutputStream fos = new FileOutputStream(File.createTempFile("应收账款转让登记协议", ".docx", basePath.toFile()));
        getBean(WzBaoLiTransferRegisterProtocolRender.class).render(fos, baseInfo);
    }

    @SneakyThrows
    @Test
    public void test3() {
        ContractBaseInfo baseInfo = getBean(ContractBaseInfoService.class).getOne(
                Wrappers.<ContractBaseInfo>lambdaQuery().eq(ContractBaseInfo::getContractCode, "浙商租【2023】保理字第(B-0008)号"));
        FileOutputStream fos = new FileOutputStream(File.createTempFile("应收账款转让通知书", ".docx", basePath.toFile()));
        getBean(WzBaoLiTransferNotifyRender.class).render(fos, baseInfo);
    }


    @SneakyThrows
    @Test
    public void test2() {
        ContractBaseInfo baseInfo = getBean(ContractBaseInfoService.class).getOne(
                Wrappers.<ContractBaseInfo>lambdaQuery().eq(ContractBaseInfo::getContractCode, "浙商租【2023】保理字第(B-0008)号"));
        FileOutputStream fos = new FileOutputStream(File.createTempFile("应收账款转让申请暨确认书", ".docx", basePath.toFile()));
        getBean(WzBaoLiTransferConfirmRender.class).render(fos, baseInfo);
    }

    @SneakyThrows
    @Test
    public void test1() {
        ContractBaseInfo baseInfo = getBean(ContractBaseInfoService.class).getOne(
                Wrappers.<ContractBaseInfo>lambdaQuery().eq(ContractBaseInfo::getContractCode, "浙商租【2023】保理字第(B-0008)号"));
        FileOutputStream fos = new FileOutputStream(File.createTempFile("国内保理业务合同", ".docx", basePath.toFile()));
        getBean(WzBaoLiBizRender.class).render(fos, baseInfo);
    }
}
