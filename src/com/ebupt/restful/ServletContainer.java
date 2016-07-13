package com.ebupt.restful;

public interface ServletContainer {
    public void initContainer();

    public ServletHandler getServlet(String uri);

    public String[] getParamsInUri(String uri);
}
