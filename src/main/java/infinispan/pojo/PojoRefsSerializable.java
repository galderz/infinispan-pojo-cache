package infinispan.pojo;

import org.infinispan.Cache;
import org.infinispan.manager.EmbeddedCacheManager;

import java.io.Serializable;

public class PojoRefsSerializable {

   public static void execute(EmbeddedCacheManager cm1, EmbeddedCacheManager cm2) {
      A a = new A(10);
      Pojo p1 = new Pojo(a);
      Pojo p2 = new Pojo(a);

      Cache<Integer, Pojo> c1 = cm1.getCache();
      Cache<Integer, Pojo> c2 = cm2.getCache();
      c1.put(1, p1);
      c1.put(2, p2);
      System.out.println("=== Cache Node 1 ===");
      System.out.println(p1);
      System.out.println(p2);

      p1 = c2.get(1);
      p2 = c2.get(2);
      System.out.println("=== Cache Node 2 ===");
      System.out.println(p1);
      System.out.println(p2);
      p1.a.age = 20; // Update reflected in both objects in a local cache

      System.out.println("=== Cache Node 2 (after update) ===");
      p1 = c2.get(1);
      p2 = c2.get(2);
      System.out.println(p1);
      System.out.println(p2);
   }

   public static class Pojo implements Serializable {
      A a;
      public Pojo(A a) { this.a = a; }

      @Override
      public String toString() {
         return "P={" + a.toString() + "}" + "@" + Integer.toHexString(hashCode());
      }
   }

   public static class A implements Serializable {
      int age;
      public A(int age) { this.age = age; }
      @Override public String toString() { return "A=" + age + "@" + Integer.toHexString(hashCode()); }
   }

}
