package com.supresong.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.supresong.wiki.domain.Category;
import com.supresong.wiki.domain.CategoryExample;
import com.supresong.wiki.mapper.CategoryMapper;
import com.supresong.wiki.req.CategoryQueryReq;
import com.supresong.wiki.req.CategorySaveReq;
import com.supresong.wiki.resp.CategoryQueryResp;
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
public class CategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(CategoryService.class);

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private SnowFlake snowFlake;

    public List<CategoryQueryResp> all(){
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.setOrderByClause("sort asc");
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);

        List<CategoryQueryResp> respkList = CopyUtil.copyList(categoryList, CategoryQueryResp.class);

        //进行返回经过构建后的返回值
        return respkList;
    }
    public PageResp<CategoryQueryResp> list(CategoryQueryReq req){
        CategoryExample categoryExample = new CategoryExample();
        CategoryExample.Criteria criteria = categoryExample.createCriteria();
        if(!ObjectUtils.isEmpty(req.getName())) {
            criteria.andNameLike("%" + req.getName() + "%");
        }
        PageHelper.startPage(req.getPage(),req.getSize());
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);

        PageInfo<Category> pageInfo = new PageInfo<>(categoryList);
        LOG.info("总行数:{}",pageInfo.getTotal());
        LOG.info("总页数:{}",pageInfo.getPages());

/*        //创建一个返回对象的实例
        List<CategoryResp> respList = new ArrayList<>();
        for (Category category : categoryList) {
            //在原有查询到的对象里进行遍历,再赋值给返回对象
            //CategoryResp categoryResp = new CategoryResp();
            //BeanUtils.copyProperties(category,categoryResp);

            CategoryResp categoryResp = CopyUtil.copy(category, CategoryResp.class);

            respList.add(categoryResp);
        }*/

        List<CategoryQueryResp> respkList = CopyUtil.copyList(categoryList, CategoryQueryResp.class);

        PageResp<CategoryQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respkList);

        //进行返回经过构建后的返回值
        return pageResp;
    }

    /*
    * 保存
    * */
    public void save(CategorySaveReq req) {
        Category category = CopyUtil.copy(req, Category.class);
        if (ObjectUtils.isEmpty(category.getId())){
            //新增
            category.setId(snowFlake.nextId());
            categoryMapper.insert(category);
        }else {
            //更新
            categoryMapper.updateByPrimaryKey(category);
        }
    }

    public void delete(Long id) {
        categoryMapper.deleteByPrimaryKey(id);
    }

}
