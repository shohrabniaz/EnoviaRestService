package com.bjit.common.rest.app.service.comosData.xmlPreparation.batchTool.com.bjit;


import com.bjit.common.rest.app.service.comosData.xmlPreparation.batchTool.com.bjit.comos.consumer.ServiceConsumer;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.integrationProcessors.IBatchToolRunner;
//import com.bjit.comos.consumer.ServiceConsumer;
//import com.bjit.services.IBatchToolRunner;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Log4j
//@SpringBootApplication
public class BjitBatchToolApplication {
    private static ApplicationContext ctx;
    public File lockfile;
    public File directoryPath;
    @Autowired
    ServiceConsumer serviceConsumer;
    @Autowired
    Environment env;

    public static void main(String[] args) {
        SpringApplication.run(BjitBatchToolApplication.class, args);
    }

    private static void greetings() {
        log.info("######################################");
        log.info("#                                    #");
        log.info("#                                    #");
        log.info("#    Starting BJIT Batch Tool 1.0    #");
        log.info("#                                    #");
        log.info("#                                    #");
        log.info("######################################");
    }

    @Bean
    public Boolean runApplication(ApplicationContext ctx) throws IOException {
        directoryPath = new File(env.getProperty("comos.jar.directory"));
        if (checkingLockFile(directoryPath)) {
            log.info("######################################");
            log.info("#                                    #");
            log.info("#                                    #");
            log.info("#    lock exist....................   #");
            log.info("#                                    #");
            log.info("#                                    #");
            log.info("######################################");
            return true;
        } else {
            String property = env.getProperty("comos.greetings.flag");
            log.info("Greeting flag is : " + property);
            if (Boolean.parseBoolean(property)) {
                greetings();
            }
            log.info("######################################");
            log.info("#                                    #");
            log.info("#                                    #");
            log.info("#    NO lock.....................    #");
            log.info("#                                    #");
            log.info("#                                    #");
            log.info("######################################");
            String lockFileDirectory = env.getProperty("comos.jar.directory") + "\\" + "comos_scheduler.lock";
            log.info(lockFileDirectory);
            lockfile = new File(lockFileDirectory);
            lockfile.createNewFile();

            IBatchToolRunner batchToolRunner = BeanFactoryAnnotationUtils.qualifiedBeanOfType(ctx.getAutowireCapableBeanFactory(), IBatchToolRunner.class, "LogicalStructureXMLImporterImpls");
            try {
                String batchToolFlag = env.getProperty("comos.batchTool.flag");
                log.info("Batch tool flag is : " + batchToolFlag);
                if (Boolean.parseBoolean(batchToolFlag)) {
                    log.info("##############################");
                    log.info("#  Catia Batch Tool Running  #");
                    log.info("##############################");
                    batchToolRunner.run();
                    File lockFile = new File(lockFileDirectory);
                    lockFile.delete();

                }
            } catch (RuntimeException e) {
                log.error(e);
                File lockFile = new File(lockFileDirectory);
                lockFile.delete();
            }

            return false;
        }
    }

    public boolean checkingLockFile(File directory) throws IOException {
        File[] directoryFiles = serviceConsumer.takingFileList(directory);
        log.info("Directory is : '" + directory + "'");

        if (Optional.ofNullable(directoryFiles).isEmpty()) {
            for (File file : directoryFiles) {
                if (file.toString().contains(".lock")) {
                    return true;
                }
            }
        }

        return false;

    }


}
