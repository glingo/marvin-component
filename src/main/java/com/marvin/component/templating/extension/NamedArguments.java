package com.marvin.component.templating.extension;

import java.util.List;

@FunctionalInterface
public interface NamedArguments {

    List<String> getArgumentNames();
}