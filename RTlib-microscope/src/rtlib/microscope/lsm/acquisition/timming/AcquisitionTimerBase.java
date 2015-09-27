package rtlib.microscope.lsm.acquisition.timming;

import java.util.concurrent.TimeUnit;

import rtlib.core.concurrent.timing.Waiting;

public class AcquisitionTimerBase	implements
																	AcquisitionTimerInterface,
																	Waiting
{

	private volatile long mLastAcquisitionTimeInNS;
	private long mAcquisitionIntervalInNS;

	@Override
	public long getLastAcquisitionTime(TimeUnit pTimeUnit)
	{
		return pTimeUnit.convert(	mLastAcquisitionTimeInNS,
															TimeUnit.NANOSECONDS);
	}

	public void setLastAcquisitionTime(	long pLastAcquisitionTime,
																			TimeUnit pTimeUnit)
	{
		mLastAcquisitionTimeInNS = TimeUnit.NANOSECONDS.convert(pLastAcquisitionTime,
																														pTimeUnit);
	}

	public long getAcquisitionInterval(TimeUnit pTimeUnit)
	{
		return pTimeUnit.convert(	mAcquisitionIntervalInNS,
															TimeUnit.NANOSECONDS);
	}

	public void setAcquisitionInterval(	long pAcquisitionInterval,
																			TimeUnit pTimeUnit)
	{
		mAcquisitionIntervalInNS = TimeUnit.NANOSECONDS.convert(pAcquisitionInterval,
																														pTimeUnit);
	}

	@Override
	public long timeLeftBeforeNextTimePoint(TimeUnit pTimeUnit)
	{
		long lTimeLeftBeforeNextTimePointInNS = (getLastAcquisitionTime(TimeUnit.NANOSECONDS) + getAcquisitionInterval(TimeUnit.NANOSECONDS)) - System.nanoTime();
		return pTimeUnit.convert(	lTimeLeftBeforeNextTimePointInNS,
															TimeUnit.NANOSECONDS);
	}

	@Override
	public boolean enoughTimeFor(	long pTimeNeeded,
																long pReservedTime,
																TimeUnit pTimeUnit)
	{
		if (pTimeNeeded < 0)
			return false;

		long lTimeNeededInNS = TimeUnit.NANOSECONDS.convert(pTimeNeeded,
																												pTimeUnit);
		long lReservedTimeInNS = TimeUnit.NANOSECONDS.convert(pReservedTime,
																													pTimeUnit);
		long lTimeLeftNoReserveInNS = timeLeftBeforeNextTimePoint(TimeUnit.NANOSECONDS);

		long lTimeLeftInNS = lTimeLeftNoReserveInNS - lReservedTimeInNS;

		boolean lEnoughTime = lTimeLeftInNS > lTimeNeededInNS;

		return lEnoughTime;
	}

	@Override
	public void waitToAcquire()
	{
		waitFor(() -> timeLeftBeforeNextTimePoint(TimeUnit.NANOSECONDS) <= 0);
	}

	@Override
	public void notifyAcquisition(long pTimeStamp)
	{
		setLastAcquisitionTime(pTimeStamp, TimeUnit.NANOSECONDS);
	}

}
