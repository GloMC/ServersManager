package net.glomc.apis.serversmanagers.common.utils.ip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class PublicIPChecker {

    private static final URL ipCheckingUrl;

    static {
        try {
            ipCheckingUrl = new URL("http://checkip.amazonaws.com");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getPublicAddress() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(ipCheckingUrl.openStream()));
        String ip = in.readLine();
        in.close();
        return ip;
    }


}
