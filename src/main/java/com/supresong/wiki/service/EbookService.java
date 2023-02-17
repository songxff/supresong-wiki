package com.supresong.wiki.service;

import com.supresong.wiki.domain.Ebook;
import com.supresong.wiki.mapper.EbookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EbookService {

    @Autowired
    private EbookMapper demoMapper;

    public List<Ebook> list(){
        return demoMapper.selectByExample(null);
    }
}
