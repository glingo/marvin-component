/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.marvin.component.templating.extension.core;

import com.marvin.component.templating.extension.Filter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Sort list items in the reverse order
 * 
 * @author Barakat Soror
 *
 */
public class RsortFilter implements Filter {

    @Override
    public List<String> getArgumentNames() {
        return null;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public List<Comparable> apply(Object input, Map<String, Object> args) {
        if (input == null) {
            return null;
        }
        List<Comparable> collection = (List<Comparable>) input;
        Collections.sort(collection, Collections.reverseOrder());
        return collection;
    }

}
