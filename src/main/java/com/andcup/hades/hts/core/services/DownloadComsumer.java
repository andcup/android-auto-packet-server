package com.andcup.hades.hts.core.services;

import com.andcup.hades.hts.HadesRootConfig;
import com.andcup.hades.hts.core.MqConsumer;
import com.andcup.hades.hts.core.annotation.Consumer;
import com.andcup.hades.hts.core.exception.ConsumeException;
import com.andcup.hades.hts.core.model.Task;
import com.andcup.hades.hts.core.model.Message;
import com.andcup.hades.hts.core.model.Topic;
import com.andcup.hades.hts.core.transfer.FtpTransfer;
import com.andcup.hades.hts.core.transfer.Transfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Amos
 * Date : 2017/5/5 17:31.
 * Description:
 */

@Consumer(topic = Topic.DOWNLOADING, bind = Topic.COMPRESS)
public class DownloadComsumer extends MqConsumer {

    final Logger logger = LoggerFactory.getLogger(DownloadComsumer.class.getName());

    Message<Task>   message;
    Transfer        transfer;

    public DownloadComsumer(){
        transfer = new FtpTransfer(HadesRootConfig.sInstance.remote.ftp);
    }

    public Message.State execute(Message<Task> message) {
        this.message = message;
        try{
            transfer.dlFromRemote(message.getData().sourcePath, Task.Helper.getApkPath(message.getData()));
        }catch (ConsumeException e){
            logger.error(e.getMessage());
            return Message.State.FAILED;
        }
        return Message.State.SUCCESS;
    }

    public void abort(Message<Task> message) {
        if( null != transfer){
            transfer.abort();
        }
    }
}
