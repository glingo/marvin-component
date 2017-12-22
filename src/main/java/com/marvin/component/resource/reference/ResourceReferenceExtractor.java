package com.marvin.component.resource.reference;

import extractor.Extractor;
import java.util.function.Supplier;
import com.marvin.component.resource.reference.path.PathType;
import com.marvin.component.resource.reference.path.PathTypeSupplier;

public class ResourceReferenceExtractor implements Extractor<String, ResourceReference> {
    
    private final Supplier<PathType> pathTypeSupplier;
    private final Extractor<String, ResourceReference> posixResourceReferenceExtractor;
    private final Extractor<String, ResourceReference> uncResourceReferenceExtractor;

    public ResourceReferenceExtractor(Supplier<PathType> pathTypeSupplier, 
            Extractor<String, ResourceReference> posixResourceReferenceExtractor, 
            Extractor<String, ResourceReference> uncResourceReferenceExtractor) {
        this.pathTypeSupplier = pathTypeSupplier;
        this.posixResourceReferenceExtractor = posixResourceReferenceExtractor;
        this.uncResourceReferenceExtractor = uncResourceReferenceExtractor;
    }

    @Override
    public ResourceReference extract(String spec) {
        if (pathTypeSupplier.get() == PathType.UNC) {
            return uncResourceReferenceExtractor.extract(spec);
        }
        
        return posixResourceReferenceExtractor.extract(spec);
    }
    
    public static ResourceReferenceExtractor defaultExtractor() {
        return new ResourceReferenceExtractor(new PathTypeSupplier(), 
                new PosixResourceReferenceExtractor(), 
                new UncResourceReferenceExtractor());
    }
}