package ru.spb.kry127.yaba.io;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс на основе шаблона проектирования синглтон.
 * На всю программу нам достаточно лишь одного уникального
 * представителя, дающего доступ к переменным окружения
 */
public class EnvironmentProvider implements Environment {

    private Map<String, String> map;

    private static Environment singleton;

    private EnvironmentProvider() {
        map = new HashMap<>(System.getenv());
        singleton = null;
    }

    /**
     * @return объект-синглтон класса Environment
     */
    public static Environment getEnvironment() {
        if (singleton == null)
            singleton = new EnvironmentProvider();
        return singleton;
    }

    @Override
    public String getEnvVariable(String name) {
        String envVariable = map.get(name);
        if (envVariable != null) {
            return envVariable;
        }
        return "";
    }

    @Override
    public void setEnvVariable(String name, String value) {
        map.put(name, value);
    }

    @Override
    public Map<String, String> getFullEnvironment() {
        return map;
    }
}
