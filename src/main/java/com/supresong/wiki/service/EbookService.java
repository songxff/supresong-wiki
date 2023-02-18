package com.supresong.wiki.service;

import com.supresong.wiki.domain.Ebook;
import com.supresong.wiki.domain.EbookExample;
import com.supresong.wiki.mapper.EbookMapper;
import com.supresong.wiki.req.EbookReq;
import com.supresong.wiki.resp.EbookResp;
import com.supresong.wiki.util.CopyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class EbookService {

    @Autowired
    private EbookMapper ebookMapper;

    public List<EbookResp> list(EbookReq req){
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        if(!ObjectUtils.isEmpty(req.getName())) {
            criteria.andNameLike("%" + req.getName() + "%");
        }
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);
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

        //进行返回经过构建后的返回值
        return respkList;
    }
}
