package infinispan.pojo;

import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import java.util.function.BiConsumer;

public class Local {

   public static void main(String[] args) throws Exception {
      //withLocal(PojoRefsSerializable::execute);
      withLocal(PojoRefsExternalizer::execute);
   }

   private static void withLocal(BiConsumer<EmbeddedCacheManager, EmbeddedCacheManager> task) {
      try {
         EmbeddedCacheManager cm = Local.createLocalCacheManager();
         try {
            task.accept(cm, cm);
         } finally {
            if (cm != null) cm.stop();
         }
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   private static EmbeddedCacheManager createLocalCacheManager() {
      EmbeddedCacheManager cm = new DefaultCacheManager();
      cm.getCache();
      return cm;
   }

}
