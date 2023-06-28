package com.atguigu.eduservice.controller;


import com.atguigu.commonutils.R;
import com.atguigu.eduservice.entity.EduTeacher;
import com.atguigu.eduservice.entity.vo.TeacherQuery;
import com.atguigu.eduservice.service.EduTeacherService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.models.Response;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.FallbackExceptionTranslationStrategy;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.jsf.FacesContextUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 讲师 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2023-05-10
 */
//加注解方便测试
@Api(description = "讲师管理")
@RestController
@CrossOrigin
@RequestMapping(value = "/eduservice/edu-teacher")
public class EduTeacherController {

    //访问地址:http://localhost:8001//eduservice/edu-teacher/findAll
    //把service注入
    @Autowired
    private EduTeacherService teacherService;

    //1.查询讲师表所有数据
    //加注解
    @ApiOperation(value = "所有讲师列表")
    @GetMapping("findAll")
    public R findAll()
    {
        //调用service方法进行查询
        List<EduTeacher> list = teacherService.list(null);
        //返回使用的方法是链式编程
        return R.ok().data("item",list).name("平行时空先生");
    }

    //2.逻辑删除讲师
    //表示id需要通过路径进行传递
    @ApiOperation(value = "删除指定id的讲师")
    @DeleteMapping("{id}")
    public R removeTeacher(@ApiParam(name = "id",value = "讲师ID",required = true) @PathVariable String id)
    {
        boolean flag = teacherService.removeById(id);
        if(flag)
        {
            return R.ok();
        }
        else {
            return R.error();
        }
    }

    //3.分页查询讲师
    @ApiOperation(value = "分页查询讲师")
    //通过路径传递当前页和每页记录数
    @GetMapping("pageTeacher/{current}/{limit}")
    public R pageListTeacher(@PathVariable long current,
                             @PathVariable long limit){
        //创建Page对象
        Page<EduTeacher> pageTeacher = new Page<>(current,limit);
        //调用方法实现分页
        teacherService.page(pageTeacher,null);
        long total = pageTeacher.getTotal();
        List<EduTeacher> records = pageTeacher.getRecords();

        Map map = new HashMap();
        map.put("total",total);
        map.put("rows",records);

        return R.ok().data(map);
    }

    //4.多条件查询讲师
    @ApiOperation(value = "分页多条件查询讲师")
    @PostMapping("pageTeacherCondition/{current}/{limit}")
    //@RequestBody是使用json传递数据，把json数据封装到对应对象里面
    //用@RequestBody需要改成@PostMapping提交
    public R pageTeacherCondition(@PathVariable long current, @PathVariable long limit,
                                  @RequestBody(required = false) TeacherQuery teacherQuery){
        //创建page对象
        Page<EduTeacher> pageTeacher = new Page<>(current,limit);
        //构造条件
        QueryWrapper<EduTeacher> wrapper = new QueryWrapper<>();
        String name = teacherQuery.getName();
        Integer level = teacherQuery.getLevel();
        String begin = teacherQuery.getBegin();
        String end = teacherQuery.getEnd();
        //判断条件是否为空，若为空则不拼接条件
        if(!StringUtils.isEmpty(name))
        {
            wrapper.like("name",name);
        }
        if(!StringUtils.isEmpty(level))
        {
            wrapper.eq("level",level);
        }
        if(!StringUtils.isEmpty(begin))
        {
            wrapper.ge("gmt_create",begin);
        }
        if(!StringUtils.isEmpty(end))
        {
            wrapper.le("gmt_create",end);
        }
        //调用方法实现分页
        teacherService.page(pageTeacher,wrapper);
        long total = pageTeacher.getTotal();
        List<EduTeacher> records = pageTeacher.getRecords();

        Map map = new HashMap();
        map.put("total",total);
        map.put("rows",records);

        return R.ok().data(map);

    }

    //5.添加讲师
    @ApiOperation(value = "添加讲师方法")
    @PostMapping("addTeacher")
    public R addTeacher(@RequestBody EduTeacher eduTeacher){
        //自动添加方法
        boolean save = teacherService.save(eduTeacher);
        if(save){
            return R.ok();
        }
        else
            return R.error();
    }

    //6.根据id查询讲师
    @ApiOperation(value = "根据id查询讲师")
    @GetMapping("getTeacher/{id}")
    public R getTeacher(@PathVariable String id){
        EduTeacher eduTeacher = teacherService.getById(id);
        return R.ok().data("teacher",eduTeacher);
    }

    //7.按id修改讲师
    @ApiOperation(value = "按id修改讲师")
    @PostMapping("updateTeacher")
    public R updateTeacher(@RequestBody EduTeacher eduTeacher){
        boolean flag = teacherService.updateById(eduTeacher);
        if(flag){
            return R.ok();
        }
        else
            return R.error();
    }
}

