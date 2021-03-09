package com.orion.hessian;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author li.lc
 */
@Data
public class Miracle implements Serializable {
    private Long id;
    private String name;
    private Integer age;
    //hessian不支持jdk8的localdatetime等新日期类型
    private Date birthDay;
}
