package com.yxtech.utils.i18n;



import jdk.nashorn.internal.objects.Global;
import net.sf.cglib.core.Local;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Administrator on 2015/10/17.
 */
public class I18n {
    public static String getMessage(String key)
    {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String language=(String)request.getSession().getAttribute("language");
        Locale locale;
        if (StringUtils.isBlank(language)) {
            locale= Locale.getDefault();
        }else {
            locale = new Locale(language);
        }

        ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n/messages",locale);

        if (resourceBundle.containsKey(key))
        {
            return resourceBundle.getString(key);
        }

        return key;
    }
    public static void main(String[] args) {
       String value= I18n.getMessage("qr.bookId.null");
        System.out.println(value);

    }


}
