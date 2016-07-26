/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.marvin.component;

import app.service.IService;
import app.service.TestServiceA;
import app.service.TestServiceB;
import app.service.TestServiceC;
import java.util.ArrayList;

/**
 *
 * @author cdi305
 */
public abstract class Services {
    
    public static final IService serviceA = new TestServiceA("Test Service A", 0);
    public static final IService serviceB = new TestServiceB(serviceA, "Test Service B", 1);
    public static final IService serviceC = new TestServiceC(serviceA, "Test Service C", 2, new ArrayList<>());

}
