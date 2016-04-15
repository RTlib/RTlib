package rtlib.microscope.lsm.gui;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import rtlib.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.device.NamedVirtualDevice;
import rtlib.core.variable.Variable;
import rtlib.gui.video.video2d.Stack2DDisplay;
import rtlib.gui.video.video3d.Stack3DDisplay;
import rtlib.hardware.cameras.StackCameraDeviceInterface;
import rtlib.microscope.lsm.LightSheetMicroscope;
import rtlib.microscope.lsm.gui.halcyon.HalcyonGUI;
import rtlib.stack.StackInterface;

public class LightSheetMicroscopeGUI extends NamedVirtualDevice	implements
																																AsynchronousExecutorServiceAccess
{

	private static final int cDefaultWindowWidth = 512;
	private static final int cDefaultWindowHeight = 512;

	private final LightSheetMicroscope mLightSheetMicroscope;

	private ArrayList<Stack2DDisplay> mStack2DVideoDeviceList = new ArrayList<>();
	private Stack3DDisplay<UnsignedShortType, ShortOffHeapAccess> mStack3DVideoDevice;
	private Variable<StackInterface>[] mCleanupStackVariable;
	private final boolean m3dView;
	private HalcyonGUI mHalcyonMicroscopeGUI;

	public LightSheetMicroscopeGUI(	LightSheetMicroscope pLightSheetMicroscope,
																	boolean p3DView)
	{
		super(pLightSheetMicroscope.getName() + "GUI");
		mLightSheetMicroscope = pLightSheetMicroscope;
		m3dView = p3DView;

		final MachineConfiguration lCurrentMachineConfiguration = MachineConfiguration.getCurrentMachineConfiguration();

		setup2D3DDisplay();

		setupScripting(	pLightSheetMicroscope,
										lCurrentMachineConfiguration);

		setupHalcyonWindow(mLightSheetMicroscope);

	}

	@SuppressWarnings("unchecked")
	public void setup2D3DDisplay()
	{
		final int lNumberOfCameras = mLightSheetMicroscope.getDeviceLists()
																											.getNumberOfStackCameraDevices();

		mCleanupStackVariable = new Variable[lNumberOfCameras];

		for (int i = 0; i < lNumberOfCameras; i++)
		{

			mCleanupStackVariable[i] = new Variable<StackInterface>("CleanupStackVariable",
																															null)
			{
				ConcurrentLinkedQueue<StackInterface> mKeepStacksAliveQueue = new ConcurrentLinkedQueue<>();

				@Override
				public StackInterface setEventHook(	StackInterface pOldValue,
																						StackInterface pNewValue)
				{
					if (pOldValue != null && !pOldValue.isReleased())
						mKeepStacksAliveQueue.add(pOldValue);

					if (mKeepStacksAliveQueue.size() > lNumberOfCameras)
					{
						StackInterface lStackToRelease = mKeepStacksAliveQueue.remove();
						lStackToRelease.release();
					}
					return pNewValue;
				}
			};

			final StackCameraDeviceInterface lStackCameraDevice = mLightSheetMicroscope.getDeviceLists()
																																									.getStackCameraDevice(i);

			final Stack2DDisplay lStack2DDisplay = new Stack2DDisplay("Video 2D - " + lStackCameraDevice.getName(),
																																cDefaultWindowWidth,
																																cDefaultWindowHeight);
			mStack2DVideoDeviceList.add(lStack2DDisplay);

		}

		if (m3dView)
		{
			final Stack3DDisplay<UnsignedShortType, ShortOffHeapAccess> lStack3DDisplay = new Stack3DDisplay<UnsignedShortType, ShortOffHeapAccess>("Video 3D",
																																																																							new UnsignedShortType(),
																																																																							cDefaultWindowWidth,
																																																																							cDefaultWindowHeight,
																																																																							1, // FIX
																																																																							10);
			mStack3DVideoDevice = lStack3DDisplay;
		}
		else
			mStack3DVideoDevice = null;

	}

	public void setupScripting(	LightSheetMicroscope pLightSheetMicroscope,
															final MachineConfiguration lCurrentMachineConfiguration)
	{

	}

	private void setupHalcyonWindow(LightSheetMicroscope pLightSheetMicroscope)
	{
		mHalcyonMicroscopeGUI = new HalcyonGUI(pLightSheetMicroscope);
	}

	@Override
	public boolean open()
	{
		try
		{
			mHalcyonMicroscopeGUI.externalStart();
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}

		executeAsynchronously(() -> {
			for (final Stack2DDisplay lStack2dDisplay : mStack2DVideoDeviceList)
			{
				lStack2dDisplay.open();
			}
		});

		executeAsynchronously(() -> {
			if (m3dView)
				mStack3DVideoDevice.open();
		});

		return super.open();
	}

	@Override
	public boolean close()
	{

		executeAsynchronously(() -> {
			if (m3dView)
				mStack3DVideoDevice.close();
		});

		executeAsynchronously(() -> {
			for (final Stack2DDisplay lStack2dDisplay : mStack2DVideoDeviceList)
			{
				lStack2dDisplay.close();
			}
		});

		executeAsynchronously(() -> {
			try
			{
				mHalcyonMicroscopeGUI.externalStop();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		});

		return super.close();
	}

	public void connectGUI()
	{

		final int lNumberOfCameras = mLightSheetMicroscope.getDeviceLists()
																											.getNumberOfStackCameraDevices();

		for (int i = 0; i < lNumberOfCameras; i++)
		{
			final Stack2DDisplay lStack2DDisplay = mStack2DVideoDeviceList.get(i);

			mLightSheetMicroscope.getDeviceLists()
														.getStackVariable(i)
														.sendUpdatesTo(lStack2DDisplay.getInputStackVariable());

			if (m3dView)
			{
				lStack2DDisplay.setOutputStackVariable(mStack3DVideoDevice.getStackInputVariable());
				mStack3DVideoDevice.setOutputStackVariable(mCleanupStackVariable[i]);
			}
			else
				lStack2DDisplay.setOutputStackVariable(mCleanupStackVariable[i]);
		}

	}

	public void disconnectGUI()
	{
		final int lNumberOfCameras = mLightSheetMicroscope.getDeviceLists()
																											.getNumberOfStackCameraDevices();

		for (int i = 0; i < lNumberOfCameras; i++)
		{

			final Stack2DDisplay lStack2DDisplay = mStack2DVideoDeviceList.get(i);

			mLightSheetMicroscope.getDeviceLists()
														.getStackVariable(i)
														.doNotSendUpdatesTo(lStack2DDisplay.getInputStackVariable());
			if (m3dView)
			{
				lStack2DDisplay.setOutputStackVariable(null);
				mStack3DVideoDevice.setOutputStackVariable(null);
			}
			else
				lStack2DDisplay.setOutputStackVariable(null);

		}
	}

	public boolean isVisible()
	{
		return mHalcyonMicroscopeGUI.isVisible();
	}

}
