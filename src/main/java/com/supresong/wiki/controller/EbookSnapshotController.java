package com.supresong.wiki.controller;

import com.supresong.wiki.resp.CommonResp;
import com.supresong.wiki.resp.StatisticResp;
import com.supresong.wiki.service.EbookSnapshotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/ebook-snapshot")
public class EbookSnapshotController {

    private static final Logger LOG = LoggerFactory.getLogger(EbookSnapshotController.class);

    @Resource
    private EbookSnapshotService ebookSnapshotService;

    @GetMapping("/get-statistic")
    public CommonResp getStatistic() {
        List<StatisticResp> statisticResp = ebookSnapshotService.getStatistic();
        CommonResp<List<StatisticResp>> commonResp = new CommonResp<>();
        commonResp.setContent(statisticResp);
        LOG.info("在传递值吗?{}", statisticResp.toString());
        return commonResp;
    }
    /**
     * 30天数值统计
     *
     */
    @GetMapping("/get-30-statistic")
    public CommonResp get30Statistic() {
        List<StatisticResp> statisticResp = ebookSnapshotService.get30Statistic();
        CommonResp<List<StatisticResp>> commonResp = new CommonResp<>();
        commonResp.setContent(statisticResp);
        return commonResp;
    }

}
