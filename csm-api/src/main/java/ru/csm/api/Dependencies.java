package ru.csm.api;

public final class Dependencies {

    public static final Dependency DBCP;
    public static final Dependency H2;

    private Dependencies(){}

    static {
        DBCP = new Dependency("commons-dbcp2-2.8.0.jar", "https://repo1.maven.org/maven2/org/apache/commons/commons-dbcp2/2.8.0/commons-dbcp2-2.8.0.jar");
        H2 = new Dependency("h2-1.4.200.jar", "https://repo1.maven.org/maven2/com/h2database/h2/1.4.200/h2-1.4.200.jar");
    }
}
