package device;

import thread.EnhancedThread;

public abstract class SignalStartableLoopTaskDevice	extends
																										SignalStartableDevice	implements
																																					VirtualDevice
{

	private SignalStartableLoopTaskDevice lThis;

	public SignalStartableLoopTaskDevice()
	{
		super();
		lThis = this;
	}

	protected EnhancedThread mTaskThread = new EnhancedThread()
	{
		@Override
		public boolean loop()
		{
			return lThis.loop();
		}
	};

	protected abstract boolean loop();



	@Override
	public boolean start()
	{
		mTaskThread.start();
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
		return true;
	}

	@Override
	public boolean stop()
	{
		mTaskThread.stop();
		return true;
	}

}