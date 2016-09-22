package main;


public class FastMath {

	public static void main(String[] args){
//		 double min = -2000;
//	      double max = +2000;
//	      double step = 1f;
//
//	      for (int i = 0; i < 8; i++)
//	      {
//	         long t0A = System.nanoTime() / 1000000L;
//	         double sumA = 0.0f;
//	         for (double y = min; y < max; y += step)
//	            for (double x = min; x < max; x += step)
//	               sumA += atan2(y, x);
//	         long t1A = System.nanoTime() / 1000000L;
//
//	         long t0B = System.nanoTime() / 1000000L;
//	         double sumB = 0.0f;
//	         for (double y = min; y < max; y += step)
//	            for (double x = min; x < max; x += step)
//	               sumB += Math.atan2(y, x);
//	         long t1B = System.nanoTime() / 1000000L;
//
//	         System.out.println();
//	         System.out.println("FastMath: " + (t1A - t0A) + "ms, sum=" + sumA);
//	         System.out.println("JavaMath: " + (t1B - t0B) + "ms, sum=" + sumB);
//	         System.out.println("factor: " + ((double) (t1B - t0B) / (t1A - t0A)));
//	      }
	}
	  private static final int ATAN2_BITS = 7;

	   private static final int ATAN2_BITS2 = ATAN2_BITS << 1;
	   private static final int ATAN2_MASK = ~(-1 << ATAN2_BITS2);
	   private static final int ATAN2_COUNT = ATAN2_MASK + 1;
	   private static final int ATAN2_DIM = (int) Math.sqrt(ATAN2_COUNT);

	   private static final double INV_ATAN2_DIM_MINUS_1 = 1.0f / (ATAN2_DIM - 1);
	   private static final double DEG = 180.0f / (double) Math.PI;

	   private static final double[] atan2 = new double[ATAN2_COUNT];



	   static
	   {
	      for (int i = 0; i < ATAN2_DIM; i++)
	      {
	         for (int j = 0; j < ATAN2_DIM; j++)
	         {
	            double x0 = (double) i / ATAN2_DIM;
	            double y0 = (double) j / ATAN2_DIM;

	            atan2[j * ATAN2_DIM + i] = (double) Math.atan2(y0, x0);
	         }
	      }
	   }


	   /**
	    * ATAN2
	    */

	   public static final double atan2Deg(double y, double x)
	   {
	      return atan2(y, x) * DEG;
	   }

	   public static final double atan2DegStrict(double y, double x)
	   {
	      return (double) Math.atan2(y, x) * DEG;
	   }

	   public static final double atan2(double y, double x)
	   {
	      double add, mul;

	      if (x < 0.0f)
	      {
	         if (y < 0.0f)
	         {
	            x = -x;
	            y = -y;

	            mul = 1.0f;
	         }
	         else
	         {
	            x = -x;
	            mul = -1.0f;
	         }

	         add = -3.141592653f;
	      }
	      else
	      {
	         if (y < 0.0f)
	         {
	            y = -y;
	            mul = -1.0f;
	         }
	         else
	         {
	            mul = 1.0f;
	         }

	         add = 0.0f;
	      }

	      double invDiv = 1.0f / (((x < y) ? y : x) * INV_ATAN2_DIM_MINUS_1);

	      int xi = (int) (x * invDiv);
	      int yi = (int) (y * invDiv);

	      return (atan2[yi * ATAN2_DIM + xi] + add) * mul;
	   }
	   
	   
	   
	// Math constants


}
