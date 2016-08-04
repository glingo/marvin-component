/*******************************************************************************
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.marvin.component.templating.node.expression;

import com.marvin.component.templating.EvaluationContext;
import com.marvin.component.templating.Template;

public class NegativeTestExpression extends PositiveTestExpression {

    @Override
    public Object evaluate(Template self, EvaluationContext context) throws Exception {
        return !((Boolean) super.evaluate(self, context));
    }
}
