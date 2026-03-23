package com.example;

import java.io.StringWriter;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import freemarker.ext.jakarta.jsp.TaglibFactory;
import freemarker.template.Configuration;
import jakarta.servlet.ServletContext;
import org.junit.jupiter.api.Test;


class JspTaglibJsp4ReproducerTest {

    @Test
    void jspTaglibs_fails_with_jsp4() throws Exception {
        var cfg = new Configuration(Configuration.VERSION_2_3_33);
        cfg.setClassForTemplateLoading(getClass(), "/");

        var ctx = (ServletContext) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{ServletContext.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getClassLoader" -> getClass().getClassLoader();
                    default -> null;
                });

        var taglibFactory = new TaglibFactory(ctx);
        taglibFactory.setMetaInfTldSources(List.of(
                new TaglibFactory.ClasspathMetaInfTldSource(Pattern.compile(".*"))));

        var template = cfg.getTemplate("test.ftl");
        try {
            template.process(Map.of("JspTaglibs", taglibFactory), new StringWriter());
        } catch (Exception | NoClassDefFoundError e) {
            System.err.println(e.getCause());
        }
    }
}
