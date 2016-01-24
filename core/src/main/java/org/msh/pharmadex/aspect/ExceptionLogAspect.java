package org.msh.pharmadex.aspect;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Created by utkarsh on 1/14/16.
 */
@Aspect
@Component
public class ExceptionLogAspect {

    @Before("execution(* org.msh.pharmadex.service.ApplicantService.findRegProductForApplicant(..))")
    public void beforeExecution(){
        System.out.println("Executing before method");
    }

//    @Before("beforeExecution()")//applying pointcut on before advice
//    public void myadvice(JoinPoint jp)//it is advice (before advice)
//    {
//        System.out.println("additional concern");
//        //System.out.println("Method Signature: "  + jp.getSignature());
//    }

    @After("execution(* org.msh.pharmadex.service.ApplicantService.findRegProductForApplicant(..))")
    public void afterExecution(JoinPoint joinPoint){
        System.out.println("Executing after method-----"+joinPoint.getSignature().getName());
    }


}
