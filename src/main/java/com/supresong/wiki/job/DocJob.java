package com.supresong.wiki.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class DocJob {

   private static final Logger LOG = LoggerFactory.getLogger(DocJob.class);

   /**
    * 自定义cron表达式跑批
    * 只有等上一次执行完成，下一次才会在下一个时间点执行，错过就错过
    */
   @Scheduled(cron = "5/30 * * * * ?")
   public void cron() throws InterruptedException {
       SimpleDateFormat formatter = new SimpleDateFormat("mm:ss SSS");
       String dateString = formatter.format(new Date());
       Thread.sleep(1500);
       LOG.info("每隔1秒钟执行一次： {}", dateString);
   }

}
