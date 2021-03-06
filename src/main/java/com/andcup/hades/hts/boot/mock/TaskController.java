package com.andcup.hades.hts.boot.mock;

import com.andcup.hades.hts.boot.MockMqFactory;
import com.andcup.hades.hts.core.MqBroker;
import com.andcup.hades.hts.core.MqConsumer;
import com.andcup.hades.hts.core.model.Task;
import com.andcup.hades.hts.core.tools.JsonConvertTool;
import com.andcup.hades.hts.server.HadesHttpResponse;
import com.andcup.hades.hts.server.RequestController;
import com.andcup.hades.hts.server.bind.Body;
import com.andcup.hades.hts.server.bind.Controller;
import com.andcup.hades.hts.server.bind.Request;
import com.andcup.hades.hts.web.controller.cps.CpsMqFactory;
import com.andcup.hades.hts.web.controller.cps.model.ResponseEntity;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Amos
 * Date : 2017/5/19 17:14.
 * Description:
 */

@Controller("/api/task")
public class TaskController extends RequestController {

    final static org.slf4j.Logger logger = LoggerFactory.getLogger(MqConsumer.class);

    @Request(value = "/start", method = Request.Method.POST)
    public HadesHttpResponse start(@Body(Task.class) List<Task> taskList){
        logger.info(JsonConvertTool.toString(taskList));
        MqBroker.getInstance().produce(new MockMqFactory(taskList));
        return new HadesHttpResponse(ResponseEntity.SUCCESS, "commit task success.");
    }
}
