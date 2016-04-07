package rtlib.stages.hub;

import java.util.concurrent.TimeUnit;

import rtlib.core.variable.ObjectVariable;
import rtlib.stages.StageDeviceInterface;

public class StageDeviceDOF
{
	private StageDeviceInterface mStageDeviceInterface;
	private int mDOFIndex;

	public StageDeviceDOF(StageDeviceInterface pStageDeviceInterface,
												int pDOFIndex)
	{
		super();
		setStageDeviceInterface(pStageDeviceInterface);
		setDOFIndex(pDOFIndex);
	}

	public StageDeviceInterface getStageDeviceInterface()
	{
		return mStageDeviceInterface;
	}

	public void setStageDeviceInterface(StageDeviceInterface pStageDeviceInterface)
	{
		mStageDeviceInterface = pStageDeviceInterface;
	}

	public int getDOFIndex()
	{
		return mDOFIndex;
	}

	public void setDOFIndex(int pDOFIndex)
	{
		mDOFIndex = pDOFIndex;
	}

	public String getName()
	{
		return mStageDeviceInterface.getDOFNameByIndex(mDOFIndex);
	}

	public void reset()
	{
		mStageDeviceInterface.reset(mDOFIndex);
	}

	public void home()
	{
		mStageDeviceInterface.home(mDOFIndex);
	}

	public void enable()
	{
		mStageDeviceInterface.enable(mDOFIndex);
	}

	public double getCurrentPosition()
	{
		return mStageDeviceInterface.getCurrentPosition(mDOFIndex);
	}

	public void goToPosition(double pValue)
	{
		mStageDeviceInterface.goToPosition(mDOFIndex, pValue);
	}

	public Boolean waitToBeReady(int pTimeOut, TimeUnit pTimeUnit)
	{
		return mStageDeviceInterface.waitToBeReady(	mDOFIndex,
																								pTimeOut,
																								pTimeUnit);
	}

	public ObjectVariable<Double> getMinPositionVariable()
	{
		return mStageDeviceInterface.getMinPositionVariable(mDOFIndex);
	}

	public ObjectVariable<Double> getMaxPositionVariable()
	{
		return mStageDeviceInterface.getMaxPositionVariable(mDOFIndex);
	}

	public ObjectVariable<Boolean> getEnableVariable()
	{
		return mStageDeviceInterface.getEnableVariable(mDOFIndex);
	}

	public ObjectVariable<Double> getPositionVariable()
	{
		return mStageDeviceInterface.getPositionVariable(mDOFIndex);
	}

	public ObjectVariable<Boolean> getReadyVariable()
	{
		return mStageDeviceInterface.getReadyVariable(mDOFIndex);
	}

	public ObjectVariable<Boolean> getHomingVariable()
	{
		return mStageDeviceInterface.getHomingVariable(mDOFIndex);
	}

	public ObjectVariable<Boolean> getStopVariable()
	{
		return mStageDeviceInterface.getStopVariable(mDOFIndex);
	}

	@Override
	public String toString()
	{
		return "StageDeviceDOF [mStageDeviceInterface=" + mStageDeviceInterface
						+ ", mDOFIndex="
						+ mDOFIndex
						+ "]";
	}

}
