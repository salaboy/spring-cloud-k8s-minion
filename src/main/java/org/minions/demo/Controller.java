package org.minions.demo;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RefreshScope
public class Controller {

    private static final Log log = LogFactory.getLog(Controller.class);
    private final String version = "0.1";

    private MinionsLibrary minionsLibrary;

    @Autowired
    private MinionConfig minionConfig;

    public Controller(MinionsLibrary minionsLibrary) {
        this.minionsLibrary = minionsLibrary;
    }

    @RequestMapping(method = GET)
    public String minion() throws UnknownHostException, UnsupportedEncodingException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Host: ").append(InetAddress.getLocalHost().getHostName()).append("<br/>");
        stringBuilder.append("Minion Type: ").append(minionConfig.getType()).append("<br/>");
        stringBuilder.append("IP: ").append(InetAddress.getLocalHost().getHostAddress()).append("<br/>");
        stringBuilder.append("Version: ").append(version).append("<br/>");
        String minion = minionsLibrary.getMinion(minionConfig.getType());
        if (minion != null && !minion.isEmpty()) {
            stringBuilder.append(minion);
        } else {
            stringBuilder.append(" - No Art for this type (" + minionConfig.getType() + ") of minion - ");
        }
        return stringBuilder.toString();
    }
}
