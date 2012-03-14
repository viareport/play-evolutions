package play.modules.evolutions.multitenant;

import multitenant.context.MultiTenantContext;
import multitenant.context.MultiTenantContextFactory;


public class StaticContextFactory extends MultiTenantContextFactory {

    @Override
    public MultiTenantContext createCtx() {
        return new StaticContext();
    }

}
