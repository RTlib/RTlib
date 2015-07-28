package rtlib.fiberswitch.devices.optojena.demo;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.fiberswitch.devices.optojena.OptoJenaFiberSwitchDevice;
import rtlib.filterwheels.devices.fli.FLIFilterWheelDevice;

public class OptoJenaFiberSwitchDemo
{

	@Test
	public void test() throws InterruptedException
	{
		final OptoJenaFiberSwitchDevice lOptoJenaFiberSwitchDevice = new OptoJenaFiberSwitchDevice("COM25");

		assertTrue(lOptoJenaFiberSwitchDevice.open());

		final DoubleVariable lPositionVariable = lOptoJenaFiberSwitchDevice.getPositionVariable();
		
		for (int i = 0; i < 10; i++)
		{
			int lTargetPosition = 1+i % 4;
			lPositionVariable.set((double) lTargetPosition);
			Thread.sleep(10);
			System.out.format("i=%d, tp=%d\n",
												i,
												lTargetPosition);
		}

		assertTrue(lOptoJenaFiberSwitchDevice.close());

	}

}
