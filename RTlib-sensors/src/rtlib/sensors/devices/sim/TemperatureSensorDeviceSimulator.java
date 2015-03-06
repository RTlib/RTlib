package rtlib.sensors.devices.sim;

import java.util.concurrent.ThreadLocalRandom;

import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.sensors.TemperatureSensorDeviceBase;
import rtlib.sensors.TemperatureSensorDeviceInterface;

public class TemperatureSensorDeviceSimulator	extends
																							TemperatureSensorDeviceBase	implements
																																					TemperatureSensorDeviceInterface
{

	public TemperatureSensorDeviceSimulator(String pDeviceName)
	{
		super(pDeviceName);
	}

	@Override
	protected boolean loop()
	{
		final DoubleVariable lTemperatureInCelciusVariable = getTemperatureInCelciusVariable();
		final ThreadLocalRandom lThreadLocalRandom = ThreadLocalRandom.current();
		final double lTemperatureInCelcius = 24 + lThreadLocalRandom.nextDouble();
		lTemperatureInCelciusVariable.setValue(lTemperatureInCelcius);

		return true;
	}

}
