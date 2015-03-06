package rtlib.slms;

import rtlib.core.device.SignalStartableDevice;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.kam.memory.ndarray.NDArrayTyped;

public abstract class SpatialPhaseModulatorDeviceBase	extends
																											SignalStartableDevice	implements
																																						SpatialPhaseModulatorDeviceInterface
{
	protected DoubleVariable mMatrixWidthVariable;
	protected DoubleVariable mMatrixHeightVariable;
	protected DoubleVariable mActuatorResolutionVariable;
	protected DoubleVariable mNumberOfActuatorsVariable;

	protected ObjectVariable<NDArrayTyped<Double>> mMatrixVariable;

	public SpatialPhaseModulatorDeviceBase(	String pDeviceName,
																					int pFullMatrixWidthHeight,
																					int pActuatorResolution)
	{
		super(pDeviceName);

		mMatrixWidthVariable = new DoubleVariable("MatrixWidth",
																							pFullMatrixWidthHeight);
		mMatrixHeightVariable = new DoubleVariable(	"MatrixHeight",
																								pFullMatrixWidthHeight);
		mActuatorResolutionVariable = new DoubleVariable(	"ActuatorResolution",
																											pActuatorResolution);

	}

	@Override
	public DoubleVariable getMatrixWidthVariable()
	{
		return mMatrixWidthVariable;
	}

	@Override
	public DoubleVariable getMatrixHeightVariable()
	{
		return mMatrixHeightVariable;
	}

	@Override
	public DoubleVariable getActuatorResolutionVariable()
	{
		return mActuatorResolutionVariable;
	}

	@Override
	public DoubleVariable getNumberOfActuatorVariable()
	{
		return mNumberOfActuatorsVariable;
	}

	@Override
	public ObjectVariable<NDArrayTyped<Double>> getMatrixReference()
	{
		return mMatrixVariable;
	}

	@Override
	public abstract void zero();

	@Override
	public abstract long getRelaxationTimeInMilliseconds();

}
