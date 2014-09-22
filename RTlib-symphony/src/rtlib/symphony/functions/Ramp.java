package rtlib.symphony.functions;

import static java.lang.Math.round;
import rtlib.symphony.interfaces.StaveInterface;

public class Ramp
{

	public static void write(	final StaveInterface pStave,
														final double pSyncStart,
														final double pSyncStop,
														final double pStartValue,
														final double pStopValue,
														final double pValueOutside)
	{
		final int lArrayLength = pStave.getNumberOfTimePoints();
		final short[] array = pStave.getStaveArray();

		final int lStartInteger = pStave.getTimePointFromNormalized(pSyncStart);
		final int lStopInteger = pStave.getTimePointFromNormalized(pSyncStop);
		final int lRampLength = lStopInteger - lStartInteger;

		final int lMaxIntegerAmplitude = pStave.getMaximalSignalIntegerAmplitude();

		final short lValueOutsideShort = (short) round(lMaxIntegerAmplitude * pValueOutside);

		if (lValueOutsideShort != 0)
		{
			for (int i = 0; i < lStartInteger; i++)
			{
				array[i] += lValueOutsideShort;
			}
		}

		for (int i = lStartInteger; i < lStopInteger; i++)
		{
			final double lValue = pStartValue + (i - lStartInteger)
														* (pStopValue - pStartValue)
														/ lRampLength;
			final short lValueShort = Utils.clampToShort(round(lMaxIntegerAmplitude * lValue));
			array[i] += lValueShort;
		}

		if (lValueOutsideShort != 0)
		{
			for (int i = lStopInteger; i < lArrayLength; i++)
			{
				array[i] += lValueOutsideShort;
			}
		}

	}

}