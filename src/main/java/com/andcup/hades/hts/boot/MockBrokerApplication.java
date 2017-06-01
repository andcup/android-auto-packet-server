package com.andcup.hades.hts.boot;

import com.andcup.hades.hts.HadesRootConfigure;
import com.andcup.hades.hts.core.MqBroker;
import com.andcup.hades.hts.server.utils.CacheClear;
import com.andcup.hades.hts.server.utils.LogUtils;

import java.io.File;

/**
 * Created by Amos
 * Date : 2017/5/5 16:23.
 * Description:
 */
public class MockBrokerApplication {

    public static void main(String[] args) {
        /**
         * 启动程序.
         * */
        MockBrokerBoot.start(args[0], args[1]);

        /**
         * 临时打包文件保留5个小时.
         * */
        new GarbageCleanerThread(HadesRootConfigure.sInstance.getApkTempDir(), 5 * 60 * 60 * 1000).start();
        /**
         * 日志文件保留5天.
         * */
        new GarbageCleanerThread(HadesRootConfigure.sInstance.getLogTempDir(), 5 * 24 * 60 * 60 * 1000).start();

        LogUtils.info(MqBroker.class, " start server ok. ");
    }


    public static class GarbageCleanerThread extends Thread {

        String dir;
        long time;

        public GarbageCleanerThread(String dir, long time) {
            this.dir = dir;
            this.time = time;
        }

        @Override
        public void run() {
            while (true) {
                //每隔10分钟遍历一次.
                try {
                    File[] files = new File(dir).listFiles();
                    for (File file : files) {
                        long modifyTime = file.isDirectory() ? getLastModifyTime(file) : file.lastModified();
                        if (System.currentTimeMillis() - modifyTime > time) {
                            //LogUtils.info(GarbageCleanerThread.class, " delete file start :  " + file.getAbsolutePath());
                            try {
                                CacheClear.delete(file);
                            } catch (Exception e) {

                            }
                            //LogUtils.info(GarbageCleanerThread.class, " delete file end :  " + file.getAbsolutePath());
                        }
                    }
                    Thread.sleep(10 * 60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {

                }
            }
        }

        public long getLastModifyTime(File dirFile) {
            long modifyTime = dirFile.lastModified();
            try {
                for (File file : dirFile.listFiles()) {
                    modifyTime = file.lastModified() > modifyTime ? file.lastModified() : modifyTime;
                }
            } catch (Exception e) {

            }
            return modifyTime;
        }
    }
}
