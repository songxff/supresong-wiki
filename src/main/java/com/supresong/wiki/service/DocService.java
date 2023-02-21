package com.supresong.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.supresong.wiki.domain.Content;
import com.supresong.wiki.domain.Doc;
import com.supresong.wiki.domain.DocExample;
import com.supresong.wiki.exception.BusinessException;
import com.supresong.wiki.exception.BusinessExceptionCode;
import com.supresong.wiki.mapper.ContentMapper;
import com.supresong.wiki.mapper.DocMapper;
import com.supresong.wiki.mapper.DocMapperCust;
import com.supresong.wiki.req.DocQueryReq;
import com.supresong.wiki.req.DocSaveReq;
import com.supresong.wiki.resp.DocQueryResp;
import com.supresong.wiki.resp.PageResp;
import com.supresong.wiki.util.CopyUtil;
import com.supresong.wiki.util.RedisUtil;
import com.supresong.wiki.util.RequestContext;
import com.supresong.wiki.util.SnowFlake;
import com.supresong.wiki.websocket.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DocService {

    private static final Logger LOG = LoggerFactory.getLogger(DocService.class);

    @Resource
    private DocMapper docMapper;

    @Resource
    private ContentMapper contentMapper;

    @Resource
    private DocMapperCust docMapperCust;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private SnowFlake snowFlake;

    @Resource
    private WebSocketServer webSocketServer;

    public List<DocQueryResp> all(Long ebookId){
        DocExample docExample = new DocExample();
        docExample.createCriteria().andEbookIdEqualTo(ebookId);
        docExample.setOrderByClause("sort asc");
        List<Doc> docList = docMapper.selectByExample(docExample);

        List<DocQueryResp> respkList = CopyUtil.copyList(docList, DocQueryResp.class);

        //进行返回经过构建后的返回值
        return respkList;
    }
    /**
     * 分页查询
     * @param req
     * @return
     */
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
        Content content = CopyUtil.copy(req, Content.class);
        if (ObjectUtils.isEmpty(doc.getId())){
            //新增
            doc.setId(snowFlake.nextId());
            doc.setViewCount(0);
            doc.setVoteCount(0);
            docMapper.insert(doc);
            content.setId(doc.getId());
            contentMapper.insert(content);
        }else {
            //更新
            docMapper.updateByPrimaryKey(doc);

            int count = contentMapper.updateByPrimaryKeyWithBLOBs(content);
            if (count == 0){
                contentMapper.insert(content);
            }
        }
    }

    public void delete(Long id) {
        docMapper.deleteByPrimaryKey(id);
    }

    /**
     * 删除
     */
    public void delete(List<String> ids) {
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);
    }

    /**
     * 富文本内容查询
     * @param id
     * @return
     */
    public String findContent(Long id) {
        Content content = contentMapper.selectByPrimaryKey(id);
        // 文档阅读数+1
        docMapperCust.increaseViewCount(id);
        if (ObjectUtils.isEmpty(content)) {
            return "";
        } else {
            return content.getContent();
        }


    }

    /**
     * 点赞数加一
     * @param id
     */
    public void vote(Long id) {
        // docMapperCust.increaseVoteCount(id);
        // 远程IP+doc.id作为key，24小时内不能重复
        String ip = RequestContext.getRemoteAddr();
        if (redisUtil.validateRepeat("DOC_VOTE_" + id + "_" + ip, 3600 * 24)) {
            docMapperCust.increaseVoteCount(id);
        } else {
            throw new BusinessException(BusinessExceptionCode.VOTE_REPEAT);
        }

        // 推送消息
        Doc docDb = docMapper.selectByPrimaryKey(id);
        webSocketServer.sendInfo("【" + docDb.getName() + "】被点赞！");
    }

    /**
     * 定时更新电子书信息
     */
    public void updateEbookInfo() {
        docMapperCust.updateEbookInfo();
    }
}
