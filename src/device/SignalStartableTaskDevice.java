package device;

import thread.EnhancedThread;
import variable.booleanv.BooleanEventListenerInterface;
import variable.booleanv.BooleanVariable;

public abstract class SignalStartableTaskDevice	extends
																								SignalStartableDevice	implements
																																			VirtualDeviceInterface,
																																			Runnable
{

	private final SignalStartableTaskDevice lThis;

	protected final BooleanVariable mCancelBooleanVariable;
	
	protected volatile boolean mCanceledSignal= false;

	public SignalStartableTaskDevice(final String pDeviceName)
	{
		super(pDeviceName, true);
		lThis = this;

		mCancelBooleanVariable = new BooleanVariable(	pDeviceName + "Cancel",
																									false);
		
		mCancelBooleanVariable.addEdgeListener(new BooleanEventListenerInterface()
		{
			
			@Override
			public void fire(boolean pCurrentBooleanValue)
			{
				if(pCurrentBooleanValue)
				{
					mCanceledSignal = true;
				}
			}
		});
	}

	protected EnhancedThread mTaskThread = new EnhancedThread()
	{
		@Override
		public boolean loop()
		{
			lThis.run();
			return false;
		}
	};

	@Override
	public abstract void run();

	@Override
	public boolean start()
	{
		mTaskThread.start();
		mCancelBooleanVariable.setValue(false);
		return true;
	}

	public boolean pause()
	{
		mTaskThread.pause();
		mTaskThread.waitForPause();
		return true;
	}

	public boolean resume()
	{
		mTaskThread.resume();
		mTaskThread.waitForRunning();
		return true;
	}

	@Override
	public boolean stop()
	{
		mCancelBooleanVariable.setValue(true);
		mTaskThread.stop();
		return true;
	}

	public BooleanVariable getIsCanceledBooleanVariable()
	{
		return mCancelBooleanVariable;
	}

	public void clearCanceled()
	{
		mCanceledSignal=false;
	}
	
	public boolean isCanceled()
	{
		return mCanceledSignal;
	}

}
