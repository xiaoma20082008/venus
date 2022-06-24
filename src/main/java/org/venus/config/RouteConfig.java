package org.venus.config;

import java.io.Serializable;
import java.util.Date;

public class RouteConfig {

}

class RouteInfo implements Serializable {

    private static final long serialVersionUID = -2177134127693954203L;

    private long id;
    private String name;
    private String cluster;
    private String group;
    private String source;
    private String target;
    private int prefix;
    private String purpose;
    private String type;
    private int state;
    private String username;
    private Date updateTime;
}
