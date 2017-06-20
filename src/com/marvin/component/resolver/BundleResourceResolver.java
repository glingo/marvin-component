package com.marvin.component.resolver;

import com.marvin.component.io.IResource;
import com.marvin.component.io.loader.ResourceLoader;
import com.marvin.component.kernel.Kernel;
import com.marvin.component.kernel.bundle.Bundle;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public abstract class BundleResourceResolver<O> extends PathResolver<O> {
    
    protected final Kernel kernel;
    private final ResourceLoader loader;
    
    private HashMap<String, String> cache = new HashMap<>();
    
    public BundleResourceResolver(Kernel kernel, ResourceLoader loader, String prefix, String sufix) {
        super(prefix, sufix);
        this.kernel = kernel;
        this.loader = loader;
    }

    @Override
    protected String resolvePath(String path) throws IOException {
        if (cache.containsKey(path)) {
            return cache.get(path);
        }
        
        String concat = getPrefix().concat(path).concat(getSufix());
        
        IResource load = this.loader.load(this.kernel.getRootDir().concat(concat));
        
        if (load.exists()) {
            cache.put(path, load.getFile().getPath());
            return cache.get(path);
        }
        
        Optional<IResource> optional = this.kernel.getBundles().values()
            .stream()
            .map(Bundle::getPath)
            .map((ns) -> ns.concat(concat))
            .filter(Objects::nonNull)
            .map((ns) -> {
                System.out.println(ns);
                return ns;
            })
            .map(this.loader::load)
            .filter(IResource::exists)
            .findFirst();
        
        if (optional.isPresent()) {
            cache.put(path, optional.get().getFile().getPath());
        }
        
        return cache.getOrDefault(path, null);
    }

    @Override
    public O doResolve(String path) throws Exception {
        if (Objects.isNull(path)) {
            return null;
        }
        
        return create(path);
    }
    
    protected abstract O create(String name);
}