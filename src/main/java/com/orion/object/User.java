package com.orion.object;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author li.lc
 */
@Data
public class User {
    private Long id;
    private String name;
    private Integer age;
    private LocalDateTime birthDay;
}
