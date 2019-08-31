package com.test.aopDemo;

import java.io.Serializable;

import com.container.aop.AbsMethodAdvance;
import com.mvc.annotation.Aspect;
import com.mvc.annotation.PointCut;

/**
 * @Description  定义切点和切面， 并且继承 AbsMethodAdvance
 * @author 李福涛
 * @version 1.0  
 *
 */
@Aspect
public class TestAspect extends AbsMethodAdvance implements Serializable{

    /** 
     * 全类名_方法名 （被拦截的类_被拦截的方法）
     */
    @PointCut("com.test.aopDemo.Test.doSomeThing")
    public void testAspect() {
    	
    }

    @Override
    public void doBefore() {
        System.out.println("do before");
    }

    @Override
    public void doAfter() {
        System.out.println("do after");
    }
    
}
