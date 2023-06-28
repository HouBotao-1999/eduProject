package com.atguigu.eduservice.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.atguigu.eduservice.entity.EduSubject;
import com.atguigu.eduservice.entity.excel.SubjectData;
import com.atguigu.eduservice.service.EduSubjectService;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

public class SubjectExcelListener extends AnalysisEventListener<SubjectData> {

    public EduSubjectService subjectService;
    //不能教给spring管理，因此不能autowied注入，只能手动加入subjectService
    public SubjectExcelListener() {}
    public SubjectExcelListener(EduSubjectService subjectService) {
        this.subjectService = subjectService;
    }

    //读取excel里面的内容，一行一行的加到数据库中
    @Override
    public void invoke(SubjectData subjectData, AnalysisContext analysisContext) {
        if(subjectData == null) {
            throw new GuliException(20001,"excel文件数据为空");
        }
        //一行一行读，每次俩数据，一个一级，一个二级分类
        //判断一级分类是否重复
        EduSubject exsitOneSubject = this.exsitOneSubject(subjectService, subjectData.getOneSubjectName());
        if(exsitOneSubject == null){
            exsitOneSubject = new EduSubject();
            exsitOneSubject.setParentId("0");
            exsitOneSubject.setTitle(subjectData.getOneSubjectName());
            subjectService.save(exsitOneSubject);
        }
        String pid = exsitOneSubject.getId();
        //判断二级分类是否重复
        EduSubject exsitTwoSubject = this.exsitTwoSubject(subjectService, subjectData.getTwoSubjectName(), pid);
        if(exsitTwoSubject == null){
            exsitTwoSubject = new EduSubject();
            exsitTwoSubject.setTitle(subjectData.getTwoSubjectName());
            exsitTwoSubject.setParentId(pid);
            subjectService.save(exsitTwoSubject);
        }
    }

    //判断一级分类上是否重复
    private EduSubject exsitOneSubject(EduSubjectService eduSubjectService,String name){
        QueryWrapper<EduSubject> wrapper = new QueryWrapper<>();
        wrapper.eq("title",name);
        wrapper.eq("parent_id","0");
        EduSubject oneSubject = subjectService.getOne(wrapper);
        return oneSubject;
    }
    //判断二级分类
    private EduSubject exsitTwoSubject(EduSubjectService eduSubjectService,String name,String pid){
        QueryWrapper<EduSubject> wrapper = new QueryWrapper<>();
        wrapper.eq("title",name);
        wrapper.eq("parent_id",pid);
        EduSubject twoSubject = subjectService.getOne(wrapper);
        return twoSubject;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
