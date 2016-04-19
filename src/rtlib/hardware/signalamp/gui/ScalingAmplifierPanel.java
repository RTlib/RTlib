package rtlib.hardware.signalamp.gui;

import rtlib.gui.jfx.sliderpanel.SliderPanel;
import rtlib.hardware.signalamp.ScalingAmplifierDeviceInterface;

public class ScalingAmplifierPanel extends SliderPanel
{

	public ScalingAmplifierPanel(ScalingAmplifierDeviceInterface pScalingAmplifierInterface)
	{
		super();

		double lMinGain = pScalingAmplifierInterface.getMinGain();
		double lMaxGain = pScalingAmplifierInterface.getMaxGain();

		addSliderForVariable(	pScalingAmplifierInterface.getGainVariable(),
													lMinGain,
													lMaxGain,
													0.1 * (lMaxGain - lMinGain));

		double lMinOffset = pScalingAmplifierInterface.getMinOffset();
		double lMaxOffset = pScalingAmplifierInterface.getMaxOffset();

		addSliderForVariable(	pScalingAmplifierInterface.getOffsetVariable(),
													lMinOffset,
													lMaxOffset,
													0.1 * (lMaxOffset - lMinOffset));
	}

}