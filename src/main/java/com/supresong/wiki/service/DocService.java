package com.supresong.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.supresong.wiki.domain.Doc;
import com.supresong.wiki.domain.DocExample;
import com.supresong.wiki.mapper.DocMapper;
import com.supresong.wiki.req.DocQueryReq;
import com.supresong.wiki.req.DocSaveReq;
import com.supresong.wiki.resp.DocQueryResp;
import com.supresong.wiki.resp.PageResp;
import com.supresong.wiki.util.CopyUtil;
import com.supresong.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class DocService {

    private static final Logger LOG = LoggerFactory.getLogger(DocService.class);

    @Autowired
    private DocMapper docMapper;

    @Autowired
    private SnowFlake snowFlake;

    public List<DocQueryResp> all(){
        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        List<Doc> docList = docMapper.selectByExample(docExample);

        List<DocQueryResp> respkList = CopyUtil.copyList(docList, DocQueryResp.class);

        //进行返回经过构建后的返回值
        return respkList;
    }
    public PageResp<DocQueryResp> list(DocQueryReq req){
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        if(!ObjectUtils.isEmpty(req.getName())) {
            criteria.andNameLike("%" + req.getName() + "%");
        }
        PageHelper.startPage(req.getPage(),req.getSize());
        List<Doc> docList = docMapper.selectByExample(docExample);

        PageInfo<Doc> pageInfo = new PageInfo<>(docList);
        LOG.info("总行数:{}",pageInfo.getTotal());
        LOG.info("总页数:{}",pageInfo.getPages());

/*        //创建一个返回对象的实例
        List<DocResp> respList = new ArrayList<>();
        for (Doc doc : docList) {
            //在原有查询到的对象里进行遍历,再赋值给返回对象
            //DocResp docResp = new DocResp();
            //BeanUtils.copyProperties(doc,docResp);

            DocResp docResp = CopyUtil.copy(doc, DocResp.class);

            respList.add(docResp);
        }*/

        List<DocQueryResp> respkList = CopyUtil.copyList(docList, DocQueryResp.class);

        PageResp<DocQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respkList);

        //进行返回经过构建后的返回值
        return pageResp;
    }

    /*
    * 保存
    * */
    public void save(DocSaveReq req) {
        Doc doc = CopyUtil.copy(req, Doc.class);
        if (ObjectUtils.isEmpty(doc.getId())){
            //新增
            doc.setId(snowFlake.nextId());
            docMapper.insert(doc);
        }else {
            //更新
            docMapper.updateByPrimaryKey(doc);
        }
    }

    public void delete(Long id) {
        docMapper.deleteByPrimaryKey(id);
    }

}
