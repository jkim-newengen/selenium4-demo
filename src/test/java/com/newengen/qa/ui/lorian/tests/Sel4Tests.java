package com.newengen.qa.ui.lorian.tests;

import static java.lang.String.format;
import static java.util.Optional.empty;
import static org.openqa.selenium.devtools.v89.network.Network.getResponseBody;
import static org.openqa.selenium.devtools.v89.network.Network.responseReceived;

import com.newengen.qa.ui.lorian.base.BaseTests;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.devtools.v89.log.Log;
import org.openqa.selenium.devtools.v89.network.Network;
import org.openqa.selenium.devtools.v89.network.Network.GetResponseBodyResponse;
import org.openqa.selenium.devtools.v89.network.model.RequestId;

public class Sel4Tests extends BaseTests {
    String testUrl = "https://platform.byjove.com/37/keywords/start";
    String testUrl2 = "https://platform.byjove.com/37/metrics";

    // todo: replace with yours
    String userName = "FIX_ME@newengen.com";
    String password = "FIX_ME";

    StringBuilder sb = new StringBuilder();

    public Sel4Tests() {
        super();
    }

    @Test
    void getHttpRequest() {
        start(userName, password);
        devTools.createSession();
        devTools.send(Network.enable(empty(), empty(), empty()));
        devTools.addListener(Network.requestWillBeSent(),
            entry -> {
                sb.append(format("[%s] ", entry.getTimestamp()));
                sb.append("Request URI : " + entry.getRequest().getUrl()+"\n");
                sb.append(" With method : "+ entry.getRequest().getMethod() + "\n");
            });

        driver.navigate().to(testUrl);
        String res = sb.toString();
        System.out.println(res);
        devTools.send(Network.disable());
        System.out.println();
    }

    @Test
    public void getHttpResponse() {
        final RequestId[] requestIds = new RequestId[1];
        devTools.createSession();
        devTools.send(
            Network.enable(empty(),
                empty(),
                empty()));

        devTools.addListener(responseReceived(), responseReceived -> {
            try {
                requestIds[0] = responseReceived.getRequestId();
                sb.append(format("[resp id = %s] url: %s, header: %s\n",
                    requestIds[0].toJson(),
                    responseReceived.getResponse().getUrl(),
                    responseReceived.getResponse().getHeaders().toJson()
                    ));
            } catch (Exception e) {
                System.out.println(e);
            }
        });

        driver.navigate().to(testUrl);
        GetResponseBodyResponse responseBody = devTools.send(getResponseBody(requestIds[0]));
        System.out.println(responseBody.getBody());
        String res = sb.toString();
        System.out.println(res);
        devTools.send(Network.disable());
    }


    @Test
    void getConsoleLog() {
        devTools.createSession();

        devTools.send(Log.enable());
        devTools.addListener(Log.entryAdded(),
            logEntry -> {
                sb.append("log: "+logEntry.getText() + "\n");
                sb.append("level: "+logEntry.getLevel() + "\n");
            });

        driver.navigate().to(testUrl2);
        String res = sb.toString();
        System.out.println(res);

        devTools.send(Log.disable());
    }


    @Test
    void cdpCommands() {
        Map<String, Object> res = new HashMap<>();
        try {
            driver.executeCdpCommand("DOM.enable", new HashMap<>());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        res.entrySet().stream().forEach(entry->{
            sb.append(format("%s:%s\n", entry.getKey(), entry.getValue()));
        });

        sb.append("================");
        driver.executeCdpCommand("DOM.disable", new HashMap<>());
    }
}
