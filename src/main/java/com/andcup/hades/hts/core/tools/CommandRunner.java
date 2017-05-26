package com.andcup.hades.hts.core.tools;

import com.andcup.hades.hts.core.model.Message;
import com.andcup.hades.hts.core.model.State;
import com.andcup.hades.hts.core.model.Task;
import com.andcup.hades.hts.server.utils.LogUtils;
import com.thoughtworks.studios.javaexec.CommandExecutor;
import com.thoughtworks.studios.javaexec.CommandExecutorException;
import com.thoughtworks.studios.javaexec.LineHandler;

import java.util.Arrays;

/**
 * Created by Amos
 * Date : 2017/5/24 18:33.
 * Description:
 */
public class CommandRunner {

    int outLineCount = 0;
    Message<Task> message;
    Class<?> tag ;

    public CommandRunner(Class<?> tag, Message<Task> message){
        this.message = message;
        this.tag = tag;
    }

    public State exec(String command) {
        CommandExecutor executor = new CommandExecutor(Arrays.asList(command.split(" ")));
        try {
            executor.run(new LineHandler() {
                public void handleLine(String line) {
                    if(line.contains("RuntimeException")){
                        throw new CommandExecutorException(line);
                    }
                    if (outLineCount++ >= 10) {
                        return;
                    }
                    LogUtils.info(tag, message.getName()  + " line : " + line + " out line count = " + outLineCount);
                }
            });
            return State.SUCCESS;
        } catch (CommandExecutorException e) {
            LogUtils.info(tag, message.getName()  + " error : " + e.getCause().getMessage());
            return State.FAILED;
        }
    }
}
