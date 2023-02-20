package com.supresong.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.supresong.wiki.domain.Ebook;
import com.supresong.wiki.domain.EbookExample;
import com.supresong.wiki.mapper.EbookMapper;
import com.supresong.wiki.req.EbookQueryReq;
import com.supresong.wiki.req.EbookSaveReq;
import com.supresong.wiki.resp.EbookQueryResp;
import com.supresong.wiki.resp.PageResp;
import com.supresong.wiki.util.CopyUtil;
import com.supresong.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EbookService {

    private static final Logger LOG = LoggerFactory.getLogger(EbookService.class);

    @Resource
    private EbookMapper ebookMapper;

    @Resource
    private SnowFlake snowFlake;

    public PageResp<EbookQueryResp> list(EbookQueryReq req){
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        if(!ObjectUtils.isEmpty(req.getName())) {
            criteria.andNameLike("%" + req.getName() + "%");
        }
        if (!ObjectUtils.isEmpty(req.getCategory2Id())) {
            criteria.andCategory2IdEqualTo(req.getCategory2Id());
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

        List<EbookQueryResp> respkList = CopyUtil.copyList(ebookList, EbookQueryResp.class);

        PageResp<EbookQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respkList);

        //进行返回经过构建后的返回值
        return pageResp;
    }

    /*
    * 保存
    * */
    public void save(EbookSaveReq req) {
        Ebook ebook = CopyUtil.copy(req, Ebook.class);
        if (ObjectUtils.isEmpty(ebook.getId())){
            //新增
            ebook.setId(snowFlake.nextId());
            ebook.setDocCount(0);
            ebook.setViewCount(0);
            ebook.setVoteCount(0);
            ebookMapper.insert(ebook);
        }else {
            //更新
            ebookMapper.updateByPrimaryKey(ebook);
        }
    }

    public void delete(Long id) {
        ebookMapper.deleteByPrimaryKey(id);
    }

}
