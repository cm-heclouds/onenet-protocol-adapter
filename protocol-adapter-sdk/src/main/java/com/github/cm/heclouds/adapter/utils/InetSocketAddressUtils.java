package com.github.cm.heclouds.adapter.utils;

import com.github.cm.heclouds.adapter.exceptions.IllegalConfigException;

import java.net.InetSocketAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * InetSocketAddress工具类
 */
public class InetSocketAddressUtils {

    private static final Pattern IPV4_PATTERN =
            Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+)\\:(\\d+)");
    private static final Pattern IPV6_PATTERN =
            Pattern.compile(
                    "(\\[((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\\\d|1\\\\d\\\\d|[1-9]?\\\\d)(\\\\.(25[0-5]|2[0-4]\\\\d|1\\\\d\\\\d|[1-9]?\\\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\\\d|1\\\\d\\\\d|[1-9]?\\\\d)(\\\\.(25[0-5]|2[0-4]\\\\d|1\\\\d\\\\d|[1-9]?\\\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\\\d|1\\\\d\\\\d|[1-9]?\\\\d)(\\\\.(25[0-5]|2[0-4]\\\\d|1\\\\d\\\\d|[1-9]?\\\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\\\d|1\\\\d\\\\d|[1-9]?\\\\d)(\\\\.(25[0-5]|2[0-4]\\\\d|1\\\\d\\\\d|[1-9]?\\\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\\\d|1\\\\d\\\\d|[1-9]?\\\\d)(\\\\.(25[0-5]|2[0-4]\\\\d|1\\\\d\\\\d|[1-9]?\\\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\\\d|1\\\\d\\\\d|[1-9]?\\\\d)(\\\\.(25[0-5]|2[0-4]\\\\d|1\\\\d\\\\d|[1-9]?\\\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\\\d|1\\\\d\\\\d|[1-9]?\\\\d)(\\\\.(25[0-5]|2[0-4]\\\\d|1\\\\d\\\\d|[1-9]?\\\\d)){3}))|:)))(%.+)?])\\:(\\d+)"
            );


    private InetSocketAddressUtils() {
    }

    /**
     * 获取ConnectionHost
     *
     * @param connectionHost connectionHost
     * @return InetSocketAddress
     */
    public static InetSocketAddress getConnectionHost(String connectionHost) {
        String ip = null;
        Integer port = null;
        try {
            String connection = connectionHost
                    .replaceAll("tcp:", "")
                    .replaceAll("ssl:", "")
                    .replaceAll("/", "");

            Matcher m = IPV4_PATTERN.matcher(connection);
            if (m.matches()) {
                ip = m.group(1);
                port = Integer.parseInt(m.group(2));
            }

            m = IPV6_PATTERN.matcher(connection);
            if (m.matches()) {
                ip = m.group(1);
                port = Integer.parseInt(m.group(m.groupCount()));
            }
        } catch (Exception e) {
            throw new IllegalConfigException("illegal connection host:" + connectionHost);
        }
        if (ip == null) {
            throw new IllegalConfigException("illegal connection host:" + connectionHost);
        }
        return new InetSocketAddress(ip, port);
    }


}
