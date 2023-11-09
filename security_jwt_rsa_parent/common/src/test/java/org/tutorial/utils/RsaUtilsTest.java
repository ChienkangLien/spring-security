package org.tutorial.utils;

import org.junit.Test;

public class RsaUtilsTest {
	private String privateFilePath = "D:\\spring_security_practice\\key\\rsa_public";
    private String publicFilePath = "D:\\spring_security_practice\\key\\rsa_public.pub";

    @Test
    public void generateKey() throws Exception {
    	RsaUtils.generateKey(publicFilePath, privateFilePath, "saltss", 2048);
    }
    
    @Test
    public void getPublicKey() throws Exception {
        System.out.println(RsaUtils.getPublicKey(publicFilePath));
    }

    @Test
    public void getPrivateKey() throws Exception {
        System.out.println(RsaUtils.getPrivateKey(privateFilePath));
    }

}
