package com.best.hello.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * entity 实体类代码
 * @date 2021/07/23
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("`users`")
public class Users {

    private Integer id;
    private String user;
    private String pass;
}
