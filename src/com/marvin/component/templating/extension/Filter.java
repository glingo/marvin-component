/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.marvin.component.templating.extension;

import java.util.Map;

public interface Filter extends NamedArguments {

    Object apply(Object input, Map<String, Object> args);
}
