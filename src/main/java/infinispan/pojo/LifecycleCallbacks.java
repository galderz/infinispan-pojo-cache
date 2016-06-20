package infinispan.pojo;

import org.infinispan.commons.marshall.AdvancedExternalizer;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.factories.GlobalComponentRegistry;
import org.infinispan.lifecycle.AbstractModuleLifecycle;
import org.infinispan.manager.EmbeddedCacheManager;

import java.util.Map;

public class LifecycleCallbacks extends AbstractModuleLifecycle {

   @Override
   public void cacheManagerStarting(GlobalComponentRegistry gcr, GlobalConfiguration globalCfg) {
      Map<Integer, AdvancedExternalizer<?>> externalizerMap =
            globalCfg.serialization().advancedExternalizers();

      EmbeddedCacheManager cacheManager = gcr.getComponent(EmbeddedCacheManager.class);
      externalizerMap.put(2000, new PojoRefsExternalizer.A.Externalizer0(cacheManager));
      externalizerMap.put(2001, new PojoRefsExternalizer.Pojo.Externalizer0());
   }

}
