package com.example.demo.annotation;

import com.example.demo.annotation.route.RouteServer;
import com.example.demo.BeanTool;
import com.example.demo.message.GDM;
import io.netty.channel.ChannelHandlerContext;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class RouteExecution  {
    private static final Map<String, String> ENUMTOCLASSMAP = new HashMap<>();
    private static  String HANDLERPATH ="";
    static {
        try {
            // 使用ClassLoader来加载资源
            InputStream inputStream = RouteExecution.class.getClassLoader().getResourceAsStream("route.xml");
            if (inputStream == null) {
                throw new RuntimeException("Resource not found: route.xml");
            }
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputStream);
            doc.getDocumentElement().normalize();

            NodeList entries = doc.getElementsByTagName("entry");
            for (int i = 0; i < entries.getLength(); i++) {
                Element entry = (Element) entries.item(i);
                String key = entry.getAttribute("key");
                String value = entry.getTextContent().trim(); // 可能需要trim()来去除前后的空白字符
                ENUMTOCLASSMAP.put(key, value);
            }
            NodeList handlerPath = doc.getElementsByTagName("handlerPath");
            Node item = handlerPath.item(0);
            HANDLERPATH = item.getTextContent().trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getBeanAndExecute(String param, ChannelHandlerContext ctx, GDM.Request request) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(HANDLERPATH))
                .setScanners(new SubTypesScanner(false)));

        Set<Class<? extends RouteServer>> classes = reflections.getSubTypesOf(RouteServer.class);
        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(ToServer.class)) {
                ToServer toServer = clazz.getAnnotation(ToServer.class);
                // 注解参数对比
                String methodName = toServer.value();
                String orDefault = ENUMTOCLASSMAP.getOrDefault(param, null);
                if (orDefault == null) {
                    continue;
                }
                if (!methodName.equals(orDefault)) {
                    continue;
                }
                Object bean = BeanTool.getBean(clazz);
                // 查找并调用方法
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(ToMethod.class)) {
                        ToMethod annotation = method.getAnnotation(ToMethod.class);
                        if (!annotation.value().equals(param)) {
                            continue;
                        }
                        try {
                            method.setAccessible(true);
                            method.invoke(bean, ctx, request);
                            ctx.close();
                            return;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

}