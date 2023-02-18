package com.supresong.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.supresong.wiki.domain.Ebook;
import com.supresong.wiki.domain.EbookExample;
import com.supresong.wiki.mapper.EbookMapper;
import com.supresong.wiki.req.EbookReq;
import com.supresong.wiki.resp.EbookResp;
import com.supresong.wiki.resp.PageResp;
import com.supresong.wiki.util.CopyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class EbookService {

    private static final Logger LOG = LoggerFactory.getLogger(EbookService.class);

    @Autowired
    private EbookMapper ebookMapper;

    public PageResp<EbookResp> list(EbookReq req){
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        if(!ObjectUtils.isEmpty(req.getName())) {
            criteria.andNameLike("%" + req.getName() + "%");
        }
        PageHelper.startPage(req.getPage(),req.getSize());
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);

        PageInfo<Ebook> pageInfo = new PageInfo<>(ebookList);
        LOG.info("总行数:{}",pageInfo.getTotal());
        LOG.info("总页数:{}",pageInfo.getPages());

/*        //创建一个返回对象的实例
        List<EbookResp> respList = new ArrayList<>();
        for (Ebook ebook : ebookList) {
            //在原有查询到的对象里进行遍历,再赋值给返回对象
            //EbookResp ebookResp = new EbookResp();
            //BeanUtils.copyProperties(ebook,ebookResp);

            EbookResp ebookResp = CopyUtil.copy(ebook, EbookResp.class);

            respList.add(ebookResp);
        }*/

        List<EbookResp> respkList = CopyUtil.copyList(ebookList, EbookResp.class);

        PageResp<EbookResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respkList);

        //进行返回经过构建后的返回值
        return pageResp;
    }
}
