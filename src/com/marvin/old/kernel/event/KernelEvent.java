/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.marvin.old.kernel.event;

import com.marvin.old.event.Event;
import com.marvin.old.kernel.Kernel;

/**
 *
 * @author Dr.Who
 */
public class KernelEvent extends Event {

    protected Kernel kernel;

    public KernelEvent(Kernel kernel) {
        this.kernel = kernel;
    }
    
}
