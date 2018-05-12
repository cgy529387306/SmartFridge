package com.mb.smartfridge.utils;

import org.json.JSONObject;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Created by Administrator on 2018\5\12 0012.
 */

public class EmptyHelper {
    /**
     * 判断对象是否为NULL或空
     * @param object 对象
     * @return 是否为NULL或空
     */
    public static boolean isEmpty (Object object){
        boolean result = false;
        if (object == null){
            result = true;
        } else {
            if (object instanceof String){
                result = ((String)object).equals("");
            }else if (object instanceof Date) {
                result = ((Date) object).getTime() == 0;
            }else if (object instanceof Long){
                result = (Long) object == Long.MIN_VALUE;
            }else if (object instanceof Integer){
                result = (Integer) object == Integer.MIN_VALUE;
            }else if (object instanceof Collection){
                result = ((Collection<?>)object).size() == 0;
            }else if (object instanceof Map){
                result = ((Map<?, ?>)object).size() == 0;
            }else if (object instanceof JSONObject){
                result = !((JSONObject)object).keys().hasNext();
            }else{
                result = object.toString().equals("");
            }
        }
        return result;
    }

    /**
     * 判断对象是否不为NULL或空
     * @param object 对象
     * @return 是否不为NULL或空
     */
    public static boolean isNotEmpty(Object object){
        return !isEmpty(object);
    }

}
