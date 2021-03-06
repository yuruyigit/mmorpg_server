package com.qworldr.mmorpg.dispatcher;

import com.google.common.collect.Lists;
import com.qworldr.mmorpg.annotation.Protocal;
import com.qworldr.mmorpg.exception.PrivilegeException;
import com.qworldr.mmorpg.bean.IdentityProvider;
import com.qworldr.mmorpg.session.Session;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class InvokerDefinition {
    private static String identifyClass;
    private static IdentityProvider provide;
    private Object instance;
    private Method method;

    public InvokerDefinition(Object instance, Method method) {
        this.instance = instance;
        this.method = method;
    }

    public Object invoke(Session session, Object protocal) throws Exception {
        Class<?>[] parameterTypes = method.getParameterTypes();
        List<Object> params = Lists.newArrayList();
        for (Class type : parameterTypes) {
            if (type.newInstance() instanceof Session) {
                params.add(session);
            } else if (type.getAnnotation(Protocal.class) != null) {
                params.add(protocal);
            } else if (type.getName().equals(identifyClass)) {
                Object id = session.getId();
                if (id == null) {
                    throw new PrivilegeException(String.format("%s 参数%s需要seesion提供id获取，但是session.id为null", method.toString(), identifyClass));
                }
                Object object = null;
                try {
                    object = provide.getIdentity(session);
                    if(object==null){
                        throw new PrivilegeException(String.format("%s方法执行时，用户未登录，无法获取%s 参数", method.toString(), identifyClass));
                    }
                } catch (Exception e) {
                    throw e;
                }
                params.add(object);
            } else {
                throw new IllegalArgumentException("SocketRequest方法参数只能是Session子类，Protocal类或提供了IdentityProvide获取的类型");
            }
        }
        try {
            return method.invoke(this.instance, params.toArray());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setIdentifyClass(String identifyClass) {
        InvokerDefinition.identifyClass = identifyClass;
    }

    public static void setProvide(IdentityProvider provide) {
        InvokerDefinition.provide = provide;
    }
}
