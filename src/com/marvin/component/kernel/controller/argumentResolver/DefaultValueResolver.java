/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.marvin.component.kernel.controller.argumentResolver;

import com.marvin.component.kernel.controller.ArgumentMetadata;
import com.marvin.component.kernel.controller.ArgumentValueResolverInterface;
import com.marvin.component.kernel.dialog.Request;

/**
 *
 * @author caill
 */
public class DefaultValueResolver implements ArgumentValueResolverInterface {

    @Override
    public boolean support(Request request, ArgumentMetadata argument) {
        return argument.hasDefaultValue();
    }

    @Override
    public Object resolve(Request request, ArgumentMetadata argument) {
        return argument.getDefaultValue();
    }
    
}
