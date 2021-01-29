package com.github.cm.heclouds.adapter.utils;

import com.github.cm.heclouds.adapter.api.ConfigUtils;
import com.github.cm.heclouds.adapter.config.Config;
import com.github.cm.heclouds.adapter.core.logging.ILogger;
import io.netty.util.internal.StringUtil;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.util.Base64;

import static com.github.cm.heclouds.adapter.core.logging.LoggerFormat.Action.RUNTIME;

/**
 * SasToken生成器
 */
public class SasTokenGenerator {

    private static final ILogger LOGGER = ConfigUtils.getLogger();

    private static String adapterToken = null;

    private static final String ADAPTER_VERSION = "v1.0.0";

    private static final String MAC_NAME = "HmacSHA1";

    private static final String ENCODING = "UTF-8";

    private static final String EXP = "2077721600";

    private static final String DEVICE_VERSION = "2018-10-31";

    private static final String PRODUCT = "products";

    private static final String DEVICE = "devices";

    private static final String GATEWAY = "gw";

    private static final String INSTANCE = "instance";

    private SasTokenGenerator() {
    }

    /**
     * 生成设备SasToken
     *
     * @param productId  产品ID
     * @param deviceName 设备名称
     * @param key        设备key或产品Key
     * @return 设备SasToken
     */
    public static String deviceSasToken(String productId, String deviceName, String key) {
        if (key.contains(PRODUCT + "%2F")) {
            return key;
        }
        return genSasToken(PRODUCT, productId, DEVICE, deviceName, key, DEVICE_VERSION);
    }

    /**
     * 生成适配相关SasToken
     *
     * @return 适配服务实例SasToken
     */
    public static String adapterSasToken(Config config) {
        if (adapterToken == null) {
            String serviceId = config.getServiceId();
            String instanceName = config.getInstanceName();
            String instanceKey = config.getInstanceKey();
            if (StringUtil.isNullOrEmpty(serviceId)
                    || StringUtil.isNullOrEmpty(instanceName)
                    || StringUtil.isNullOrEmpty(instanceKey)) {
                LOGGER.logInnerError(ConfigUtils.getName(), RUNTIME, serviceId, instanceName, "illegal adapter config with empty value", null);
                System.exit(0);
            }
            adapterToken = genSasToken(GATEWAY, serviceId, INSTANCE, instanceName, instanceKey, ADAPTER_VERSION);
        }
        return adapterToken;
    }

    /**
     * 生成SasToken
     *
     * @param serviceType  服务类型  gw或product
     * @param serviceId    服务ID
     * @param instanceType 实例类型  instance或devices
     * @param instanceName 实例名称
     * @param instanceKey  实例Key
     * @param version      版本
     * @return SasToken
     */
    private static String genSasToken(String serviceType, String serviceId, String instanceType, String instanceName, String instanceKey, String version) {
        try {
            String res = serviceType
                    + "/"
                    + serviceId
                    + "/"
                    + instanceType
                    + "/"
                    + instanceName;
            String encryptTxt = EXP +
                    "\n" +
                    "sha1" +
                    "\n" +
                    res +
                    "\n" +
                    version;
            String sign = HmacSHA1Encrypt(encryptTxt, instanceKey);
            String urlSign = URLEncoder.encode(sign, "UTF-8");
            return "et=" +
                    EXP +
                    "&method=sha1&res=" +
                    URLEncoder.encode(res, "utf-8") +
                    "&version=" +
                    version +
                    "&sign=" +
                    urlSign;
        } catch (Exception e) {
            LOGGER.logInnerError(ConfigUtils.getName(), RUNTIME, "generate sasToken failed", e);
            return null;
        }
    }

    /**
     * SHA1编码
     *
     * @param encryptText 编码文本
     * @param encryptKey  编码Key
     * @return SHA1编码
     * @throws Exception Exception
     */
    private static String HmacSHA1Encrypt(String encryptText, String encryptKey) throws Exception {
        byte[] key = Base64.getDecoder().decode(encryptKey);
        //根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKey secretKey = new SecretKeySpec(key, MAC_NAME);
        //生成一个指定 Mac 算法 的 Mac 对象
        Mac mac = Mac.getInstance(MAC_NAME);
        //用给定密钥初始化 Mac 对象
        mac.init(secretKey);
        byte[] text = encryptText.getBytes(ENCODING);
        //完成 Mac 操作
        return Base64.getEncoder().encodeToString(mac.doFinal(text));
    }
}
