# security_jwt_rsa_parent
採maven module 方式建立專案，父工程<packaging>pom</packaging> 底下多個子工程

## common
自訂工具類：公私鑰轉換；私鑰加密公鑰解密

## auth_server
頒發token
config：RsaKeyProperties(讀取鑰匙)、SecurityConfig(加入過濾器)
filter：自訂過濾器JwtLoginFilter、JwtVerifyFilter

http://localhost:8081/login (POST) 登入請求
body: {
"username":"user2",
"password":"user2"
}
response head 會帶 token，Authorization : Bearer ...

## resource
只提供資源服務，根據auth_server、去除認證的動作(JwtLoginFilter、私鑰)

http://localhost:8082/ 發送資源請求
requset header 要帶token，Authorization : Bearer ...
