package com.best.hello.controller.SQLI;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.best.hello.entity.Users;
import com.best.hello.mapper.UserMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api("SQL注入 - MyBatis-Plus")
@Slf4j
@RestController
@RequestMapping("/SQLI/MyBatisPlus")
public class MyBatisPlus {

    @Autowired
    private UserMapper userMapper;

    @ApiOperation(value = "vul：Mybatis-Plus使用apply查询")
    @GetMapping("/vul/apply")
    public List<Users> queryApply(@RequestParam("user") String user) {
        QueryWrapper<Users> wrapper = new QueryWrapper<>();
        wrapper.apply("user = '" + user + "'");
        return userMapper.selectList(wrapper);
    }

    @ApiOperation(value = "vul：Mybatis-Plus使用last")
    @GetMapping("/vul/last")
    public List<Users> queryLast(@RequestParam("column") String column) {
        QueryWrapper<Users> wrapper = new QueryWrapper<>();
        wrapper.last("order by " + column);
        return userMapper.selectList(wrapper);
    }

    @ApiOperation(value = "vul：Mybatis-Plus使用exists")
    @GetMapping("/vul/exists")
    public List<Users> queryExists(@RequestParam("user") String user) {
        QueryWrapper<Users> wrapper = new QueryWrapper<>();
        wrapper.exists("select user from users where user = '" + user + "'");
        return userMapper.selectList(wrapper);
    }

    @ApiOperation(value = "vul：Mybatis-Plus使用having")
    @GetMapping("/vul/having")
    public List<Users> queryHaving(@RequestParam("id") String id) {
        QueryWrapper<Users> wrapper = new QueryWrapper<>();
        wrapper.select().groupBy("id").having("id>" + id);
        return userMapper.selectList(wrapper);
    }

    @ApiOperation(value = "vul：Mybatis-Plus使用order by")
    @GetMapping("/vul/orderBy")
    public List<Users> queryOrderBy(@RequestParam("column") String column) {
        QueryWrapper<Users> wrapper = new QueryWrapper<>();
        wrapper.select().orderBy(true,true,column);
        return userMapper.selectList(wrapper);
    }

    @ApiOperation(value = "vul：Mybatis-Plus使用group by")
    @GetMapping("/vul/groupBy")
    public List<Users> queryGroupBy(@RequestParam("column") String column) {
        QueryWrapper<Users> wrapper = new QueryWrapper<>();
        wrapper.select().groupBy(column);
        return userMapper.selectList(wrapper);
    }

    @ApiOperation(value = "vul：Mybatis-Plus使用insql/notinsql")
    @GetMapping("/vul/insql")
    public List<Users> queryInsql(@RequestParam("column") String column,@RequestParam("id") String id) {
        QueryWrapper<Users> wrapper = new QueryWrapper<>();
        wrapper.select().inSql(column,"select user from users where id >" + id);
        return userMapper.selectList(wrapper);
    }


    // -----------------------------------安全分割线-----------------------------------
    @ApiOperation(value = "safe：Mybatis-Plus使用apply查询")
    @GetMapping("/safe/apply")
    public List<Users> queryApplySafe(@RequestParam("user") String user) {
        QueryWrapper<Users> wrapper = new QueryWrapper<>();
        wrapper.apply("user={0}", user);
        return userMapper.selectList(wrapper);
    }

    @ApiOperation(value = "safe：Mybatis-Plus使用last")
    @GetMapping("/safe/last")
    public List<Users> queryLastSafe(@RequestParam("column") String column) {
        QueryWrapper<Users> wrapper = new QueryWrapper<>();
        if (column.equals("id")) {
            wrapper.last("order by " + column);
        } else {
            wrapper.last("order by " + "user");
        }
        return userMapper.selectList(wrapper);
    }

    @ApiOperation(value = "safe：Mybatis-Plus使用exists")
    @GetMapping("/safe/exists")
    public List<Users> queryExistsSafe(@RequestParam("id") Integer id) {
        QueryWrapper<Users> wrapper = new QueryWrapper<>();
        wrapper.exists("select user from users where id > " + id);
        return userMapper.selectList(wrapper);
    }

//    @GetMapping("/safe/exists")
//    public List<Users> queryExistsSafe(@RequestParam("user") String user) {
//        QueryWrapper<Users> wrapper = new QueryWrapper<>();
//        user = checkSQLi(user);
//        wrapper.exists("select user from users where user = '" + user + "'");
//        return userMapper.selectList(wrapper);
//    }

    @ApiOperation(value = "safe：Mybatis-Plus使用having")
    @GetMapping("/safe/having")
    public List<Users> queryHavingSafe(@RequestParam("id") String id) {
        QueryWrapper<Users> wrapper = new QueryWrapper<>();
        wrapper.select().groupBy("id").having("id>{0}", id);
        return userMapper.selectList(wrapper);
    }

    @ApiOperation(value = "safe：Mybatis-Plus使用order by")
    @GetMapping("/safe/orderBy")
    public List<Users> queryOrderBySafe(@RequestParam("column") String column) {
        QueryWrapper<Users> wrapper = new QueryWrapper<>();
        if (column.equals("user")){
            wrapper.select().orderBy(true,true,"user");
        } else if (column.equals("pass")) {
            wrapper.select().orderBy(true,true,"pass");
        }else {
            wrapper.select().orderBy(true,true,"id");
        }
        return userMapper.selectList(wrapper);
    }

    @ApiOperation(value = "safe：Mybatis-Plus使用group by")
    @GetMapping("/safe/groupBy")
    public List<Users> queryGroupBySafe(@RequestParam("column") String column) {
        QueryWrapper<Users> wrapper = new QueryWrapper<>();
        if (column.equals("user")){
            wrapper.select().groupBy(column);
        } else if (column.equals("pass")) {
            wrapper.select().groupBy(column);
        }else {
            wrapper.select().groupBy("id");
        }
        return userMapper.selectList(wrapper);
    }

    @ApiOperation(value = "safe：Mybatis-Plus使用insql/notinsql")
    @GetMapping("/safe/insql")
    public List<Users> queryInsqlSafe(@RequestParam("column") String column,@RequestParam("id") Integer id) {
        QueryWrapper<Users> wrapper = new QueryWrapper<>();
        if (column.equals("pass")){
            wrapper.select().inSql(column,"select pass from users where id >" + id);
        }else {
            wrapper.select().inSql("user","select user from users where id >" + id);
        }
        return userMapper.selectList(wrapper);
    }
}
