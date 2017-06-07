package com.andcup.hades.httpserver;

import com.andcup.hades.httpserver.bind.Body;
import com.andcup.hades.httpserver.bind.Var;
import com.andcup.hades.httpserver.utils.IOUtils;
import com.andcup.hades.httpserver.utils.JsonConvertTool;
import com.andcup.hades.httpserver.utils.LogUtils;
import com.fasterxml.jackson.databind.JavaType;
import com.sun.net.httpserver.HttpExchange;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Amos
 * Date : 2017/5/15 18:10.
 * Description:
 */
interface RequestParamAdapter {


    List<Object> adapter(RequestInvoker invoker, HttpExchange httpExchange) throws Exception;

    RequestParamAdapter PARAM = new RequestParamAdapter() {
        public List<Object> adapter(RequestInvoker invoker, HttpExchange httpExchange) throws Exception{
            List<Object> listValue = new ArrayList<Object>();
            try {
                Map<String, String>  params = RequestParamsParser.parseUrlParams(httpExchange);
                Class<?>[] parameters = invoker.method.getParameterTypes();
                Annotation[][] annotations = invoker.method.getParameterAnnotations();
                for(int i=0; i< parameters.length; i++){
                    Var var = (Var) annotations[i][0];
                    if( null == var){
                        continue;
                    }
                    ParamFiller.fill(listValue, parameters[i], params.get(var.value()));
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return listValue;
        }
    };

    RequestParamAdapter BODY_APP_JSON = new RequestParamAdapter() {
        public List<Object> adapter(RequestInvoker invoker, HttpExchange httpExchange) throws Exception {
            List<Object> listValue = new ArrayList<Object>();
            Class<?>[] parameters = invoker.method.getParameterTypes();
            Annotation[][] annotations = invoker.method.getParameterAnnotations();
            for(int i=0; i< parameters.length; i++){
                Object object;
                Class clazz = parameters[i];
                if(clazz.isAssignableFrom(ArrayList.class) ||
                        parameters[i].getClass().isAssignableFrom(List.class)){
                    Body body = (Body) annotations[i][0];
                    JavaType type = JsonConvertTool.getCollectionType(ArrayList.class, body.value());
                    String bodyValue = IOUtils.convertStreamToString(httpExchange.getRequestBody());
                    LogUtils.info(RequestParamAdapter.class, bodyValue);
                    object = JsonConvertTool.toJson(bodyValue, type);
                }else{
                    object = JsonConvertTool.toJson(IOUtils.convertStreamToString(httpExchange.getRequestBody()), clazz);
                }
                if( null != object){
                    listValue.add(object);
                }

            }
            return listValue;
        }
    };

    RequestParamAdapter XWWW = new RequestParamAdapter() {
        public List<Object> adapter(RequestInvoker invoker, HttpExchange httpExchange) throws Exception{
            List<Object> listValue = new ArrayList<Object>();
            try {
                Map<String, String>  params = RequestParamsParser.parseXWWWFormUrlEncoded(httpExchange);
                Class<?>[] parameters = invoker.method.getParameterTypes();
                Annotation[][] annotations = invoker.method.getParameterAnnotations();
                for(int i=0; i< parameters.length; i++){
                    Var var = (Var) annotations[i][0];
                    if( null != var){
                        LogUtils.info(RequestParamAdapter.class, "key = " + var.value() + " value = " + params.get(var.value()));
                        ParamFiller.fill(listValue, parameters[i], params.get(var.value()));
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return listValue;
        }
    };

    /**
     * 参数填充.
     * */
    class ParamFiller{
        public static void fill(List<Object> list, Class<?> parameter, String value) {
            if("java.lang.String".equals(parameter.getName())){
                list.add(value);
            }else if("java.lang.Character".equals(parameter.getName())){
                char[] ch = (value).toCharArray();
                list.add(ch[0]);
            }else if("char".equals(parameter.getName())){
                char[] ch = (value).toCharArray();
                list.add(ch[0]);
            }else if("java.lang.Double".equals(parameter.getName())){
                list.add(Double.parseDouble( value));
            }else if("double".equals(parameter.getName())){
                list.add(Double.parseDouble(value));
            }else if("java.lang.Integer".equals(parameter.getName())){
                list.add(Integer.parseInt(  value));
            }else if("int".equals(parameter.getName())){
                list.add(Integer.parseInt(  value));
            }else if("java.lang.Long".equals(parameter.getName())){
                list.add(Long.parseLong(  value));
            }else if("long".equals(parameter.getName())){
                list.add(Long.parseLong(  value));
            }else if("java.lang.Float".equals(parameter.getName())){
                list.add(Float.parseFloat(  value));
            }else if("float".equals(parameter.getName())){
                list.add(Float.parseFloat(  value));
            }else if("java.lang.Short".equals(parameter.getName())){
                list.add(Short.parseShort(  value));
            }else if("shrot".equals(parameter.getName())){
                list.add(Short.parseShort(  value));
            }else if("java.lang.Byte".equals(parameter.getName())){
                list.add(Byte.parseByte(  value));
            }else if("byte".equals(parameter.getName())){
                list.add(Byte.parseByte(  value));
            }else if("java.lang.Boolean".equals(parameter.getName())){
                if("false".equals(value) || "0".equals(value)){
                    list.add(false);
                }else if("true".equals(value) || "1".equals(value)){
                    list.add(true);
                }
            }else if("boolean".equals(parameter.getName())){
                if("false".equals(value) || "0".equals(value)){
                    list.add(false);
                }else if("true".equals(value) || "1".equals(value)){
                    list.add(true);
                }
            }
        }
    }
}