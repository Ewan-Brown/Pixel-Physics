package main;


public class FastMath {
	
	private static final int ATAN2_BITS = 7;
	private static final int ATAN2_BITS2 = ATAN2_BITS << 1;
	private static final int ATAN2_MASK = ~(-1 << ATAN2_BITS2);
	private static final int ATAN2_COUNT = ATAN2_MASK + 1;
	private static final double[] atan2 = new double[ATAN2_COUNT];
	private static final int ATAN2_DIM = (int) Math.sqrt(ATAN2_COUNT);


	private static final double DEG = 180.0f / Math.PI;
	private static final double INV_ATAN2_DIM_MINUS_1 = 1.0f / (ATAN2_DIM - 1);

	static
	{
		for (int i = 0; i < ATAN2_DIM; i++)
			for (int j = 0; j < ATAN2_DIM; j++)
			{
				final double x0 = (double) i / ATAN2_DIM;
				final double y0 = (double) j / ATAN2_DIM;

				atan2[j * ATAN2_DIM + i] = Math.atan2(y0, x0);
			}
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
			} else
				mul = 1.0f;

			add = 0.0f;
		}

		final double invDiv = 1.0f / ((x < y ? y : x) * INV_ATAN2_DIM_MINUS_1);

		final int xi = (int) (x * invDiv);
		final int yi = (int) (y * invDiv);

		return (atan2[yi * ATAN2_DIM + xi] + add) * mul;
	}


	/**
	 * ATAN2
	 */

	 public static final double atan2Deg(final double y, final double x)
	{
		return atan2(y, x) * DEG;
	}

	public static final double atan2DegStrict(final double y, final double x)
	{
		return Math.atan2(y, x) * DEG;
	}




	// Math constants


}
