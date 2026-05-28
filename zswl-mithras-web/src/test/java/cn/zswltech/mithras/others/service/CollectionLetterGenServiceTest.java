package cn.zswltech.mithras.others.service;

import cn.zswltech.mithras.service.overdue.domain.collection.CollectionActionId;
import cn.zswltech.mithras.service.overdue.domain.collection.CollectionLetterGenService;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2025/11/27
 * @description
 */
public class CollectionLetterGenServiceTest extends ApplicationTest {
    @Resource
    private CollectionLetterGenService collectionLetterGenService;

    @Test
    public void genLetterTest() {
        collectionLetterGenService.genLetter(new CollectionActionId(79L));
    }
}
