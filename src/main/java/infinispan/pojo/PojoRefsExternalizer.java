package infinispan.pojo;

import org.infinispan.Cache;
import org.infinispan.commons.marshall.AbstractExternalizer;
import org.infinispan.commons.util.Util;
import org.infinispan.manager.EmbeddedCacheManager;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Set;

public class PojoRefsExternalizer {

   public static void execute(EmbeddedCacheManager cm1, EmbeddedCacheManager cm2) {
      A a = new A(11, "a name");
      Pojo p1 = new Pojo(a);
      Pojo p2 = new Pojo(a);

      Cache<Integer, Object> c1 = cm1.getCache();
      Cache<Integer, Object> c2 = cm2.getCache();
      c1.put(11, a);
      c1.put(1, p1);
      c1.put(2, p2);
      System.out.println("=== Cache Node 1 ===");
      System.out.println(p1);
      System.out.println(p2);

      p1 = (Pojo) c2.get(1);
      p2 = (Pojo) c2.get(2);
      System.out.println("=== Cache Node 2 ===");
      System.out.println(p1);
      System.out.println(p2);
      p1.a.name = "different name"; // Update reflected in both objects in a local cache

      p1 = (Pojo) c2.get(1);
      p2 = (Pojo) c2.get(2);
      System.out.println("=== Cache Node 2 (after update) ===");
      System.out.println(p1);
      System.out.println(p2);
   }

   public static class Pojo {
      A a;
      public Pojo(A a) { this.a = a; }

      @Override
      public String toString() {
         return "P={" + a.toString() + "}" + "@" + Integer.toHexString(hashCode());
      }

      public static class Externalizer0 extends AbstractExternalizer<Pojo> {
         @Override
         public void writeObject(ObjectOutput output, Pojo object) throws IOException {
            output.writeObject(object.a);
         }

         @Override
         public Pojo readObject(ObjectInput input) throws IOException, ClassNotFoundException {
            A a = (A) input.readObject();
            return new Pojo(a);
         }

         @Override
         public Set<Class<? extends Pojo>> getTypeClasses() {
            return Util.<Class<? extends Pojo>>asSet(Pojo.class);
         }
      }
   }

   public static class A {
      int id;
      String name;
      public A(int id, String name) { this.id = id; this.name = name; }

      @Override
      public String toString() {
         return "A{" +
               "id=" + id +
               ", name='" + name + '\'' +
               '}' + "@" + Integer.toHexString(hashCode());
      }

      public static class Externalizer0 extends AbstractExternalizer<A> {
         private final EmbeddedCacheManager cacheManager;

         public Externalizer0(EmbeddedCacheManager cacheManager) {
            this.cacheManager = cacheManager;
         }

         @Override
         public void writeObject(ObjectOutput output, A object) throws IOException {
            output.writeInt(object.id);

            Cache<Integer, Object> cache = cacheManager.getCache();
            Object o = cache.get(object.id);
            if (o == null)
               output.writeUTF(object.name);
         }

         @Override
         public A readObject(ObjectInput input) throws IOException, ClassNotFoundException {
            Cache<Integer, Object> cache = cacheManager.getCache();
            int id = input.readInt();
            Object o = cache.get(id);
            if (o == null) {
               String name = input.readUTF();
               return new A(id, name);
            }

            return (A) o;
         }

         @Override
         public Set<Class<? extends A>> getTypeClasses() {
            return Util.<Class<? extends A>>asSet(A.class);
         }
      }
   }


}
