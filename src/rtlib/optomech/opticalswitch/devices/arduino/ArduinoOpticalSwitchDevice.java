package rtlib.optomech.opticalswitch.devices.arduino;

import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.device.SwitchingDeviceInterface;
import rtlib.core.variable.ObjectVariable;
import rtlib.core.variable.VariableSetListener;
import rtlib.optomech.OptoMechDeviceInterface;
import rtlib.optomech.opticalswitch.devices.arduino.adapters.ArduinoOpticalSwitchPositionAdapter;
import rtlib.serial.SerialDevice;

public class ArduinoOpticalSwitchDevice extends SerialDevice implements
																														SwitchingDeviceInterface,
																														OptoMechDeviceInterface
{

	private final ObjectVariable<Long> mCommandVariable;

	private final ObjectVariable<Boolean>[] mLightSheetOnOff;

	private static final long cAllClosed = 0;
	private static final long cAllOpened = 100;

	public ArduinoOpticalSwitchDevice(final int pDeviceIndex)
	{
		this(MachineConfiguration.getCurrentMachineConfiguration()
															.getSerialDevicePort(	"fiberswitch.optojena",
																										pDeviceIndex,
																										"NULL"));
	}

	public ArduinoOpticalSwitchDevice(final String pPortName)
	{
		super("ArduinoOpticalSwitch", pPortName, 250000);

		final ArduinoOpticalSwitchPositionAdapter lFiberSwitchPosition = new ArduinoOpticalSwitchPositionAdapter(this);

		mCommandVariable = addSerialVariable(	"OpticalSwitchPosition",
																					lFiberSwitchPosition);

		mLightSheetOnOff = new ObjectVariable[4];

		final VariableSetListener<Boolean> lBooleanVariableListener = (	u,
																																		v) -> {

			int lCount = 0;
			for (int i = 0; i < mLightSheetOnOff.length; i++)
				if (mLightSheetOnOff[i].get())
					lCount++;

			if (lCount == 1)
			{
				for (int i = 0; i < mLightSheetOnOff.length; i++)
					if (mLightSheetOnOff[i].get())
						mCommandVariable.set((long) (101 + i));
			}
			else
				for (int i = 0; i < mLightSheetOnOff.length; i++)
				{
					boolean lOn = mLightSheetOnOff[i].get();
					mCommandVariable.set((long) ((i + 1) * (lOn ? 1 : -1)));
				}
		};

		for (int i = 0; i < mLightSheetOnOff.length; i++)
		{

			mLightSheetOnOff[i] = new ObjectVariable<Boolean>(String.format("LightSheet%dOnOff",
																																			i),
																												false);
			mLightSheetOnOff[i].addSetListener(lBooleanVariableListener);

		}

	}

	@Override
	public boolean open()
	{
		final boolean lIsOpened = super.open();
		mCommandVariable.set(cAllClosed);

		return lIsOpened;
	}

	@Override
	public boolean close()
	{
		final boolean lIsClosed = super.close();
		mCommandVariable.set(cAllClosed);

		return lIsClosed;
	}

	@Override
	public int getNumberOfSwitches()
	{
		return 4;
	}

	@Override
	public ObjectVariable<Boolean> getSwitchingVariable(int pSwitchIndex)
	{
		return mLightSheetOnOff[pSwitchIndex];
	}

}
