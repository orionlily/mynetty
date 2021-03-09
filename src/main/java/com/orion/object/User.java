package com.orion.object;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author li.lc
 */
@Data
public class User implements Serializable {
    private Long id;
    private String name;
    private Integer age;
    private LocalDateTime birthDay;
}
