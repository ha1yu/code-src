package com.best.hello.controller.SQLI;

import com.best.hello.entity.Users;
import com.best.hello.mapper.TkUserMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Api("SQL注入 - tkMapper")
@Slf4j
@RestController
@RequestMapping("/SQLI/tkMapper")
public class tkMapper {
    @Autowired
    TkUserMapper tkUserMapper;

    @ApiOperation(value = "vul：tkMapper使用andCondition查询")
    @GetMapping("/vul/andCondition")
    public List<Users> queryAndCondition(@RequestParam("user") String user) {
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andCondition("user=" + "'" + user + "'");
        return tkUserMapper.selectByExample(example);
    }

    @ApiOperation(value = "vul：tkMapper使用orCondition查询")
    @GetMapping("/vul/orCondition")
    public List<Users> queryOrCondition(@RequestParam("user") String user) {
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andCondition("id = 1");
        criteria.orCondition("user=" + "'" + user + "'");
        return tkUserMapper.selectByExample(example);
    }

    @ApiOperation(value = "vul：tkMapper使用setOrderByClause查询")
    @GetMapping("/vul/setOrderByClause")
    public List<Users> querySetOrderByClause(@RequestParam("sort") String sort) {
        Example example = new Example(Users.class);
        example.setOrderByClause(sort);
        return tkUserMapper.selectByExample(example);
    }

    // -----------------------------------安全分割线-----------------------------------

    @ApiOperation(value = "safe：tkMapper使用andCondition查询")
    @GetMapping("/safe/andCondition")
    public List<Users> queryAndConditionSafe(@RequestParam("user") String user) {
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andCondition("user=", user);
        return tkUserMapper.selectByExample(example);
    }

    @ApiOperation(value = "safe：tkMapper使用orCondition查询")
    @GetMapping("/safe/orCondition")
    public List<Users> queryOrConditionSafe(@RequestParam("user") String user) {
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andCondition("id = 1");
        criteria.orCondition("user=", user);
        return tkUserMapper.selectByExample(example);
    }

    @ApiOperation(value = "vul：tkMapper使用setOrderByClause查询")
    @GetMapping("/safe/setOrderByClause")
    public List<Users> querySetOrderByClauseSafe(@RequestParam("sort") String sort) {
        Example example = new Example(Users.class);
        if (sort.equals("user")){
            example.setOrderByClause("user");
        } else if (sort.equals("pass")) {
            example.setOrderByClause("pass");
        }else {
            example.setOrderByClause("id");
        }
        return tkUserMapper.selectByExample(example);
    }
}
