# security_jwt_rsa_parent
採maven module方式建立專案，父工程<packaging>pom</packaging> 底下多個子工程

## common
自訂工具類：公私鑰轉換；此範例採公鑰加密私鑰解密

## auth_server
頒發token
config：RsaKeyProperties(讀取鑰匙)、SecurityConfig(加入過濾器)
filter：自訂過濾器JwtLoginFilter、JwtVerifyFilter

## resource
只提供資源服務，根據auth_server、去除認證的動作(JwtLoginFilter、私鑰)
