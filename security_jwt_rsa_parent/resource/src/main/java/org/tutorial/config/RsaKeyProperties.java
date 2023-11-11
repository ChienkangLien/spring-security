package org.tutorial.config;

import java.security.PublicKey;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.tutorial.utils.RsaUtils;

import lombok.Data;

@ConfigurationProperties("rsa.key")
@Data
public class RsaKeyProperties {
    private String publicKeyFile;

    private PublicKey publicKey;

    // 給Key賦值
    @PostConstruct
    public void createRsaKey() throws Exception {
        publicKey = RsaUtils.getPublicKey(publicKeyFile);
    }
}
