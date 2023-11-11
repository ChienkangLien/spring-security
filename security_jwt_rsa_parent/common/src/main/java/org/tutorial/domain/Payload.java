package org.tutorial.domain;

import lombok.Data;

import java.util.Date;

/**
 * 為了方便獲取token中的用戶信息，將token中載荷部分單獨封裝成一個對象
 */
@Data
public class Payload<T> {
    private String id;
    private T userInfo;
    private Date expiration;
}
