package cn.zswltech.mithras.others.service.contract;

import cn.hutool.core.io.FileUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.contract.application.script.ContractMatcher;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/6 15:53
 */
public class ContractMatcherTest extends ApplicationTest {
    @Resource
    private ContractMatcher contractMatcher;

    @Test
    public void matchTest() throws IOException {
        File f = new File("/Users/zhaozhengkang/1231存量项目台账.xlsx");
        BufferedOutputStream outputStream = FileUtil.getOutputStream("/Users/zhaozhengkang/合同匹配差异结果.xlsx");
        contractMatcher.match(FileUtil.getInputStream(f),outputStream);
    }
}
